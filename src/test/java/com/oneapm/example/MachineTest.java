package com.oneapm.example;

import com.oneapm.example.state.StateMachine;
import com.oneapm.example.state.StateMachineFactory;
import org.junit.Test;

/**
 * Created by Wei ZUO on 2016/10/27.
 */
public class MachineTest {
    static class XXImpl {
        void f1(Event e) {
            System.out.println("I buy an apple.");
        }
    }
    enum State {
        INIT,
        CHARGE;
    }
    enum EventType {
        BUY;
    }
    static class Event {

    }
    @Test
    public void test1() {
        StateMachineFactory<XXImpl, State, EventType, Event> factory = new StateMachineFactory<XXImpl, State, EventType, Event>(State.INIT).addTransition(State.INIT, State.CHARGE, EventType.BUY, XXImpl::f1).installTopology();
        StateMachine machine = factory.make(new XXImpl());
        machine.doTransition(EventType.BUY, new Event());
    }
}
