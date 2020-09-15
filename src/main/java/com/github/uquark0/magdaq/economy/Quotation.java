package com.github.uquark0.magdaq.economy;

import com.github.uquark0.magdaq.economy.order.BuyLimitOrder;
import com.github.uquark0.magdaq.economy.order.SellLimitOrder;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class Quotation {
    public final ArrayList<BuyLimitOrder> bid;
    public final ArrayList<SellLimitOrder> ask;
    public final MoneyAmount spread;
    public final Item stock;

    public static class RawInfo {
        public final long[] bidPrices;
        public final int[] bidAmounts;
        public final long[] askPrices;
        public final int[] askAmounts;
        public final long spread;
        public final int stock;

        public RawInfo(Quotation q) {
            bidPrices = new long[q.bid.size()];
            bidAmounts = new int[q.bid.size()];
            askPrices = new long[q.bid.size()];
            askAmounts = new int[q.bid.size()];
            spread = q.spread.value;
            stock = Registry.ITEM.getRawId(q.stock);
        }

        public RawInfo(long[] bidPrices, int[] bidAmounts, long[] askPrices, int[] askAmounts, long spread, int stock) {
            this.bidPrices = bidPrices;
            this.bidAmounts = bidAmounts;
            this.askPrices = askPrices;
            this.askAmounts = askAmounts;
            this.spread = spread;
            this.stock = stock;
        }
    }

    public Quotation(ArrayList<BuyLimitOrder> bid, ArrayList<SellLimitOrder> ask, Item stock) {
        this.bid = (ArrayList<BuyLimitOrder>) bid.clone();
        this.ask = (ArrayList<SellLimitOrder>) ask.clone();
        this.stock = stock;
        if (bid.size() > 0 && ask.size() > 0)
            spread = new MoneyAmount(ask.get(0).price.value - bid.get(0).price.value);
        else
            spread = new MoneyAmount(0);
    }

    public Quotation(RawInfo rawInfo) {
        stock = Registry.ITEM.get(rawInfo.stock);
        spread = new MoneyAmount(rawInfo.spread);
        bid = new ArrayList<>();
        for (int i = 0; i < rawInfo.bidPrices.length; i++)
            bid.add(new BuyLimitOrder(rawInfo.bidAmounts[i], new MoneyAmount(rawInfo.bidPrices[i]), null, stock));
        ask = new ArrayList<>();
        for (int i = 0; i < rawInfo.askPrices.length; i++)
            ask.add(new SellLimitOrder(rawInfo.askAmounts[i], new MoneyAmount(rawInfo.askPrices[i]), null, stock));
    }
}
