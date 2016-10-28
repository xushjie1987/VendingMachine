package com.oneapm.example;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Wei ZUO on 2016/10/26.
 */
public class TestDynamicPlan {

    public static class F {
        public F(Integer value) {
            this.value = value;
        }

        Integer value;
        Map<Integer, F> s;

        void walk() {
            if (value == 0) return;
            s = new HashMap<>();
            if (value >= 10)
                s.put(10, new F(value - 10));
            if (value >= 25)
                s.put(25, new F(value - 25));
            if (value >= 50)
                s.put(50, new F(value - 50));
            if (value >= 100)
                s.put(100, new F(value - 100));
            s.forEach((k, v) -> {
                v.walk();
            });
        }

        void print(String... pre) {
            if (value == 0)
                System.out.println(pre);
            else
                s.forEach((k, v) -> {
                    v.print(pre + "," + k);
                });
        }
    }


    public static void main(String[] args) {
        F f = new F(200);
        f.walk();
        f.print();
    }


}
