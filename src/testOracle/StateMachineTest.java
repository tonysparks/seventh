package testOracle;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.StateMachine;

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
}
