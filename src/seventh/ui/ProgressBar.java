/**
 * 
 */
package seventh.ui;

import leola.frontend.listener.EventDispatcher;

/**
 * A progress bar
 * 
 * @author Tony
 *
 */
public class ProgressBar extends Widget {

    public static final int MAX_PROGRESS = 100;
    private int progress;
    
    public ProgressBar() {
        this(new EventDispatcher());
    }
    
    /**
     * @param eventDispatcher
     */
    public ProgressBar(EventDispatcher eventDispatcher) {
        super(eventDispatcher);
    }

    /**
     * The progress is from a scale of 0-100
     * 
     * @param progress the progress to set
     */
    public void setProgress(int progress) {
        this.progress = progress;
        if(this.progress < 0) {
            this.progress = 0;
        }
        if(this.progress > MAX_PROGRESS) {
            this.progress = MAX_PROGRESS;
        }
    }
    
    /**
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }
    
    /**
     * @return the percentage completed
     */
    public float getPercentCompleted() {
        return (float)progress / (float)MAX_PROGRESS;
    }
}
