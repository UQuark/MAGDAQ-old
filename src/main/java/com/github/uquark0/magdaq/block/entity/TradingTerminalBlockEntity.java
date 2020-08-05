package com.github.uquark0.magdaq.block.entity;

import com.github.uquark0.magdaq.Main;
import com.github.uquark0.magdaq.economy.Broker;
import com.github.uquark0.magdaq.economy.MoneyAmount;
import com.github.uquark0.magdaq.economy.Transaction;
import com.github.uquark0.magdaq.gui.TradingTerminalScreenHandler;
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

public class TradingTerminalBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Broker {
    public TradingTerminalBlockEntity() {
        super(BlockEntityTypeManager.TRADING_TERMINAL_BLOCK_ENTITY_TYPE);
    }

    private HashMap<Item, ArrayList<TradingTerminalScreenHandler>> subscribers = new HashMap<>();

    @Override
    public Text getDisplayName() {
        return new TranslatableText("gui.trading_terminal.title");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new TradingTerminalScreenHandler(syncId, this);
    }

    public Item[] getStocks() {
        return Main.MARKET.getStocks();
    }

    public Transaction[] getPrints(Item stock) {
        return Main.MARKET.getMarketMaker(stock).getPrints();
    }

    public void subscribe(Item stock, int syncId) {

    }

    @Override
    public void reduceMoney(MoneyAmount amount) {

    }

    @Override
    public void increaseMoney(MoneyAmount amount) {

    }

    @Override
    public void reduceStock(Item stock, int amount) {

    }

    @Override
    public void increaseStock(Item stock, int amount) {

    }
}
