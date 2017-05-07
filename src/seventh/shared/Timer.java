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
        this.setOnFirstTime(false);
        reset();
    }

    public void set(Timer timer) {
        this.setCurrentTime(timer.getCurrentTime());
        this.setEndTime(timer.getEndTime());
        this.setUpdate(timer.isUpdate());
        this.setLoop(timer.isLoop());
        this.setTime(timer.isTime());
        this.setOnFirstTime(timer.isOnFirstTime());
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
        return this.getEndTime() - this.getCurrentTime();
    }
    
    /**
     * @return true if this timer is currently being updated
     */
    public boolean isUpdating() {
        return isUpdate();
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
        return isLoop();
    }
    
    public Timer reset() {
        this.setCurrentTime(0);
        this.setUpdate(true);
        this.setTime(false);
        return this;
    }
    
    public Timer stop() {
        this.setCurrentTime(0);
        this.setTime(false);
        this.setUpdate(false);
        return this;
    }

    public Timer start() {
        this.setUpdate(true);
        return this;
    }

    public Timer pause() {
        this.setUpdate(false);
        return this;
    }

    public boolean isExpired() {
        return isTime() && !isLooping();
    }
    
    /**
     * Move the remaining time to 0
     */
    public Timer expire() {
        this.setCurrentTime(this.getEndTime());
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
        this.setOnFirstTime(false);
        if (this.isUpdate()) {            
            this.setCurrentTime(this.getCurrentTime() + timeStep.getDeltaTime());
            if (this.getCurrentTime() >= this.getEndTime()) {
                if(this.isLooping()) {
                    reset();
                }
                else {
                    this.setUpdate(false);
                }
                
                this.setTime(true);
                this.setOnFirstTime(true);
                
                onFinish(this);
                
            }
            else {
                this.setTime(false);
            }
        }
    }

	private boolean isUpdate() {
		return update;
	}

	private void setUpdate(boolean update) {
		this.update = update;
	}

	private boolean isLoop() {
		return loop;
	}

	private void setTime(boolean isTime) {
		this.isTime = isTime;
	}

	private void setOnFirstTime(boolean onFirstTime) {
		this.onFirstTime = onFirstTime;
	}

	private long getCurrentTime() {
		return currentTime;
	}

	private void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
}
