/*
 * see license.txt 
 */
package seventh.client.gfx.particle;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class CrubbledParticle extends ImageParticle {

	private float speed;
	private float friction;
	
	/**
	 * @param image
	 * @param pos
	 * @param vel
	 * @param rotateDegrees
	 * @param scale
	 * @param timeToLive
	 */
	public CrubbledParticle(TextureRegion image, Vector2f pos, Vector2f vel, float rotateDegrees, float scale,
			int timeToLive) {
		super(image, pos, vel, rotateDegrees, scale, timeToLive);
		
		this.speed = 5.0f;
		this.friction = 2.0f;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Particle#isAlive()
	 */
	@Override
	public boolean isAlive() {	
		return true;
	}
	
	/**
	 * @param friction the friction to set
	 */
	public void setFriction(float friction) {
		this.friction = friction;
	}
	
	/**
	 * @return the friction
	 */
	public float getFriction() {
		return friction;
	}
	
	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	/**
	 * @return the speed
	 */
	public float getSpeed() {
		return speed;
	}

	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
		
		float dt = (float)timeStep.asFraction();		
		float newX = (pos.x + vel.x * speed * dt);
		float newY = (pos.y + vel.y * speed * dt);
		
		speed -= friction;
		if(speed < 0) {
			speed = 0;
		}
		
		setPos(newX, newY);		
	}
}
