package com.github.uquark0.magdaq.economy.order;

import com.github.uquark0.magdaq.economy.Transaction;

public interface Subscriber {
    void notify(Transaction t);
}
