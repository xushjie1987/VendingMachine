package com.oneapm.example;

import com.oneapm.example.charge.Charge;
import com.oneapm.example.charge.DynamicCharge;
import com.oneapm.example.coins.Coin;
import com.oneapm.example.products.Product;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by xushjie on 2016/10/20.
 */
public class MachineState {
    List<Coin> insertedCoins = new ArrayList<>();

    Map<Coin, AtomicInteger> changes = new HashMap<>();

    Map<Product, AtomicInteger> products = new HashMap<>();

    Charge charge = new DynamicCharge();

    String output;

    public void obtainProduct(Product product) {
        if (this.products.get(product) == null || this.products.get(product)
                                                               .get() == 0) {
            returnTheInsertedCoins();
            return;
        }

        int balance = getUserBalance() - product.getPrice();

        if (balance < 0) {
            returnTheInsertedCoins();
            return;
        }
        if (balance == 0) {
            this.products.get(product)
                         .decrementAndGet();
            this.insertedCoins.clear();
            this.output = product.getName();
            System.out.println(output);
            return;
        }
        insertedCoins.forEach(coin -> addThingsToPile(changes,coin));
        List<Coin> cs = charge.doCharge(changes,
                                        balance);

        if (cs.isEmpty()) {
            this.insertedCoins.forEach(coin -> this.subThingsFromPile(this.changes,
                                                                      coin));
            returnTheInsertedCoins();

        } else {
            this.insertedCoins.forEach(coin -> this.subThingsFromPile(this.changes,
                                                                      coin));
            this.products.get(product)
                         .decrementAndGet();
            this.output = product.getName() + "," + cs.stream()
                                                      .map(coin -> coin.name)
                                                      .collect(Collectors.joining(","));
            this.insertedCoins.clear();
            System.out.println(output);

        }
    }

    private void returnTheInsertedCoins() {
        output = this.insertedCoins.stream()
                                   .map(coin -> coin.name)
                                   .collect(Collectors.joining(","));
    }

    public int getUserBalance() {
        return this.insertedCoins.stream()
                                 .mapToInt(coin -> coin.value)
                                 .sum();
    }

    public void addProduct(Product product, int number) {
        if (this.products.get(product) == null) {
            this.products.put(product,
                              new AtomicInteger(number));
            return;
        }
        this.products.get(product)
                     .addAndGet(number);
    }

    public void insert(String coin) {


    }

    public String returnChanges(int v) {
        Map<Coin, AtomicInteger> cs = new HashMap<>();
        int result = charge(Coin.N,
                            charge(Coin.D,
                                   charge(Coin.Q,
                                          charge(Coin.DOLLAR,
                                                 v,
                                                 cs),
                                          cs),
                                   cs),
                            cs);
        if (result == 0) {
            return cs.keySet()
                     .stream()
                     .map(coin -> coin.name)
                     .collect(Collectors.joining(","));
        } else {
            rollback(cs);
        }
        return "NO-EXTRACHANGE";
    }

    private void rollback(Map<Coin, AtomicInteger> cs) {
        cs.forEach((s, i) -> {
            changes.get(s)
                   .addAndGet(i.get());
        });
    }

    private int charge(Coin coin, int userBalance, Map<Coin, AtomicInteger> changes) {
        if (!this.changes.containsKey(coin)) {
            return userBalance;
        }
        if (userBalance == 0) {
            return userBalance;
        }
        while (userBalance >= coin.value && this.changes.get(coin)
                                                        .intValue() > 0) {
            userBalance -= coin.value;
            if (changes.containsKey(coin)) {
                changes.get(coin)
                       .incrementAndGet();
            } else {
                changes.put(coin,
                            new AtomicInteger(1));
            }
            this.changes.get(coin)
                        .decrementAndGet();

        }
        return userBalance;
    }

    public void setAvailableChange(Coin coin, int num) {

        if (this.changes.get(coin) == null) {
            this.changes.put(coin,
                             new AtomicInteger(num));
        } else {
            this.changes.get(coin)
                        .addAndGet(num);
        }
    }

    public void insertCoins(List<Coin> coins) {
        this.insertedCoins.addAll(coins);
    }


//    private void addCoinsToPile(Map<Coin,AtomicInteger> pile,Coin...coins){
//        Arrays.stream(coins).forEach(coin -> {
//            if(pile.containsKey(coin)){
//                pile.get(coin).incrementAndGet();
//            }
//            else pile.put(coin,new AtomicInteger(1));
//        });
//    }

    private <T> void addThingsToPile(Map<T, AtomicInteger> pile, T... things) {
        Arrays.stream(things)
              .forEach(thing -> {
                  if (pile.containsKey(thing)) {
                      pile.get(thing)
                          .incrementAndGet();
                  } else pile.put(thing,
                                  new AtomicInteger(1));
              });
    }

    private <T> void subThingsFromPile(Map<T, AtomicInteger> pile, T... things) {
        Arrays.stream(things)
              .forEach(thing -> pile.get(thing)
                                    .decrementAndGet());
    }

    public void setAvailableItems(Product product, int num) {
        this.addProduct(product,
                        num);
    }

    public int getAvailableChange(Coin coin) {
        return this.changes.get(coin)
                           .get();
    }

    public int getAvailableItems(Product product) {
        return this.products.get(product)
                            .get();
    }


    public void depositeCoins(List<Coin> list) {
        list.forEach(coin -> addThingsToPile(this.changes,
                                             coin));
    }

    public void depositeProducts(List<Product> list) {
        list.forEach(product -> addThingsToPile(this.products,
                                                product));
    }

    public void returnCoin() {
        returnTheInsertedCoins();
        this.insertedCoins.clear();
    }
}
