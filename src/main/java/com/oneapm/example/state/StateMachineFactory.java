package com.oneapm.example.state;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

final public class StateMachineFactory<OPERAND, STATE extends Enum<STATE>, EVENTTYPE extends Enum<EVENTTYPE>, EVENT> {

    private final TransitionsListNode transitionsListNode;

    private Map<STATE, Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>> stateMachineTable;

    private STATE defaultInitialState;

    private final boolean optimized;

    public StateMachineFactory(STATE defaultInitialState) {
        this.transitionsListNode = null;
        this.defaultInitialState = defaultInitialState;
        this.optimized = false;
        this.stateMachineTable = null;
    }

    private StateMachineFactory(StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> that,
                                ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT> t) {
        this.defaultInitialState = that.defaultInitialState;
        this.transitionsListNode = new TransitionsListNode(t,
                that.transitionsListNode);
        this.optimized = false;
        this.stateMachineTable = null;
    }

    private StateMachineFactory(StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> that,
                                boolean optimized) {
        this.defaultInitialState = that.defaultInitialState;
        this.transitionsListNode = that.transitionsListNode;
        this.optimized = optimized;
        if (optimized) {
            makeStateMachineTable();
        } else {
            stateMachineTable = null;
        }
    }

    private interface ApplicableTransition<OPERAND, STATE extends Enum<STATE>, EVENTTYPE extends Enum<EVENTTYPE>, EVENT> {
        void apply(StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> subject);
    }

    private class TransitionsListNode {
        final ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT> transition;
        final TransitionsListNode next;

        TransitionsListNode(ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT> transition,
                            TransitionsListNode next) {
            this.transition = transition;
            this.next = next;
        }
    }

    static private class ApplicableSingleOrMultipleTransition<OPERAND, STATE extends Enum<STATE>, EVENTTYPE extends Enum<EVENTTYPE>, EVENT> implements ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT> {
        final STATE preState;
        final EVENTTYPE eventType;
        final Transition<OPERAND, STATE, EVENTTYPE, EVENT> transition;

        ApplicableSingleOrMultipleTransition(STATE preState,
                                             EVENTTYPE eventType,
                                             Transition<OPERAND, STATE, EVENTTYPE, EVENT> transition) {
            this.preState = preState;
            this.eventType = eventType;
            this.transition = transition;
        }

        @Override
        public void apply(StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> subject) {
            Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>> transitionMap = subject.stateMachineTable.get(preState);
            if (transitionMap == null) {
                transitionMap = new HashMap<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>();
                subject.stateMachineTable.put(preState,
                        transitionMap);
            }
            transitionMap.put(eventType,
                    transition);
        }
    }

    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(STATE preState,
                                                                               STATE postState,
                                                                               EVENTTYPE eventType) {
        return addTransition(preState,
                postState,
                eventType,
                null);
    }

    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(STATE preState,
                                                                               STATE postState,
                                                                               Set<EVENTTYPE> eventTypes) {
        return addTransition(preState,
                postState,
                eventTypes,
                null);
    }

    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(STATE preState,
                                                                               STATE postState,
                                                                               Set<EVENTTYPE> eventTypes,
                                                                               SingleArcTransition<OPERAND, EVENT> hook) {
        StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> factory = null;
        for (EVENTTYPE event : eventTypes) {
            if (factory == null) {
                factory = addTransition(preState,
                        postState,
                        event,
                        hook);
            } else {
                factory = factory.addTransition(preState,
                        postState,
                        event,
                        hook);
            }
        }
        return factory;
    }

    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(STATE preState,
                                                                               STATE postState,
                                                                               EVENTTYPE eventType,
                                                                               SingleArcTransition<OPERAND, EVENT> hook) {
        return new StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT>(this,
                new ApplicableSingleOrMultipleTransition<OPERAND, STATE, EVENTTYPE, EVENT>(preState,
                        eventType,
                        new SingleInternalArc(postState,
                                hook)));
    }

    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> addTransition(STATE preState,
                                                                               Set<STATE> postStates,
                                                                               EVENTTYPE eventType,
                                                                               MultipleArcTransition<OPERAND, EVENT, STATE> hook) {
        return new StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT>(this,
                new ApplicableSingleOrMultipleTransition<OPERAND, STATE, EVENTTYPE, EVENT>(preState,
                        eventType,
                        new MultipleInternalArc(postStates,
                                hook)));
    }

