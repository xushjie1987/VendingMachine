package com.oneapm.example.state;

public interface MultipleArcTransition<OPERAND, EVENT, STATE extends Enum<STATE>> {
    public STATE transition(OPERAND operand,
                            EVENT event);
}
