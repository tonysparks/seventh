/*
 * see license.txt 
 */
package seventh.shared;


/**
 * @author Tony
 * 
 */
public class Timer implements Updatable {

    private long currentTime;
    private long endTime;

    private boolean update, loop, isTime, onFirstTime;

    /**
     * @param loop
     * @param endTime
     */
    public Timer(boolean loop, long endTime) {
        this.setLoop(loop);
        this.setEndTime(endTime);                
        this.onFirstTime = false;
        reset();
    }

    public void set(Timer timer) {
        this.currentTime = timer.currentTime;
        this.setEndTime(timer.getEndTime());
        this.update = timer.update;
        this.setLoop(timer.loop);
        this.isTime = timer.isTime();
        this.onFirstTime = timer.isOnFirstTime();
    }

	/**
     * @param endTime the endTime to set
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    /**
     * @return the endTime
     */
    public long getEndTime() {
        return endTime;
    }
    
    /**
     * @return the remaining time
     */
    public long getRemainingTime() {
        return this.getEndTime() - this.currentTime;
    }
    
    /**
     * @return true if this timer is currently being updated
     */
    public boolean isUpdating() {
        return update;
    }
    
    /**
     * @param loop the loop to set
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    /**
     * @return true if set to loop
     */
    public boolean isLooping() {
        return loop;
    }
    
    public Timer reset() {
        this.currentTime = (long) 0;
        this.update = true;
        this.isTime = false;
        return this;
    }
    
    public Timer stop() {
        this.currentTime = (long) 0;
        this.isTime = false;
        this.update = false;
        return this;
    }

    public Timer start() {
        this.update = true;
        return this;
    }

    public Timer pause() {
        this.update = false;
        return this;
    }

    public boolean isExpired() {
        return isTime() && !isLooping();
    }
    
    /**
     * Move the remaining time to 0
     */
    public Timer expire() {
        this.currentTime = this.getEndTime();
        return this;
    }
    
    /**
     * @return the isTime
     */
    public boolean isTime() {
        return isTime;
    }
    
    /**
     * @return the onFirstTime
     */
    public boolean isOnFirstTime() {
        return onFirstTime;
    }
    
    /**
     * You can override this method to get invoked when
     * the timer has been reached
     */
    public void onFinish(Timer timer) {
    }
    
    /**
     * Updates the timer
     * @param timeStep
     */
    public void update(TimeStep timeStep) {
        this.onFirstTime = false;
        if (this.update) {            
            this.currentTime = this.currentTime + timeStep.getDeltaTime();
            if (this.currentTime >= this.getEndTime()) {
                if(this.isLooping()) {
                    reset();
                }
                else {
                    this.update = false;
                }
                
                this.isTime = true;
                this.onFirstTime = true;
                
                onFinish(this);
                
            }
            else {
                this.isTime = false;
            }
        }
    }
}
