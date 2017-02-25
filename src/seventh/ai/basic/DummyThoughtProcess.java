/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.shared.TimeStep;

/**
 * A {@link ThoughtProcess} that does nothing, this is to allow for
 * having Agents that do nothing for debugging purposes.
 * 
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

    /* (non-Javadoc)
     * @see seventh.shared.Debugable#getDebugInformation()
     */
    @Override
    public DebugInformation getDebugInformation() {
        DebugInformation me = new DebugInformation();
        me.add("type", getClass().getSimpleName());
        return me;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getDebugInformation().toString();
    }
}
