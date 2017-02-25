/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.ai.basic.memory.FeelMemory;
import seventh.ai.basic.memory.SightMemory;
import seventh.ai.basic.memory.SoundMemory;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * The {@link Brain}s memory, store and retrieve information.
 * 
 * @author Tony
 *
 */
public class Memory implements Updatable {

    
    private SightMemory sightMemory;
    private SoundMemory soundMemory;
    private FeelMemory feelMemory;
    
    public Memory(Brain brain) {
        AIConfig config = brain.getConfig();
        this.sightMemory = new SightMemory(config.getSightExpireTime());
        this.soundMemory = new SoundMemory(config.getSoundExpireTime());
        this.feelMemory = new FeelMemory(config.getFeelExpireTime());
        
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.sightMemory.update(timeStep);
        this.soundMemory.update(timeStep);
        this.feelMemory.update(timeStep);
    }
    
    /**
     * @return the soundMemory
     */
    public SoundMemory getSoundMemory() {
        return soundMemory;
    }
    
    /**
     * @return the sightMemory
     */
    public SightMemory getSightMemory() {
        return sightMemory;
    }
    
    /**
     * @return the feelMemory
     */
    public FeelMemory getFeelMemory() {
        return feelMemory;
    }
    
    /**
     * Clear the memory
     */
    public void clear() {
        this.sightMemory.clear();
        this.soundMemory.clear();
        this.feelMemory.clear();
    }    
    
}
