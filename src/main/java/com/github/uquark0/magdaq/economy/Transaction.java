package com.github.uquark0.magdaq.economy;

import com.github.uquark0.magdaq.economy.order.BuyLimitOrder;
import com.github.uquark0.magdaq.economy.order.BuyOrder;
import com.github.uquark0.magdaq.economy.order.SellLimitOrder;
import com.github.uquark0.magdaq.economy.order.SellOrder;

public class Transaction {
    public final BuyOrder buy;
    public final SellOrder sell;
    public final int amount;
    public final MoneyAmount price;

    public Transaction(BuyOrder buy, SellOrder sell) {
        this.buy = buy;
        this.sell = sell;
        this.amount = Math.min(buy.amount, sell.amount);
        if (buy instanceof BuyLimitOrder && sell instanceof SellLimitOrder) {
            if (buy.timestamp < sell.timestamp)
                this.price = ((BuyLimitOrder) buy).price;
            else if (buy.timestamp > sell.timestamp)
                this.price = ((SellLimitOrder) sell).price;
            else
                this.price = new MoneyAmount((((BuyLimitOrder) buy).price.amount + ((SellLimitOrder) sell).price.amount) / 2);
        } else if (buy instanceof BuyLimitOrder) {
            this.price = ((BuyLimitOrder) buy).price;
        } else if (sell instanceof SellLimitOrder) {
            this.price = ((SellLimitOrder) sell).price;
        } else {
            throw new IllegalArgumentException("Can't pair two market orders");
        }
    }

    public void apply() {
        buy.transact(amount, price);
        sell.transact(amount, price);
    }

    public String toString() {
        return String.format("%d.%d %d", price.getWhole(), price.getFraction(), amount);
    }
}
