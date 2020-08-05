package com.github.uquark0.magdaq.economy.order;

import com.github.uquark0.magdaq.economy.Broker;
import com.github.uquark0.magdaq.economy.MoneyAmount;
import net.minecraft.item.Item;

public class BuyLimitOrder extends BuyOrder implements Comparable<Order> {
    public final MoneyAmount price;

    public BuyLimitOrder(int amount, MoneyAmount price, Broker owner, Item stock) {
        super(amount, owner, stock);
        this.price = price;
    }

    @Override
    public int compareTo(Order order) {
        if (!(order instanceof BuyLimitOrder))
            throw new IllegalArgumentException("Can compare only with BuyLimitOrder");
        BuyLimitOrder b = (BuyLimitOrder) order;
        return Long.compare(b.price.amount, price.amount);
    }

    @Override
    public void transact(int amount, MoneyAmount transactionPrice) {
        if (transactionPrice.amount > price.amount)
            throw new IllegalArgumentException("Transaction price exceeds max price");
        if (amount > this.amount)
            throw new IllegalArgumentException("Transaction amount exceeds order amount");
        this.amount -= amount;
        MoneyAmount sum = new MoneyAmount(transactionPrice.amount * amount);
        owner.reduceMoney(sum);
        owner.increaseStock(stock, amount);
    }
}
