package com.github.uquark0.magdaq.economy.order;

import com.github.uquark0.magdaq.economy.Broker;
import net.minecraft.item.Item;

public abstract class SellOrder extends Order {
    public SellOrder(int amount, Broker owner, Item stock) {
        super(amount, owner, stock);
    }
}
