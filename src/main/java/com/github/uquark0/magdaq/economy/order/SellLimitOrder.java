package com.github.uquark0.magdaq.economy.order;

import com.github.uquark0.magdaq.economy.Broker;
import com.github.uquark0.magdaq.economy.MoneyAmount;
import net.minecraft.item.Item;

public class SellLimitOrder extends SellOrder implements Comparable<Order> {
    public final MoneyAmount price;

    public SellLimitOrder(int amount, MoneyAmount price, Broker owner, Item stock) {
        super(amount, owner, stock);
        this.price = price;
    }

    @Override
    public int compareTo(Order order) {
        if (!(order instanceof SellLimitOrder))
            throw new IllegalArgumentException("Can compare only with SellLimitOrder");
        SellLimitOrder s = (SellLimitOrder) order;
        return Long.compare(price.value, s.price.value);
    }

    @Override
    public void transact(int amount, MoneyAmount transactionPrice) {
        if (transactionPrice.value < price.value)
            throw new IllegalArgumentException("Transaction price exceeds min price");
        if (amount > this.amount)
            throw new IllegalArgumentException("Transaction amount exceeds order amount");
        this.amount -= amount;
        MoneyAmount sum = new MoneyAmount(transactionPrice.value * amount);
        owner.increaseMoney(sum);
        owner.reduceStock(stock, amount);
    }
}
