package com.oneapm.example;

/**
 * Created by Wei ZUO on 2016/10/26.
 */
public class NoProductException extends RuntimeException {
    public NoProductException(String message){
        super(message);
    }
}
