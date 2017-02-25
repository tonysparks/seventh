/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class BulletCasingEffect implements Effect {

    private Timer timeToLive;
    private Vector2f pos;
    private Vector2f vel;
    private Sprite sprite;
    
    private Timer rotationTimer;
    private Timer ejectTimer;
    
    private Random random;
    private float rotation;
    private float speed;
    private float alpha;
    
    /**
     * 
     */
    public BulletCasingEffect(Random random) {
        this.random = random;
        
        this.timeToLive = new Timer(false, 25_000);
        this.timeToLive.stop();
        
        this.rotationTimer = new Timer(false, 800);
        this.ejectTimer = new Timer(false, 1400);
        
        
        this.pos = new Vector2f();
        this.vel = new Vector2f();
        
        this.sprite = new Sprite(Art.bulletShell);
        this.alpha = 0.68f;
    }
    
    public void respawn(Vector2f pos, float orientation) {
        this.pos.set(pos);
        this.timeToLive.reset();
        this.timeToLive.start();
        
        this.rotationTimer.reset();
        this.rotationTimer.setEndTime(200 + random.nextInt(200));
        
        this.ejectTimer.reset();
        this.ejectTimer.setEndTime(300 + random.nextInt(200));
        
        float angle = (float)Math.toDegrees(orientation);
        this.sprite.setRotation(angle);
        
        float dir = random.nextBoolean() ? -1 : 1;
        angle += dir * (random.nextInt(9) * 5) + random.nextInt(5);
        
        this.vel.set(0,1);
        Vector2f.Vector2fRotate(vel, Math.toRadians(angle), vel);
        
        this.rotation = 25f;
        this.speed = 8f + random.nextInt(3);
        this.alpha = 0.68f;
    }
    
    public boolean isFree() {
        return !this.timeToLive.isUpdating();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.timeToLive.update(timeStep);
        this.rotationTimer.update(timeStep);
        this.ejectTimer.update(timeStep);
        
        if(!this.rotationTimer.isExpired()) {
            this.rotation *= .9f;
        }
        else {
            this.rotation = 0f;
        }
        
        if(!this.ejectTimer.isExpired()) {
            Vector2f.Vector2fMA(pos, vel, speed, pos);
            this.speed *= .7f;            
        }
        else {
            this.alpha *= 0.996f;
        }
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        Vector2f cameraPos = camera.getRenderPosition(alpha);
        
        this.sprite.setPosition(this.pos.x-cameraPos.x, this.pos.y-cameraPos.y);        
        this.sprite.rotate(rotation);
        this.sprite.setAlpha(this.alpha);
        
        canvas.drawRawSprite(sprite);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle.Effect#isDone()
     */
    @Override
    public boolean isDone() {
        boolean isDone = this.timeToLive.isExpired();
        if(isDone) {
            this.timeToLive.stop();
        }
        return isDone;
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle.Effect#destroy()
     */
    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
