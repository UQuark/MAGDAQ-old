package com.github.uquark0.magdaq.gui;

import com.github.uquark0.magdaq.Main;
import com.github.uquark0.magdaq.block.entity.TradingTerminalBlockEntity;
import com.github.uquark0.magdaq.economy.Transaction;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class TradingTerminalScreenHandler extends ScreenHandler {
    public static final Identifier C2S_GET_STOCKS = new Identifier(Main.MODID, "c2s_get_stocks");
    public static final Identifier C2S_GET_PRINTS = new Identifier(Main.MODID, "c2s_get_prints");
    public static final Identifier C2S_UNREGISTER_HANDLER = new Identifier(Main.MODID, "c2s_unregister_handler");
    public static final Identifier S2C_RECEIVE_STOCKS = new Identifier(Main.MODID, "s2c_receive_stocks");
    public static final Identifier S2C_RECEIVE_PRINTS = new Identifier(Main.MODID, "s2c_receive_prints");

    public static final HashMap<Integer, TradingTerminalScreenHandler> clientHandlers = new HashMap<>();
    public static final HashMap<Integer, TradingTerminalScreenHandler> serverHandlers = new HashMap<>();

    public int[] stocks;
    public Prints prints;

    private final TradingTerminalBlockEntity owner;

    public static class Prints {
        public final long[] prices;
        public final int[] amounts;
        public final int count;

        public Prints(int count) {
            prices = new long[count];
            amounts = new int[count];
            this.count = count;
        }

        public Prints(long[] prices, int[] amounts) {
            if (prices.length != amounts.length)
                throw new IllegalArgumentException("Prices count and amounts count are not equal");
            this.prices = prices;
            this.amounts = amounts;
            this.count = prices.length;
        }
    }

    public TradingTerminalScreenHandler(int syncId, TradingTerminalBlockEntity owner) {
        super(ScreenHandlerTypeManager.TRADING_TERMINAL_SCREEN_HANDLER_TYPE, syncId);
        this.owner = owner;
        register();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public int[] getStocks() {
        if (owner == null) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(syncId);
            ClientSidePacketRegistry.INSTANCE.sendToServer(C2S_GET_STOCKS, buf);
            return null;
        } else {
            Item[] stocks = owner.getStocks();
            int[] ids = new int[stocks.length];

            for (int i = 0; i < stocks.length; i++)
                ids[i] = Registry.ITEM.getRawId(stocks[i]);

            return ids;
        }
    }

    public Prints getPrints(int item) {
        if (owner == null) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(syncId);
            buf.writeInt(item);
            ClientSidePacketRegistry.INSTANCE.sendToServer(C2S_GET_PRINTS, buf);
            return null;
        } else {
            Transaction[] t = owner.getPrints(Registry.ITEM.get(item));
            Prints p = new Prints(t.length);
            for (int i = 0; i < t.length; i++) {
                p.prices[i] = t[i].price.amount;
                p.amounts[i] = t[i].amount;
            }
            return p;
        }
    }
    
    public static void registerC2SPackets() {
        ServerSidePacketRegistry.INSTANCE.register(C2S_GET_STOCKS, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = serverHandlers.get(syncId);
            if (handler == null)
                return;

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(syncId);
            buf.writeIntArray(handler.getStocks());
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(packetContext.getPlayer(), S2C_RECEIVE_STOCKS, buf);
        });

        ServerSidePacketRegistry.INSTANCE.register(C2S_GET_PRINTS, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = serverHandlers.get(syncId);
            if (handler == null)
                return;

            Prints p = handler.getPrints(packetByteBuf.readInt());
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(syncId);
            buf.writeLongArray(p.prices);
            buf.writeIntArray(p.amounts);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(packetContext.getPlayer(), S2C_RECEIVE_PRINTS, buf);
        });

        ServerSidePacketRegistry.INSTANCE.register(C2S_UNREGISTER_HANDLER, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            serverHandlers.remove(syncId);
        });
    }

    public void register() {
        if (owner == null)
            clientHandlers.put(syncId, this);
        else
            serverHandlers.put(syncId, this);
    }

    public void unregister() {
        clientHandlers.remove(syncId);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(syncId);
        ClientSidePacketRegistry.INSTANCE.sendToServer(C2S_UNREGISTER_HANDLER, buf);
    }

    public static void registerS2CPackets() {
        ClientSidePacketRegistry.INSTANCE.register(S2C_RECEIVE_STOCKS, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = clientHandlers.get(syncId);
            if (handler == null)
                return;

            handler.stocks = packetByteBuf.readIntArray();
        });

        ClientSidePacketRegistry.INSTANCE.register(S2C_RECEIVE_PRINTS, (packetContext, packetByteBuf) -> {
            int syncId = packetByteBuf.readInt();
            TradingTerminalScreenHandler handler = clientHandlers.get(syncId);
            if (handler == null)
                return;

            long[] prices = packetByteBuf.readLongArray(null);
            int[] amounts = packetByteBuf.readIntArray();
            handler.prints = new Prints(prices, amounts);
        });
    }
}
