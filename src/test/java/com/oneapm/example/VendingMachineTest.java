package com.oneapm.example;

import com.oneapm.example.coins.Coin;
import com.oneapm.example.products.Product;
import com.oneapm.example.products.Product_A;
import com.oneapm.example.products.Product_B;
import com.oneapm.example.products.Products;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class VendingMachineTest {
    private VendingMachine vendingMachine = VendingMachine.instance;
    
    @Test
    public void Buy_B_with_exact_change() {

        VendingMachineService service=vendingMachine;
        service.depositeProducts(Products.PRODUCT_B);
        assertEquals("B", vendingMachine.process("Q, Q, Q, Q, GET-B"));
    }

    @Test
    public void Start_adding_change_but_hit_coin_return_to_get_change_back(){
        assertEquals("Q,Q", vendingMachine.process("Q, Q, COIN-RETURN"));
    }

    @Test
    public void Buy_A_without_exact_change() {
        VendingMachineService service=vendingMachine;
        service.depositeProducts(Products.PRODUCT_A);
        service.depositeCoins(Coin.Q,Coin.D);
        assertEquals("A,D,Q", vendingMachine.process("DOLLAR, GET-A"));
    }




    
}
