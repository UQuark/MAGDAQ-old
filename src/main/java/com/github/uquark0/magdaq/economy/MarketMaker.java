package com.github.uquark0.magdaq.economy;

import com.github.uquark0.magdaq.economy.order.*;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.Collections;

public class MarketMaker {
    public final Item stock;

    private final ArrayList<BuyLimitOrder> buyLimit = new ArrayList<>();
    private final ArrayList<SellLimitOrder> sellLimit = new ArrayList<>();
    private final ArrayList<BuyMarketOrder> buyMarket = new ArrayList<>();
    private final ArrayList<SellMarketOrder> sellMarket = new ArrayList<>();

    private final ArrayList<Transaction> prints = new ArrayList<>();

    private long volume;

    public MarketMaker(Item stock) {
        this.stock = stock;
    }

    public Transaction[] getPrints() {
        Transaction[] t = new Transaction[prints.size()];
        prints.toArray(t);
        return t;
    }

    public void putOrder(Order order) {
        if (order.stock != stock)
            throw new IllegalArgumentException("Wrong MarketMaker");

        if (order instanceof BuyLimitOrder) {
            buyLimit.add((BuyLimitOrder) order);
            Collections.sort(buyLimit);
        }
        if (order instanceof SellLimitOrder) {
            sellLimit.add((SellLimitOrder) order);
            Collections.sort(sellLimit);
        }
        if (order instanceof BuyMarketOrder)
            buyMarket.add((BuyMarketOrder) order);
        if (order instanceof SellMarketOrder)
            sellMarket.add((SellMarketOrder) order);

        resolve();
    }

    public long getVolume() {
        return volume;
    }

    private void runTransaction(BuyOrder b, SellOrder s) {
        Transaction t = new Transaction(b, s);
        t.apply();
        prints.add(t);
        volume += t.amount;
    }

    private boolean isParity() {
        return (buyLimit.size() == 0) ||
                (sellLimit.size() == 0) ||
                (sellLimit.get(0).price.amount > buyLimit.get(0).price.amount);
    }

    private void resolve() {
        while (buyMarket.size() > 0 && sellLimit.size() > 0) {
            BuyMarketOrder b = buyMarket.get(0);
            SellLimitOrder s = sellLimit.get(0);
            runTransaction(b, s);
            if (b.amount == 0)
                buyMarket.remove(b);
            if (s.amount == 0)
                sellLimit.remove(s);
        }

        while (sellMarket.size() > 0 && buyLimit.size() > 0) {
            SellMarketOrder s = sellMarket.get(0);
            BuyLimitOrder b = buyLimit.get(0);
            runTransaction(b, s);
            if (b.amount == 0)
                buyLimit.remove(b);
            if (s.amount == 0)
                sellMarket.remove(s);
        }

        while (!isParity()) {
            BuyLimitOrder b = buyLimit.get(0);
            SellLimitOrder s = sellLimit.get(0);
            runTransaction(b, s);
            if (b.amount == 0)
                buyLimit.remove(b);
            if (s.amount == 0)
                sellLimit.remove(s);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Item stock = new Item(new Item.Settings());
        MarketMaker mm = new MarketMaker(stock);

        Broker fake = new Broker() {
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
        };
        mm.putOrder(new BuyLimitOrder(10, new MoneyAmount(5, 15), fake, stock));
        mm.putOrder(new BuyLimitOrder(15, new MoneyAmount(5, 20), fake, stock));
        mm.putOrder(new BuyLimitOrder(5, new MoneyAmount(5, 18), fake, stock));

        Object lock = new Object();
        synchronized (lock) {
            lock.wait(100);
        }

        mm.putOrder(new SellLimitOrder(64, new MoneyAmount(5, 17), fake, stock));
//        mm.putOrder(new SellMarketOrder(64, fake, stock));

        for (Transaction t : mm.prints) {
            System.out.println(t.toString());
        }
    }
}
