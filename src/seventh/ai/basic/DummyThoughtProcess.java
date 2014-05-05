/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class DummyThoughtProcess implements ThoughtProcess {

	/**
	 * The single instance
	 */
	private static final ThoughtProcess DUMMY_THOUGHT_PROCESS = new DummyThoughtProcess();
	
	/**
	 * @return the singleton instance
	 */
	public static ThoughtProcess getInstance() {
		return DUMMY_THOUGHT_PROCESS;
	}
	
	/**
	 */
	protected DummyThoughtProcess() {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onSpawn(seventh.ai.basic.Brain)
	 */
	@Override
	public void onSpawn(Brain brain) {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onKilled(seventh.ai.basic.Brain)
	 */
	@Override
	public void onKilled(Brain brain) {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#think(seventh.shared.TimeStep, seventh.ai.basic.Brain)
	 */
	@Override
	public void think(TimeStep timeStep, Brain brain) {
	}

}
