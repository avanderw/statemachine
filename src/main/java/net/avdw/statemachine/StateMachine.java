package net.avdw.statemachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pmw.tinylog.Logger;

public class StateMachine {

    private Class current;
    private final Map<Class, AState> states = new HashMap();

    public void addState(AState state, Class... fromStates) {
        states.put(state.getClass(), state);
        if (fromStates != null) {
            states.get(state.getClass()).from(Arrays.asList(fromStates));
        }
    }

    public void initial(Class state) {
        states.get(state).enter();
        current = state;
    }

    public void process() {
        states.get(current).process();
    }

    public void transition(Class state) {
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
            Logger.warn(String.format("invalid %s -> %s", current.getSimpleName(), state.getSimpleName()));
        }
    }

    static public interface AState {

        void enter();

        void exit();

        void from(List<Class> states);

        List<Class> from();

        void process();

    }
}
