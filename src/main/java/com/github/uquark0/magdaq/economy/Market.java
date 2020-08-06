package com.github.uquark0.magdaq.economy;

import com.github.uquark0.magdaq.Main;
import net.minecraft.item.Item;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Market {
    private final HashMap<Item, MarketMaker> marketMakers = new HashMap<>();

    public MarketMaker getMarketMaker(Item stock) {
        return marketMakers.get(stock);
    }

    public void addStock(Item stock) {
        marketMakers.put(stock, new MarketMaker(stock));
    }

    public List<Item> getStocks() {
        Set<Item> stocks = marketMakers.keySet();
        Stream<Item> sorted = stocks.stream().sorted((i1, i2) -> {
            long v1 = Main.MARKET.getMarketMaker(i1).getVolume();
            long v2 = Main.MARKET.getMarketMaker(i2).getVolume();
            return Long.compare(v2, v1);
        });

        return sorted.collect(Collectors.toList());
    }
}
