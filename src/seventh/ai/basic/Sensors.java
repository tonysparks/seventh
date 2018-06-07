/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.shared.TimeStep;

/**
 * Just a container for all of our sensory inputs
 * 
 * @author Tony
 *
 */
public class Sensors {

    private SensorFactory sensorfactory;
    
    
    
    public Sensors(Brain brain) {
        this.sensorfactory = new SensorFactory(brain);
    }
    
    
    /**
     * Resets each {@link Sensor}
     * 
     * @param brain
     */
    public void reset(Brain brain) {
        this.sensorfactory.reset(brain);
    }
    
    /**
     * @return the feelSensor
     */
    public FeelSensor getFeelSensor() {
        return this.sensorfactory.getFeelSensor();
    }
    
    /**
     * @return the sightSensor
     */
    public SightSensor getSightSensor() {
        return this.sensorfactory.getSightSensor();
    }
    
    /**
     * @return the soundSensor
     */
    public SoundSensor getSoundSensor() {
        return this.sensorfactory.getSoundSensor();
    }
    
    /**
     * Poll each sensor
     * @param timeStep
     */
    public void update(TimeStep timeStep) {
        this.sensorfactory.update(timeStep);       
    }
        
}