    public StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> installTopology() {
        return new StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT>(this,
                true);
    }

    private STATE doTransition(OPERAND operand,
                               STATE oldState,
                               EVENTTYPE eventType,
                               EVENT event) throws InvalidStateTransitonException {
        Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>> transitionMap = stateMachineTable.get(oldState);
        if (transitionMap != null) {
            Transition<OPERAND, STATE, EVENTTYPE, EVENT> transition = transitionMap.get(eventType);
            if (transition != null) {
                return transition.doTransition(operand,
                        oldState,
                        event,
                        eventType);
            }
        }
        throw new InvalidStateTransitonException(oldState,
                eventType);
    }

    private synchronized void maybeMakeStateMachineTable() {
        if (stateMachineTable == null) {
            makeStateMachineTable();
        }
    }

    private void makeStateMachineTable() {
        Stack<ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT>> stack = new Stack<ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT>>();

        Map<STATE, Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>> prototype = new HashMap<STATE, Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>>();

        prototype.put(defaultInitialState,
                null);

        stateMachineTable = new EnumMap<STATE, Map<EVENTTYPE, Transition<OPERAND, STATE, EVENTTYPE, EVENT>>>(prototype);

        for (TransitionsListNode cursor = transitionsListNode; cursor != null; cursor = cursor.next) {
            stack.push(cursor.transition);
        }

        while (!stack.isEmpty()) {
            stack.pop()
                    .apply(this);
        }
    }

    private interface Transition<OPERAND, STATE extends Enum<STATE>, EVENTTYPE extends Enum<EVENTTYPE>, EVENT> {
        STATE doTransition(OPERAND operand,
                           STATE oldState,
                           EVENT event,
                           EVENTTYPE eventType);
    }

    private class SingleInternalArc implements Transition<OPERAND, STATE, EVENTTYPE, EVENT> {

        private STATE postState;
        private SingleArcTransition<OPERAND, EVENT> hook;     // transition hook

        SingleInternalArc(STATE postState,
                          SingleArcTransition<OPERAND, EVENT> hook) {
            this.postState = postState;
            this.hook = hook;
        }

        @Override
        public STATE doTransition(OPERAND operand,
                                  STATE oldState,
                                  EVENT event,
                                  EVENTTYPE eventType) {
            if (hook != null) {
                hook.transition(operand,
                        event);
            }
            return postState;
        }
    }

    private class MultipleInternalArc implements Transition<OPERAND, STATE, EVENTTYPE, EVENT> {

        private Set<STATE> validPostStates;
        private MultipleArcTransition<OPERAND, EVENT, STATE> hook;

        MultipleInternalArc(Set<STATE> postStates,
                            MultipleArcTransition<OPERAND, EVENT, STATE> hook) {
            this.validPostStates = postStates;
            this.hook = hook;
        }

        @Override
        public STATE doTransition(OPERAND operand,
                                  STATE oldState,
                                  EVENT event,
                                  EVENTTYPE eventType) throws InvalidStateTransitonException {
            STATE postState = hook.transition(operand,
                    event);

            if (!validPostStates.contains(postState)) {
                throw new InvalidStateTransitonException(oldState,
                        eventType);
            }
            return postState;
        }
    }

    public StateMachine<STATE, EVENTTYPE, EVENT> make(OPERAND operand,
                                                      STATE initialState) {
        return new InternalStateMachine(operand,
                initialState);
    }

    public StateMachine<STATE, EVENTTYPE, EVENT> make(OPERAND operand) {
        return new InternalStateMachine(operand,
                defaultInitialState);
    }

    private class InternalStateMachine implements StateMachine<STATE, EVENTTYPE, EVENT> {
        private final OPERAND operand;
        private STATE currentState;

        InternalStateMachine(OPERAND operand,
                             STATE initialState) {
            this.operand = operand;
            this.currentState = initialState;
            if (!optimized) {
                maybeMakeStateMachineTable();
            }
        }

        @Override
        public synchronized STATE getCurrentState() {
            return currentState;
        }

        @Override
        public synchronized STATE doTransition(EVENTTYPE eventType,
                                               EVENT event) throws InvalidStateTransitonException {
            currentState = StateMachineFactory.this.doTransition(operand,
                    currentState,
                    eventType,
                    event);
            return currentState;
        }
    }
}
