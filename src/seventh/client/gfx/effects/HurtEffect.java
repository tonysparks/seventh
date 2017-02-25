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
 * Flashes red on the screen indicating the player has received damage
 * 
 * @author Tony
 *
 */
public class HurtEffect implements Effect {

    private float alpha;
    private Timer hurtTimer;
    /**
     * 
     */
    public HurtEffect() {
        this.hurtTimer = new Timer(false, 150);
    }
    
    public void reset() {
        reset(760);
    }
    
    public void reset(long time) {
        this.hurtTimer.reset();
        this.hurtTimer.setEndTime(time);
        this.alpha = 0.35f;        
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.hurtTimer.update(timeStep);
        this.alpha *= 0.9f;
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        if(this.hurtTimer.isUpdating()) {
            canvas.fillRect(0, 0, canvas.getWidth(), canvas.getHeight(), Colors.toColor(1.0f, 0, 0, this.alpha));
        }
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle.Effect#isDone()
     */
    @Override
    public boolean isDone() {
        boolean isDone = this.hurtTimer.isExpired();
        if(isDone) {
            this.hurtTimer.stop();
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
