package com.oneapm.example.products;

/**
 * Created by Wei ZUO on 2016/10/26.
 */
public class Product_A implements Product {
    @Override
    public String getName() {
        return "A";
    }

    @Override
    public int getPrice() {
        return 65;
    }
}
