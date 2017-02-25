/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.effects;

import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class AnimationEffect implements Effect {

    private AnimatedImage anim;
    private Vector2f pos;
    private boolean persist;
    private FadeValue fade;
    private float rotation;
    private Sprite sprite;
    
    private int offsetX, offsetY;
    
    /**
     * @param anim
     * @param pos
     * @param persist
     */
    public AnimationEffect(AnimatedImage anim, Vector2f pos, float rotation) {
        this(anim, pos, rotation, false);
    }
    
    /**
     * @param anim
     * @param pos
     * @param persist
     */
    public AnimationEffect(AnimatedImage anim, Vector2f pos, float rotation, boolean persist) {
        this(anim, pos, rotation, persist, 12000);
    }

    /**
     * @param anim
     * @param pos
     * @param persist
     */
    public AnimationEffect(AnimatedImage anim, Vector2f pos, float rotation, boolean persist, int fadeTime) {
        super();
        this.anim = anim;
        this.pos = pos;
        this.persist = persist;
        this.rotation = (float)(Math.toDegrees(rotation));
        this.fade = new FadeValue(255, 0, fadeTime);
        
        this.sprite = new Sprite(anim.getCurrentImage());
        this.sprite.flip(false, true);
    }
    
    public void setOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }
    
    /**
     * Reset the animation effect
     */
    public void reset(Vector2f pos, float rotation) {
        this.pos.set(pos);
        this.rotation = (float)(Math.toDegrees(rotation));
        this.anim.reset();
        this.fade.reset();
    }
    
    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        Vector2f cameraPos = camera.getRenderPosition(alpha);
        float rx = (pos.x - cameraPos.x);
        float ry = (pos.y - cameraPos.y);
        
        float currentAlpha = 1.0f;
        if(!this.persist) {
             currentAlpha = (float)this.fade.getCurrentValue() / 255.0f;
        }
        
        float priorAlpha = canvas.getCompositeAlpha();
        canvas.setCompositeAlpha(currentAlpha);
        
        TextureRegion region = anim.getCurrentImage();
        sprite.setRegion(region);
                
        if(offsetX != 0 || offsetY != 0) {
            sprite.setPosition(rx-offsetX, ry-offsetY);
            sprite.setOrigin(offsetX, offsetY);
        }
        else {
            float w = region.getRegionWidth() / 2f;
            float h = region.getRegionHeight() / 2f;
            float x = Math.round(rx-w);
            float y = Math.round(ry-h);
            sprite.setPosition(x,y);
        }
        sprite.setRotation(this.rotation-90);
        
//        sprite.setColor(1, 1, 1, alpha);
        canvas.drawSprite(sprite);
        
        canvas.setCompositeAlpha(priorAlpha);
    }
    
    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {    
        anim.update(timeStep);
        if(!this.persist) {
            this.fade.update(timeStep);
        }
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle.Effect#isDone()
     */
    @Override
    public boolean isDone() {
        return !persist && anim.isDone() && this.fade.isDone();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle.Effect#destroy()
     */
    @Override
    public void destroy() {        
    }
}
