package com.oneapm.example.coins;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by Wei ZUO on 2016/10/26.
 */
public enum Coin {
    N(5, "N", 0),
    D(10, "D", 0),
    Q(25, "Q", 0),
    DOLLAR(100, "DOLLAR", 0);
    public Integer value;
    public String name;
    public AtomicInteger count;

    Coin(Integer value, String name, Integer count) {
        this.value = value;
        this.name = name;
        this.count = new AtomicInteger(count);
    }
    public static Map<Integer, AtomicInteger> allCoinCounts() {
        final Map<Integer, AtomicInteger> current = new HashMap<>();
        Arrays.asList(Coin.values()).stream().forEach(c -> current.putIfAbsent(c.value, c.count));
        return current;
    }
    public static List<Integer> allCoinValues() {
        return Arrays.asList(Arrays.asList(Coin.values()).stream().map(c -> c.value).toArray(Integer[]::new));
    }
    public static boolean isOne(String coin) {
        return Arrays.asList(Coin.values()).stream().anyMatch(c -> c.name.equalsIgnoreCase(coin));
    }
    public static Coin nameOf(Integer v) {
        return Stream.of(Coin.values()).filter(c -> c.value == v).findAny().get();
    }
}
