package com.github.uquark0.magdaq.economy.order;

import com.github.uquark0.magdaq.economy.Broker;
import net.minecraft.item.Item;

public abstract class BuyOrder extends Order {
    public BuyOrder(int amount, Broker owner, Item stock) {
        super(amount, owner, stock);
    }
}
