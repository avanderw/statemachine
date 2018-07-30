package net.avdw.statemachine;

import java.util.*;

import org.pmw.tinylog.Logger;

public class StateMachine {

    private Class current;
    private final Map<Class, AState> states = new HashMap();

    public void addState(AState state, Class... fromStates) {
        state.machine = this;
        states.put(state.getClass(), state);
        if (fromStates != null) {
            states.get(state.getClass()).from(Arrays.asList(fromStates));
        }
    }

    public void initial(Class state) {
        states.get(state).enter();
        current = state;
    }

    public void process(Object state) {
        states.get(current).process(state);
    }

    public void transition(Class state) throws InvalidStateTransitionException {
        if (current == null) {
            Logger.warn(String.format("initial state not set"));
        }

        if (!states.containsKey(state)) {
            Logger.error(String.format("%s unknown", state.getSimpleName()));
            return;
        }

        if (states.get(state).from().contains(current) || current == null) {
            Logger.debug(String.format("%s -> %s", current == null ? "null" : current.getSimpleName(), state.getSimpleName()));

            if (current != null) {
                states.get(current).exit();
            }
            states.get(state).enter();

            current = state;
        } else {
            throw new InvalidStateTransitionException(String.format("invalid %s -> %s", current.getSimpleName(), state.getSimpleName()));
        }
    }

    public static class AState<T> {

        private Set<Class> from = new HashSet();
        protected StateMachine machine;

        void enter() {
            Logger.debug("enter");
        }

        void exit() {
            Logger.debug("exit");
        }

        final void from(List<Class> states) {
            from.addAll(states);
        }

        final List<Class> from() {
            return new ArrayList(from);
        }

        void process(T state) {
            Logger.debug("process");
        }
    }

    public static class InvalidStateTransitionException extends Exception {
        InvalidStateTransitionException(String message) {
            super(message);
        }

    }
}
