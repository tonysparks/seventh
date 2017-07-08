/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * An award was achieved
 * 
 * @author Tony
 *
 */
public class AwardEffect implements Effect {

    private String text;
    private TextureRegion icon;
    private Timer showTimer;
    
    private Vector2f startPos, endPos;
    private Vector2f pos, destPos;    
    private Integer color;
    
    private float slideSpeed;

    /**
     * @param icon
     * @param text
     * @param time
     * @param startPos
     * @param endPos
     */
    public AwardEffect(TextureRegion icon, 
                       String text, 
                       long time, 
                       Vector2f startPos, 
                       Vector2f endPos) {
        this(icon, text, time, startPos, endPos, 0xffffffff);
    }
    
    
    /**
     * @param icon
     * @param text
     * @param time
     * @param startPos
     * @param endPos
     * @param color
     */
    public AwardEffect(TextureRegion icon, 
                       String text, 
                       long time, 
                       Vector2f startPos, 
                       Vector2f endPos,
                       Integer color) {
        this.icon = icon;
        this.text = text;
        this.startPos = startPos;
        this.endPos = endPos;
        this.color = color;
        
        this.showTimer = new Timer(false, time).stop();
        
        this.pos = new Vector2f(startPos);
        this.destPos = new Vector2f(endPos);
        
        this.slideSpeed = 0.5f;
                
    }

    @Override
    public void update(TimeStep timeStep) {
        if(!showTimer.isUpdating() && Vector2f.Vector2fApproxEquals(pos, endPos)) {
            pos.set(endPos);
            showTimer.start();
            slideSpeed = 0.15f;
        }
        
        showTimer.update(timeStep);
        
        if(showTimer.isOnFirstTime()) {
            this.destPos.set(startPos);
        }
        
        Vector2f.Vector2fInterpolate(pos, destPos, this.slideSpeed, pos);
    }

    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        canvas.resizeFont(16f);
        RenderFont.drawShadedString(canvas, text, pos.x + icon.getRegionWidth() + 10, pos.y, 0xffffffff);
        canvas.drawImage(icon, pos.x, pos.y - (icon.getRegionHeight()/2 + canvas.getHeight("W")/2), this.color);
    }

    @Override
    public boolean isDone() {        
        return Vector2f.Vector2fApproxEquals(pos, startPos) && showTimer.isExpired();
    }

    @Override
    public void destroy() {
    }

}
