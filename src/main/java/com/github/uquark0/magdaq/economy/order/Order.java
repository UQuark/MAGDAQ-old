package com.github.uquark0.magdaq.economy.order;

import com.github.uquark0.magdaq.economy.Broker;
import com.github.uquark0.magdaq.economy.MoneyAmount;
import net.minecraft.item.Item;

public abstract class Order {
    public int amount;
    public final Broker owner;
    public final long timestamp;
    public final Item stock;

    public Order(int amount, Broker owner, Item stock) {
        this.amount = amount;
        this.owner = owner;
        this.stock = stock;
        timestamp = System.currentTimeMillis();
    }

    public abstract void transact(int amount, MoneyAmount price);
}
