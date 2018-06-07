package seventh.ai.basic;

import seventh.shared.TimeStep;

public class SensorFactory {

    protected SightSensor sightSensor;
    protected SoundSensor soundSensor;
    protected FeelSensor feelSensor;
    
    public SensorFactory(Brain brain){
        this.sightSensor = new SightSensor(brain);
        this.soundSensor = new SoundSensor(brain);
        this.feelSensor = new FeelSensor(brain);
    }
    
    public FeelSensor getFeelSensor() {
        return feelSensor;
    }
    
    public SightSensor getSightSensor() {
        return sightSensor;
    }
    
    public SoundSensor getSoundSensor() {
        return soundSensor;
    }

    public void reset(Brain brain) {
        this.sightSensor.reset(brain);
        this.soundSensor.reset(brain);
        this.feelSensor.reset(brain);
    }

    public void update(TimeStep timeStep) {
        this.sightSensor.update(timeStep);
        this.soundSensor.update(timeStep);
        this.feelSensor.update(timeStep);
    }
    
    
}
