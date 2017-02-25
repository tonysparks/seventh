/*
 * see license.txt 
 */
package seventh.game;

import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.shared.Updatable;

/**
 * @author Tony
 *
 */
public class Timers implements Updatable {

    private Timer[] timers;
    /**
     * 
     */
    public Timers(int maxTimers) {
        this.timers = new Timer[maxTimers];
    }

    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        for(int i = 0; i < timers.length; i++) {
            if(timers[i]!=null) {
                Timer timer = timers[i];
                timer.update(timeStep);
                if(timer.isExpired()) {
                    timers[i] = null;
                }
            }
        }
    }
    
    /**
     * Attempts to add a timer
     * @param timer
     * @return true if the timer was added; false if there is no more room
     */
    public boolean addTimer(Timer timer) {
        for(int i = 0; i < timers.length; i++) {
            if(timers[i]==null) {
                timers[i] = timer;
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes all of the timers
     */
    public void removeTimers() {
        for(int i = 0; i < timers.length; i++) {
            timers[i] = null;
        }
    }
}
