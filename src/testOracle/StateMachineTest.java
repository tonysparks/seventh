package testOracle;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.server.GameServer;
import seventh.server.InGameState;
import seventh.server.ServerStartState;
import seventh.shared.State;
import seventh.shared.StateMachine;
import seventh.shared.StateMachine.StateMachineListener;
import seventh.shared.TimeStep;

public class StateMachineTest {

	/*
	 * Purpose: current state of stateMachine is NULL after calling constructor
	 * Input: nothing
	 * Expected: 
	 * 			currentState of statemachine is NULL
	 */
	@Test
	public void testCurrentStateAfterConstruct() {
		assertNull(new StateMachine<>().getCurrentState());
	}
	
	/*
	 * Purpose: change the current state to enter with no Listener
	 * Input: test state => ServerStartState
	 * Expected: 
	 * 			currentState is enter
	 * 			listener is NULL
	 */
	@Test
	public void testChangeCurrentStateEnter(){
		StateMachine<State> statemachine = new StateMachine<State>();
		ServerStartState expectedState = new ServerStartState();
		statemachine.changeState(expectedState);
		assertEquals(expectedState,statemachine.getCurrentState());
		assertNull(statemachine.getListener());
	}
	
}
