package com.oneapm.example.products;

import java.util.Arrays;

/**
 * Created by Wei ZUO on 2016/10/26.
 */
public class Products {
    public static final Product PRODUCT_A = new Product_A();
    public static final Product PRODUCT_B = new Product_B();
    public static final Product PRODUCT_C = new Product_C();

    public static  Product valueOf(String product) {
        return PRODUCT_A.getName()
                        .equalsIgnoreCase(product)
               ? PRODUCT_A
               : PRODUCT_B.getName()
                          .equalsIgnoreCase(product)
                 ? PRODUCT_B
                 : PRODUCT_C;
    }

    public static String[] values(){
        return new String[]{"A","B","C"};
    }

    public static boolean isOne(String pro) {
        return Arrays.asList(Products.values()).stream().anyMatch(p -> p.equalsIgnoreCase(pro));
    }
}
