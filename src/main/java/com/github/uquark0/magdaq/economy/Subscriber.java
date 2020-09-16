package com.github.uquark0.magdaq.economy;

public interface Subscriber {
    void notifyTransaction(Transaction t);

    void notifyQuotation(Quotation q);

    void notifyBalance(MoneyAmount moneyAmount);
}
