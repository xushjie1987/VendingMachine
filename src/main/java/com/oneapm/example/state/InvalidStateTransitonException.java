package com.oneapm.example.state;

public class InvalidStateTransitonException extends RuntimeException {

    private static final long serialVersionUID = 8610511635996283691L;

    private Enum<?> currentState;
    private Enum<?> event;

    public InvalidStateTransitonException(Enum<?> currentState, Enum<?> event) {
        super("Invalid event: " + event + " at " + currentState);
        this.currentState = currentState;
        this.event = event;
    }

    public Enum<?> getCurrentState() {
        return currentState;
    }

    public Enum<?> getEvent() {
        return event;
    }

}
