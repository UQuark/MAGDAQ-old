package com.github.uquark0.magdaq.block.entity;

import com.github.uquark0.magdaq.Main;
import com.github.uquark0.magdaq.economy.Transaction;
import com.github.uquark0.magdaq.economy.order.Subscriber;
import com.github.uquark0.magdaq.gui.TradingTerminalScreenHandler;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TradingTerminalBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Subscriber {
    private final HashMap<Item, ArrayList<Subscriber>> stockSubs = new HashMap<>();

    public TradingTerminalBlockEntity() {
        super(BlockEntityTypeManager.TRADING_TERMINAL_BLOCK_ENTITY_TYPE);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("gui.container.trading_terminal");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new TradingTerminalScreenHandler(syncId, this);
    }

    @Override
    public void notify(Transaction t) {
        ArrayList<Subscriber> subs = stockSubs.get(t.stock);
        if (subs != null)
            for (Subscriber s : subs)
                s.notify(t);
    }

    public List<Item> getStocks() {
        return Main.MARKET.getStocks();
    }

    public List<Transaction> getTransactions(Item stock) {
        return Main.MARKET.getMarketMaker(stock).getTransactions();
    }

    public void subscribe(Item stock, Subscriber sub) {
        ArrayList<Subscriber> subs = stockSubs.computeIfAbsent(stock, k -> {
            Main.MARKET.getMarketMaker(stock).subscribe(this);
            return new ArrayList<>();
        });
        subs.add(sub);
    }

    public void unsubscribe(Item stock, Subscriber sub) {
        ArrayList<Subscriber> subs = stockSubs.get(stock);
        if (subs == null)
            return;
        subs.remove(sub);
        if (subs.size() == 0) {
            Main.MARKET.getMarketMaker(stock).unsubscribe(this);
            stockSubs.remove(stock);
        }
    }
}
