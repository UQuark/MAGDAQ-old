package com.github.uquark0.magdaq.economy;

import com.github.uquark0.magdaq.Main;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public class Market {
    private final HashMap<Item, MarketMaker> marketMakers = new HashMap<>();

    public MarketMaker getMarketMaker(Item stock) {
        return marketMakers.get(stock);
    }

    public void addStock(Item stock) {
        marketMakers.put(stock, new MarketMaker(stock));
    }

    public Item[] getStocks() {
        Set<Item> stocks = marketMakers.keySet();
        Stream<Item> sorted = stocks.stream().sorted((i1, i2) -> {
            long v1 = Main.MARKET.getMarketMaker(i1).getVolume();
            long v2 = Main.MARKET.getMarketMaker(i2).getVolume();
            return Long.compare(v2, v1);
        });

        Item[] items = new Item[stocks.size()];
        Iterator<Item> iterator = sorted.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            items[i] = iterator.next();
            i++;
        }
        return items;
    }
}
