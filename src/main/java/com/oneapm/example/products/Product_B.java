package com.oneapm.example.products;

/**
 * Created by Wei ZUO on 2016/10/26.
 */
public class Product_B implements Product {
    @Override
    public String getName() {
        return "B";
    }

    @Override
    public int getPrice() {
        return 100;
    }
}
