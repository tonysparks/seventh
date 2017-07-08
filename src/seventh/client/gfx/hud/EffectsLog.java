/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.hud;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.client.gfx.effects.Effect;
import seventh.client.gfx.effects.Effects;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class EffectsLog  implements Renderable {
    
    private Effects logs;
        
    private final int maxLogEntries;
    private final int startX, startY;
        
    /**
     * @param x
     * @param y
     * @param bleedOffTime
     * @param maxLogEntries
     */
    public EffectsLog(int x, int y, int maxLogEntries) {
        this.startX = x;
        this.startY = y;
        
        this.maxLogEntries = maxLogEntries;
                    
        this.logs = new Effects();
    }
       
    public Vector2f nextOffset() {
        Vector2f pos = new Vector2f(startX, startY);
        pos.y += ((logs.size()+1) * 50);
        return pos;
    }
    
    /**
     * @param message
     * @param color
     */
    public void addEffect(Effect effect) {
        if (logs.size() > maxLogEntries) {
            expireEntry();
        }
        
        this.logs.addEffect(effect); 
    }

    public void clearLogs() {
        logs.clearEffects();
    }
    
    protected void expireEntry() {        
    }
    
    @Override
    public void update(TimeStep timeStep) {                
        this.logs.update(timeStep);
    }
        
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {        
        this.logs.render(canvas, camera, alpha);        
    }
}
