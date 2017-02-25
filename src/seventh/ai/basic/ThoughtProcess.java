/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.shared.Debugable;
import seventh.shared.TimeStep;

/**
 * The entities thought process
 * 
 * @author Tony
 *
 */
public interface ThoughtProcess extends Debugable {

    /**
     * This brain has freshly spawned
     * @param brain
     */
    public void onSpawn(Brain brain);
    
    /**
     * The player has been killed
     * @param brain
     */
    public void onKilled(Brain brain);
    
    /**
     * A pluggable thinking/strategy
     * @param timeStep
     * @param brain
     */
    public void think(TimeStep timeStep, Brain brain);    
}
