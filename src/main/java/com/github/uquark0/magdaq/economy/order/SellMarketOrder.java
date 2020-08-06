package com.github.uquark0.magdaq.economy.order;

import com.github.uquark0.magdaq.economy.Broker;
import com.github.uquark0.magdaq.economy.MoneyAmount;
import net.minecraft.item.Item;

public class SellMarketOrder extends SellOrder {
    public SellMarketOrder(int amount, Broker owner, Item stock) {
        super(amount, owner, stock);
    }

    @Override
    public void transact(int amount, MoneyAmount transactionPrice) {
        if (amount > this.amount)
            throw new IllegalArgumentException("Transaction amount exceeds order amount");
        this.amount -= amount;
        MoneyAmount sum = new MoneyAmount(transactionPrice.value * amount);
        owner.increaseMoney(sum);
        owner.reduceStock(stock, amount);
    }
}
