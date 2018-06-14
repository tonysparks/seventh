/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Colors;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * Flashes a color on the screen
 * 
 * @author Tony
 *
 */
public class ScreenFlashEffect implements Effect {
    
    private final int color;
    private final float startingAlpha;
    private float alpha;
    private Timer flashTimer;
    
    /**
     * 
     */
    public ScreenFlashEffect(int color, float startingAlpha, long flashTime) {
        this.color = color;
        this.startingAlpha = startingAlpha;
        this.flashTimer = new Timer(false, flashTime);
    }
    
    public void reset() {
        reset(this.flashTimer.getEndTime());
    }
    
    public void reset(long time) {
        this.flashTimer.reset();
        this.flashTimer.setEndTime(time);
        this.alpha = this.startingAlpha;        
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.flashTimer.update(timeStep);
        this.alpha *= 0.9f;
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        if(this.flashTimer.isUpdating()) {
            canvas.fillRect(0, 0, canvas.getWidth(), canvas.getHeight(), Colors.setAlpha(this.color, this.alpha));
        }
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle.Effect#isDone()
     */
    @Override
    public boolean isDone() {
        boolean isDone = this.flashTimer.isExpired();
        if(isDone) {
            this.flashTimer.stop();
        }
        return isDone;
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle.Effect#destroy()
     */
    @Override
    public void destroy() {        
    }

}
