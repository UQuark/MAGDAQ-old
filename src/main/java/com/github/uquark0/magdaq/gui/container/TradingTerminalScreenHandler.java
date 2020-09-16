package com.github.uquark0.magdaq.gui.container;

import com.github.uquark0.magdaq.Main;
import com.github.uquark0.magdaq.block.TradingTerminalBlock;
import com.github.uquark0.magdaq.block.entity.TradingTerminalBlockEntity;
import com.github.uquark0.magdaq.economy.Quotation;
import com.github.uquark0.magdaq.economy.Transaction;
import com.github.uquark0.magdaq.economy.Subscriber;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TradingTerminalScreenHandler extends ScreenHandler implements Subscriber {
    public static ScreenHandlerType<TradingTerminalScreenHandler> TRADING_TERMINAL_SCREEN_HANDLER_TYPE;
    public static void register() {
        TRADING_TERMINAL_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(
                TradingTerminalBlock.ID,
                (i, playerInventory) -> new TradingTerminalScreenHandler(i, null)
        );
    }

    private static final HashMap<Integer, TradingTerminalScreenHandler> serverHandlers = new HashMap<>();
    private static TradingTerminalScreenHandler clientHandler;

    private static void link(int syncId, TradingTerminalScreenHandler handler) {
        serverHandlers.put(syncId, handler);
    }

    private static void unlink(int syncId) {
        serverHandlers.remove(syncId);
    }

    private static final Identifier C2S_REQUEST_STOCKS = new Identifier(Main.MODID, "c2s_request_stocks");
    private static final Identifier C2S_REQUEST_TRANSACTIONS = new Identifier(Main.MODID, "c2s_request_transactions");
    private static final Identifier C2S_SUBSCRIBE = new Identifier(Main.MODID, "c2s_subscribe");
    private static final Identifier C2S_UNSUBSCRIBE = new Identifier(Main.MODID, "c2s_unsubscribe");
    private static final Identifier C2S_UNLINK = new Identifier(Main.MODID, "c2s_unlink");
    private static final Identifier C2S_REQUEST_QUOTATION = new Identifier(Main.MODID, "c2s_request_quotation");

    private static final Identifier S2C_RETURN_STOCKS = new Identifier(Main.MODID, "s2c_return_stocks");
    private static final Identifier S2C_RETURN_TRANSACTIONS = new Identifier(Main.MODID, "s2c_return_transactions");
    private static final Identifier S2C_NOTIFY_TRANSACTION = new Identifier(Main.MODID, "s2c_notify_transaction");
    private static final Identifier S2C_NOTIFY_QUOTATION = new Identifier(Main.MODID, "s2c_notify_quotation");

    public static void registerC2SPackets() {
        ServerSidePacketRegistry.INSTANCE.register(C2S_REQUEST_STOCKS, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = serverHandlers.get(syncId);
            List<Item> items = handler.owner.getStocks();
            int[] ids = new int[items.size()];
            for (int i = 0; i < items.size(); i++)
                ids[i] = Registry.ITEM.getRawId(items.get(i));
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeIntArray(ids);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(packetContext.getPlayer(), S2C_RETURN_STOCKS, buf);
        });

        ServerSidePacketRegistry.INSTANCE.register(C2S_REQUEST_TRANSACTIONS, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = serverHandlers.get(syncId);
            int stock = packetByteBuf.readInt();
            List<Transaction> transactions = handler.owner.getTransactions(Registry.ITEM.get(stock));
            long[] prices = new long[transactions.size()];
            int[] amounts = new int[transactions.size()];
            int[] stocks = new int[transactions.size()];
            for (int i = 0; i < transactions.size(); i++) {
                Transaction.RawInfo rawInfo = new Transaction.RawInfo(transactions.get(i));
                prices[i] = rawInfo.price;
                amounts[i] = rawInfo.amount;
                stocks[i] = rawInfo.stock;
            }
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(transactions.size());
            buf.writeLongArray(prices);
            buf.writeIntArray(amounts);
            buf.writeIntArray(stocks);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(packetContext.getPlayer(), S2C_RETURN_TRANSACTIONS, buf);
        });

        ServerSidePacketRegistry.INSTANCE.register(C2S_SUBSCRIBE, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = serverHandlers.get(syncId);
            int stock = packetByteBuf.readInt();
            handler.owner.subscribe(Registry.ITEM.get(stock), handler);
            handler.subscriber = packetContext.getPlayer();
        });

        ServerSidePacketRegistry.INSTANCE.register(C2S_UNSUBSCRIBE, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = serverHandlers.get(syncId);
            int stock = packetByteBuf.readInt();
            handler.owner.unsubscribe(Registry.ITEM.get(stock), handler);
            handler.subscriber = null;
        });

        ServerSidePacketRegistry.INSTANCE.register(C2S_UNLINK, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = serverHandlers.get(syncId);
            handler.owner.unsubscribe(handler);
            unlink(syncId);
        });

        ServerSidePacketRegistry.INSTANCE.register(C2S_REQUEST_QUOTATION, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = serverHandlers.get(syncId);
            int stock = packetByteBuf.readInt();
            Quotation.RawInfo rawInfo = new Quotation.RawInfo(handler.owner.getQuotation(Registry.ITEM.get(stock)));
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeLongArray(rawInfo.bidPrices);
            buf.writeIntArray(rawInfo.bidAmounts);
            buf.writeLongArray(rawInfo.askPrices);
            buf.writeIntArray(rawInfo.askAmounts);
            buf.writeLong(rawInfo.spread);
            buf.writeInt(rawInfo.stock);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(packetContext.getPlayer(), S2C_NOTIFY_QUOTATION, buf);
        });
    }

    public static void registerS2CPackets() {
        ClientSidePacketRegistry.INSTANCE.register(S2C_RETURN_STOCKS, (packetContext, packetByteBuf) -> {
            int[] ids = packetByteBuf.readIntArray();
            List<Item> stocks = new ArrayList<>();
            for (int id : ids) {
                stocks.add(Registry.ITEM.get(id));
            }
            clientHandler.stocks = stocks;
        });

        ClientSidePacketRegistry.INSTANCE.register(S2C_RETURN_TRANSACTIONS, (packetContext, packetByteBuf) -> {
            int size = packetByteBuf.readInt();
            long[] prices = packetByteBuf.readLongArray(null);
            int[] amounts = packetByteBuf.readIntArray();
            int[] stocks = packetByteBuf.readIntArray();
            List<Transaction> transactions = new ArrayList<>();
            for (int i = 0; i < size; i++)
                transactions.add(new Transaction(new Transaction.RawInfo(prices[i], amounts[i], stocks[i])));
            clientHandler.transactions = transactions;
        });

        ClientSidePacketRegistry.INSTANCE.register(S2C_NOTIFY_TRANSACTION, (packetContext, packetByteBuf) -> {
            long price = packetByteBuf.readLong();
            int amount = packetByteBuf.readInt();
            int stock = packetByteBuf.readInt();
            clientHandler.appendTransaction(new Transaction(new Transaction.RawInfo(price, amount, stock)));
        });

        ClientSidePacketRegistry.INSTANCE.register(S2C_NOTIFY_QUOTATION, (packetContext, packetByteBuf) -> {
            long[] bidPrices = packetByteBuf.readLongArray(null);
            int[] bidAmounts = packetByteBuf.readIntArray();
            long[] askPrices = packetByteBuf.readLongArray(null);
            int[] askAmounts = packetByteBuf.readIntArray();
            long spread = packetByteBuf.readLong();
            int stock = packetByteBuf.readInt();
            clientHandler.updateQuotation(new Quotation(new Quotation.RawInfo(bidPrices, bidAmounts, askPrices, askAmounts, spread, stock)));
        });
    }

    private final TradingTerminalBlockEntity owner;
    private boolean refreshTransactions;
    private boolean refreshQuotation;

    private PlayerEntity subscriber;

    public List<Item> stocks;
    public List<Transaction> transactions;
    public Quotation quotation;

    public TradingTerminalScreenHandler(int syncId, TradingTerminalBlockEntity owner) {
        super(TRADING_TERMINAL_SCREEN_HANDLER_TYPE, syncId);
        this.owner = owner;
        if (owner == null)
            clientHandler = this;
        else
            link(syncId, this);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public void requestStocks() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(syncId);
        ClientSidePacketRegistry.INSTANCE.sendToServer(C2S_REQUEST_STOCKS, buf);
    }

    public void requestTransactions(Item stock) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(syncId);
        buf.writeInt(Registry.ITEM.getRawId(stock));
        ClientSidePacketRegistry.INSTANCE.sendToServer(C2S_REQUEST_TRANSACTIONS, buf);
    }

    public void subscribe(Item stock) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(syncId);
        buf.writeInt(Registry.ITEM.getRawId(stock));
        ClientSidePacketRegistry.INSTANCE.sendToServer(C2S_SUBSCRIBE, buf);
    }

    public void unsubscribe(Item stock) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(syncId);
        buf.writeInt(Registry.ITEM.getRawId(stock));
        ClientSidePacketRegistry.INSTANCE.sendToServer(C2S_UNSUBSCRIBE, buf);
    }

    public void onClose() {
        clientHandler = null;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(syncId);
        ClientSidePacketRegistry.INSTANCE.sendToServer(C2S_UNLINK, buf);
    }

    public void appendTransaction(Transaction t) {
        transactions.add(t);
        refreshTransactions = true;
    }

    public void updateQuotation(Quotation q) {
        quotation = q;
        refreshQuotation = true;
    }

    public boolean popRefreshTransactions() {
        if (refreshTransactions) {
            refreshTransactions = false;
            return true;
        }
        return false;
    }

    public boolean popRefreshQuotation() {
        if (refreshQuotation) {
            refreshQuotation = false;
            return true;
        }
        return false;
    }

    public void requestQuotation(Item stock) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(syncId);
        buf.writeInt(Registry.ITEM.getRawId(stock));
        ClientSidePacketRegistry.INSTANCE.sendToServer(C2S_REQUEST_QUOTATION, buf);
    }

    @Override
    public void notifyTransaction(Transaction t) {
        Transaction.RawInfo rawInfo = new Transaction.RawInfo(t);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeLong(rawInfo.price);
        buf.writeInt(rawInfo.amount);
        buf.writeInt(rawInfo.stock);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(subscriber, S2C_NOTIFY_TRANSACTION, buf);
    }

    @Override
    public void notifyQuotation(Quotation q) {
        Quotation.RawInfo rawInfo = new Quotation.RawInfo(q);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeLongArray(rawInfo.bidPrices);
        buf.writeIntArray(rawInfo.bidAmounts);
        buf.writeLongArray(rawInfo.askPrices);
        buf.writeIntArray(rawInfo.askAmounts);
        buf.writeLong(rawInfo.spread);
        buf.writeInt(rawInfo.stock);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(subscriber, S2C_NOTIFY_QUOTATION, buf);
    }
}
