package com.github.uquark0.magdaq.economy;

import com.github.uquark0.magdaq.economy.order.*;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarketMaker {
    public final Item stock;

    private final ArrayList<BuyLimitOrder> buyLimit = new ArrayList<>();
    private final ArrayList<SellLimitOrder> sellLimit = new ArrayList<>();
    private final ArrayList<BuyMarketOrder> buyMarket = new ArrayList<>();
    private final ArrayList<SellMarketOrder> sellMarket = new ArrayList<>();

    private final ArrayList<Transaction> transactions = new ArrayList<>();

    private final ArrayList<Subscriber> subs = new ArrayList<>();

    private long volume;

    public MarketMaker(Item stock) {
        this.stock = stock;
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

    public void subscribe(Subscriber sub) {
        subs.add(sub);
    }

    public void unsubscribe(Subscriber sub) {
        subs.remove(sub);
    }

    public long getVolume() {
        return volume;
    }

    public List<Transaction> getTransactions() {
        return (List<Transaction>) transactions.clone();
    }

    public Quotation getQuotation() {
        return new Quotation(buyLimit, sellLimit, stock);
    }

    private void runTransaction(BuyOrder b, SellOrder s) {
        Transaction t = new Transaction(b, s);
        t.apply();
        transactions.add(t);
        volume += t.amount;
        for (Subscriber subscriber : subs)
            subscriber.notifyTransaction(t);
    }

    private boolean isParity() {
        return (buyLimit.size() == 0) ||
                (sellLimit.size() == 0) ||
                (sellLimit.get(0).price.value > buyLimit.get(0).price.value);
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

        for (Subscriber subscriber : subs)
            subscriber.notifyQuotation(new Quotation(buyLimit, sellLimit, stock));
    }
}
