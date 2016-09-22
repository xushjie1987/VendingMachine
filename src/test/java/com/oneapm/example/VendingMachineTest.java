package com.oneapm.example;

import org.junit.Test;

import static org.junit.Assert.*;

public class VendingMachineTest {
    private VendingMachine vendingMachine = new VendingMachine();
    
    @Test
    public void coinReturnTest(){
        String result=vendingMachine.process("Q,COIN-RETURN");
        assertEquals("Get a wrong result","Q",result);
        
        result=vendingMachine.process("Q,Q,COIN-RETURN");
        assertEquals("Get a wrong result","Q,Q",result);
    
        result=vendingMachine.process("Q,Q,D,COIN-RETURN");
        assertEquals("Get a wrong result","Q,Q,D",result);
        
    }
    
    @Test
    public void buyBWithExactChange() throws Exception {
        String result = vendingMachine.process("Q,Q,Q,Q,GET-B");
        assertEquals("Get a wrong result", "B", result);
    }
    
    @Test
    public void buyAWithoutExactChange() throws Exception {
        String result = vendingMachine.process("DOLLAR,GET-A");
        assertEquals("Get a wrong result", "A,Q,D", result);
    }
    
}
