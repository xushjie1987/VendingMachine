package com.oneapm.example;

import com.oneapm.example.coins.Coin;
import com.oneapm.example.products.Product;

/**
 * Created by Wei ZUO on 2016/10/28.
 */
public interface VendingMachineService {
    public void depositeCoins(Coin...coins);
    void depositeProducts(Product...products);

}
