/*
 *    leola-live 
 *  see license.txt
 */
package seventh.shared;


/**
 * Counts the Frames Per Seconds and average frames per second based on a sample size.
 * 
 * @author Tony
 *
 */
public class FpsCounter {

    /**
     * Sample size
     */
    private final int sampleSize;
        

    /**
     * Average fps
     */
    private long avgFPS = 0;
    
    /**
     * FPS tally
     */
    private long fpsTally = 0;
    
    /**
     * The current sample
     */
    private int currentSample = 0;
    
    /**
     * Frames per second
     */
    private long fps;

    /**
     * @param sampleSize
     */
    public FpsCounter(int sampleSize) {
        this.sampleSize = sampleSize;
    }
    
    /**
     */
    public FpsCounter() {
        this(100);
    }
                         
    
    /**
     * Update the counter.
     * 
     * @param dt
     */
    public void update(long dt) {
        if ( dt == 0 ) {
            return;
        }
        
        this.fps = 1000 / dt;
        
        this.currentSample++;
        if ( this.currentSample > this.sampleSize ) {
            this.avgFPS = (this.fpsTally / this.sampleSize); 
            this.fpsTally = 0;
            this.currentSample=0;            
        } 
        else {
            this.fpsTally += this.fps;
        }
    }

    /**
     * @return the sampleSize
     */
    public int getSampleSize() {
        return this.sampleSize;
    }

    /**
     * @return the avgFPS
     */
    public long getAvgFPS() {
        return this.avgFPS;
    }

    /**
     * @return the fps
     */
    public long getFps() {
        return this.fps;
    }
    
    
}
