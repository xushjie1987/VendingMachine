package com.oneapm.example.charge;

import com.oneapm.example.coins.Coin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by xushjie on 2016/10/27.
 */
public class DynamicCharge implements Charge {
    Integer value;
    Integer less;
    Integer level;
    Map<Integer, DynamicCharge> childs = new HashMap<>();
    Map<Integer, AtomicInteger> current;

    public  DynamicCharge() {

    }

    public DynamicCharge(Integer value, Integer less, Integer level, Map<Integer, AtomicInteger> current) {
        this.value = value;
        this.less = less;
        this.level = level;
        this.current = current;
    }

    boolean hasOne(Integer target) {
        if (current.get(target) == null) return false;
        return current.get(target)
                      .get() > 0;
    }

    Map<Integer, AtomicInteger> decrese(Integer target) {
        final Map<Integer, AtomicInteger> result = new HashMap<>();
        current.forEach((k, v) -> result.putIfAbsent(k,
                                                     new AtomicInteger(v.get())));
        result.get(target)
              .decrementAndGet();
        return result;
    }

    void build() {
        Coin.allCoinValues()
            .stream()
            .filter(v -> (value == null || (v >= value && less >= v)) && hasOne(v) && less >= v)
            .forEach(next -> childs.put(next,
                                        new DynamicCharge(next,
                                                          less - next,
                                                          level + 1,
                                                          decrese(next))));
        childs.forEach((k, v) -> v.build());
    }

    void print(String pre, Integer depth) {
        if (less == 0 && level <= depth) System.out.println(pre + ("".equals(pre)
                                                                   ? ""
                                                                   : ",") + (value==null?"": value));
        else if (less > 0 && level < depth) childs.forEach((k, v) -> v.print(pre + ("".equals(pre)
                                                                                    ? ""
                                                                                    : ",") + (value == null
                                                                                              ? ""
                                                                                              : value),
                                                                             depth));
    }

    final static AtomicReference<List<Coin>> result = new AtomicReference<>();

    List<Coin> any(String pre, Integer depth) {
        if (less == 0 && level <= depth) return Arrays.asList(
                (pre + ("".equals(pre)
                                                                      ? ""
                                                                      : ",") + (value==null?"": value)).split(",\\s*")
        )
                                                      .stream()
                                                      .filter(s -> !s.equals(""))
                                                      .map(
                                                              s ->
                                                                      Coin.nameOf(Integer.valueOf(s)))
                                                      .collect(Collectors.toList());
        else if (less > 0 && level < depth) {

            childs.forEach((k, v) -> result.set(v.any(pre + ("".equals(pre)
                                                             ? ""
                                                             : ",") + (value == null
                                                                       ? ""
                                                                       : value),
                                                      depth)));
            return result.get() == null? new ArrayList<>(): result.get();
        }
        return new ArrayList<>();
    }


    public static void main(String[] args) {
        DynamicCharge charge = new DynamicCharge(null,
                                                 200,
                                                 0,
                                                 Coin.allCoinCounts());
        charge.build();
        System.out.println("-----------------------------------ALL------------------------------");
        charge.print("",
                     Integer.MAX_VALUE);
        System.out.println("------------------------------------6-------------------------------");
        charge.print("",
                     6);
        charge = new DynamicCharge(null,
                                   0,
                                   0,
                                   Coin.allCoinCounts());
        charge.build();
        System.out.println("-----------------------------------ALL------------------------------");
        charge.print("",
                     Integer.MAX_VALUE);
        charge.any("", Integer.MAX_VALUE);
        Arrays.stream("".split(",\\s*")).forEach(s -> System.out.println(s));
    }

    @Override
    public List<Coin> doCharge(Map<Coin, AtomicInteger> currentChanges, int userBalance) {
        final Map<Integer, AtomicInteger> current = new HashMap<>();
        currentChanges.forEach((i, a) -> current.putIfAbsent(i.value, a));
        DynamicCharge charge = new DynamicCharge(null,
                                                 userBalance,
                                                 0,
                                                 current);
        charge.build();
        return charge.any("",
                          Integer.MAX_VALUE);
    }
}
