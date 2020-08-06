package com.github.uquark0.magdaq.economy;

public class MoneyAmount implements Comparable<MoneyAmount> {
    public final long value;

    public MoneyAmount(long value) {
        this.value = value;
    }

    public MoneyAmount(int whole, int fraction) {
        this.value = whole * 10000 + fraction * 100;
    }

    public long getWhole() {
        return value / 10000;
    }

    public long getFraction() {
        return (value % 10000) / 100;
    }

    @Override
    public int compareTo(MoneyAmount other) {
        return Long.compare(value, other.value);
    }
}
