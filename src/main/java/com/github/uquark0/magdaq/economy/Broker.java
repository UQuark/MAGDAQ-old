package com.github.uquark0.magdaq.economy;

import net.minecraft.item.Item;

public interface Broker {
    void reduceMoney(MoneyAmount amount);
    void increaseMoney(MoneyAmount amount);

    void reduceStock(Item stock, int amount);
    void increaseStock(Item stock, int amount);
}
