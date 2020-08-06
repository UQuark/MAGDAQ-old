package com.github.uquark0.magdaq.economy;

import com.github.uquark0.magdaq.economy.order.BuyLimitOrder;
import com.github.uquark0.magdaq.economy.order.BuyOrder;
import com.github.uquark0.magdaq.economy.order.SellLimitOrder;
import com.github.uquark0.magdaq.economy.order.SellOrder;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class Transaction {
    public final BuyOrder buy;
    public final SellOrder sell;
    public final int amount;
    public final MoneyAmount price;
    public final Item stock;

    public static class RawInfo {
        public final long price;
        public final int amount;
        public final int stock;

        public RawInfo(Transaction t) {
            this.price = t.price.value;
            this.amount = t.amount;
            this.stock = Registry.ITEM.getRawId(t.stock);
        }

        public RawInfo(long price, int amount, int stock) {
            this.price = price;
            this.amount = amount;
            this.stock = stock;
        }
    }

    public Transaction(BuyOrder buy, SellOrder sell) {
        if (buy.stock != sell.stock)
            throw new IllegalArgumentException("Different stocks");
        this.stock = buy.stock;
        this.buy = buy;
        this.sell = sell;
        this.amount = Math.min(buy.amount, sell.amount);
        if (buy instanceof BuyLimitOrder && sell instanceof SellLimitOrder) {
            if (buy.timestamp < sell.timestamp)
                this.price = ((BuyLimitOrder) buy).price;
            else if (buy.timestamp > sell.timestamp)
                this.price = ((SellLimitOrder) sell).price;
            else
                this.price = new MoneyAmount((((BuyLimitOrder) buy).price.value + ((SellLimitOrder) sell).price.value) / 2);
        } else if (buy instanceof BuyLimitOrder) {
            this.price = ((BuyLimitOrder) buy).price;
        } else if (sell instanceof SellLimitOrder) {
            this.price = ((SellLimitOrder) sell).price;
        } else {
            throw new IllegalArgumentException("Can't pair two market orders");
        }
    }

    public Transaction(RawInfo rawInfo) {
        buy = null;
        sell = null;
        amount = rawInfo.amount;
        price = new MoneyAmount(rawInfo.price);
        stock = Registry.ITEM.get(rawInfo.stock);
    }

    public void apply() {
        buy.transact(amount, price);
        sell.transact(amount, price);
    }
}
