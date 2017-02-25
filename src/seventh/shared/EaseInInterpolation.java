/*
 * see license.txt 
 */
package seventh.shared;

/**
 * @author Tony
 *
 */
public class EaseInInterpolation implements Updatable {

    private float value;
    private float target;
    private float speed, acceleration;
    
    private long totalTime;
    private long remainingTime;

    public EaseInInterpolation() {        
    }
    
    /**
     * @param from
     * @param to
     * @param time
     */
    public EaseInInterpolation(float from, float to, long time) {
        reset(from, to, time);
    }
    
    /**
     * Rests this interpolation
     * 
     * @param from
     * @param to
     * @param time
     */
    public void reset(float from, float to, long time) {
        if(time > 0) {                        
            this.value = from;
            this.target = to;
            this.speed = 0f;
            
            float timeSq = (time/1000.0f) * (time/1000.0f);
            this.acceleration = (to-from) / (timeSq/4);
            this.remainingTime = this.totalTime = time;
        }
    }

    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.remainingTime -= timeStep.getDeltaTime();
        if(this.remainingTime > 0) {
            if(this.remainingTime < this.totalTime/2) {
                this.speed -= this.acceleration * timeStep.asFraction();
            }
            else {
                this.speed += this.acceleration * timeStep.asFraction();
            }
            
            this.value += this.speed * timeStep.asFraction();
        }
    }
    
    /**
     * @return if there is any time remaining
     */
    public boolean isExpired() {
        return this.remainingTime <= 0;
    }
    
    /**
     * @return the remainingTime
     */
    public long getRemainingTime() {
        return remainingTime;
    }
    
    /**
     * @return the value
     */
    public float getValue() {
        return value;
    }
    
    /**
     * @return the target
     */
    public float getTarget() {
        return target;
    }
    
}
