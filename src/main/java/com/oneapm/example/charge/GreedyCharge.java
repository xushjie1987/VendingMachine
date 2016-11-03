package com.oneapm.example.charge;

import com.oneapm.example.coins.Coin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Wei ZUO on 2016/10/28.
 */
public class GreedyCharge implements Charge {

    @Override
    public List<Coin> doCharge(Map<Coin, AtomicInteger> currentChanges, int userBalance) {
        return new ArrayList<>();
    }
}
