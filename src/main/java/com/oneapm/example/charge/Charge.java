package com.oneapm.example.charge;

import com.oneapm.example.coins.Coin;
import com.oneapm.example.products.Product;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Wei ZUO on 2016/10/28.
 */
public interface Charge {
    List<Coin> doCharge(Map<Coin,AtomicInteger> currentChanges, int userBalance);
}
