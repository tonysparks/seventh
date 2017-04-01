package testOracle;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.TimeStep;
import seventh.shared.Timer;

public class TimerTest {

	/*
	 * Purpose: get remaining time when endTime is invalid time
	 * Input: endTime -> endTime -10
	 * Expected: 
	 * 			return failure
	 * 			remain time < 0
	 */
	@Test
	public void testConstructMinusEndTime() {
		Timer timer = new Timer(true,-10);
		TimeStep timestep = new TimeStep();
		timestep.setDeltaTime(10);
		timer.update(timestep);
		assertTrue(0 < timer.getRemainingTime());
		assertTrue(0 < timer.getEndTime());
	}
	
}
