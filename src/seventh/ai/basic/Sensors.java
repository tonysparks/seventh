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

	private SightSensor sightSensor;
	private SoundSensor soundSensor;
	private FeelSensor feelSensor;
	
	
	
	public Sensors(Brain brain) {
		this.sightSensor = new SightSensor(brain);
		this.soundSensor = new SoundSensor(brain);
		this.feelSensor = new FeelSensor(brain);
	}
	
	
	/**
	 * Resets each {@link Sensor}
	 * 
	 * @param brain
	 */
	public void reset(Brain brain) {
		this.sightSensor.reset(brain);
		this.soundSensor.reset(brain);
		this.feelSensor.reset(brain);
	}
	
	/**
	 * @return the feelSensor
	 */
	public FeelSensor getFeelSensor() {
		return feelSensor;
	}
	
	/**
	 * @return the sightSensor
	 */
	public SightSensor getSightSensor() {
		return sightSensor;
	}
	
	/**
	 * @return the soundSensor
	 */
	public SoundSensor getSoundSensor() {
		return soundSensor;
	}
	
	/**
	 * Poll each sensor
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {
		this.sightSensor.update(timeStep);
		this.soundSensor.update(timeStep);
		this.feelSensor.update(timeStep);		
	}
		
}
