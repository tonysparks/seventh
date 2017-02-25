/*
 *    leola-live 
 *  see license.txt
 */
package seventh.client.gfx;

import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public abstract class Animation {

    /**
     * Paused
     */
    private boolean paused;

    /**
     * Loop
     */
    private boolean loop;
    
    /**
     * If the animation is completed
     */
    protected boolean isDone;

    /**
     * Constructs a new {@link Animation}.
     */
    protected Animation() {
        this.pause(false);
        this.loop(true);
    }

    /**
     * @return true if the animation has completed
     */
    public boolean isDone() {
        return isDone;
    }
    
    /**
     * Update the animation.
     *
     * @param timeStep
     */
    public abstract void update(TimeStep timeStep);

    /**
     * Set the current frame.
     *
     * @param frameNumber
     */
    public abstract void setCurrentFrame(int frameNumber);

    /**
     * @return the currentFrame
     */
    public abstract int getCurrentFrame();

    /**
     * @return the numberOfFrames
     */
    public abstract int getNumberOfFrames();

    /**
     * Reset the animation.
     */
    public abstract void reset();

    /**
     * @return the paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * @param paused the paused to set
     */
    public void pause(boolean paused) {
        this.paused = paused;
    }

    /**
     * @return the loop
     */
    public boolean isLooping() {
        return loop;
    }

    /**
     * @param loop the loop to set
     */
    public void loop(boolean loop) {
        this.loop = loop;
    }
}

