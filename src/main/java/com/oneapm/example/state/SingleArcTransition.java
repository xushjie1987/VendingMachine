package com.oneapm.example.state;

@FunctionalInterface
public interface SingleArcTransition<OPERAND, EVENT> {
    public void transition(OPERAND operand,
                           EVENT event);
}
