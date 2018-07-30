package net.avdw.statemachine;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import static org.junit.Assert.assertEquals;

public class StateMachineTest {

    StateMachine stateMachine;

    @BeforeClass
    public static void beforeClass() {
        Configurator.currentConfig()
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}() {level}: {message}")
                .level(Level.DEBUG)
                .activate();
    }

    @Before
    public void setup() {
        stateMachine = new StateMachine();

        stateMachine.addState(new AvailableState());
        stateMachine.addState(new PendingState(), AvailableState.class);
        stateMachine.addState(new UnavailableState(), PendingState.class);
        stateMachine.initial(AvailableState.class);
    }

    @Test
    public void testBasicUsage() throws StateMachine.InvalidStateTransitionException {
        TestState testState = new TestState();

        stateMachine.process(testState);
        assertEquals("available", testState.message);
        stateMachine.transition(PendingState.class);
        stateMachine.process(testState);
        assertEquals("pending->unavailable", testState.message);
    }

    @Test(expected = StateMachine.InvalidStateTransitionException.class)
    public void testInvalidStateTransition() throws StateMachine.InvalidStateTransitionException {
        stateMachine.transition(UnavailableState.class);
    }

    class TestState {
        String message;
    }

    class AvailableState extends StateMachine.AState<TestState> {
        @Override
        public void process(TestState state) {
            Logger.info("process");
            state.message = "available";
        }
    }

    class PendingState extends StateMachine.AState<TestState> {
        @Override
        public void process(TestState state) {
            Logger.info("process");
            try {
                machine.transition(UnavailableState.class);
                state.message = "pending->unavailable";
            } catch (StateMachine.InvalidStateTransitionException e) {
                Logger.error(e);
            }
        }
    }

    class UnavailableState extends StateMachine.AState<TestState> {
    }
}