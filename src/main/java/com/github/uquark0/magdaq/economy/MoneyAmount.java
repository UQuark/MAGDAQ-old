package com.github.uquark0.magdaq.economy;

public class MoneyAmount implements Comparable<MoneyAmount> {
    public final long amount;

    public MoneyAmount(long amount) {
        this.amount = amount;
    }

    public MoneyAmount(int whole, int fraction) {
        this.amount = whole * 10000 + fraction * 100;
    }

    public long getWhole() {
        return amount / 10000;
    }

    public long getFraction() {
        return (amount % 10000) / 100;
    }

    @Override
    public int compareTo(MoneyAmount other) {
        return Long.compare(amount, other.amount);
    }
}
