/*
 * see license.txt 
 */
package seventh.game;

import seventh.math.FastMath;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * @author Tony
 *
 */
public class SmoothOrientation implements Updatable {

	private float desiredOrientation;
	private float orientation;
	private Vector2f facing;
	
	private double movementSpeed;
	private boolean moved;
	
	public SmoothOrientation(double movementSpeed) {
		this.facing = new Vector2f();
		this.movementSpeed = movementSpeed;
		this.moved = false;
	}
	
	/**
	 * @return the desiredOrientation
	 */
	public float getDesiredOrientation() {
		return desiredOrientation;
	}
	
	/**
	 * @return the orientation
	 */
	public float getOrientation() {
		return orientation;
	}
	
	/**
	 * @return the movementSpeed
	 */
	public double getMovementSpeed() {
		return movementSpeed;
	}
	
	/**
	 * @param movementSpeed the movementSpeed to set
	 */
	public void setMovementSpeed(double movementSpeed) {
		this.movementSpeed = movementSpeed;
	}
	
	/**
	 * @param desiredOrientation the desiredOrientation to set
	 */
	public void setDesiredOrientation(float desiredOrientation) {
		final float fullCircle = FastMath.fullCircle;
		if(desiredOrientation < 0) {
			desiredOrientation += fullCircle;
		}
		this.desiredOrientation = desiredOrientation;
	}
	
	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(float orientation) {
		this.orientation = orientation;
	}
	
	/**
	 * @return the facing
	 */
	public Vector2f getFacing() {
		return facing;
	}
	
	public boolean moved() {
		return this.moved;
	}
	
	
	protected boolean updateOrientation(TimeStep timeStep) {
		final float fullCircle = FastMath.fullCircle;
		float deltaOrientation = (this.desiredOrientation-this.orientation);
		float deltaOrientationAbs = Math.abs(deltaOrientation);
		
		if(deltaOrientationAbs > 0.001f) {					
			if(deltaOrientationAbs > (fullCircle/2) ) {
				deltaOrientation *= -1;
			}
			
			if(deltaOrientation != 0) {
				float direction = deltaOrientation / deltaOrientationAbs;
				
				this.orientation += (direction * Math.min(movementSpeed, deltaOrientationAbs));
				if(this.orientation < 0) {
					this.orientation = fullCircle + this.orientation;
				}
				this.orientation %= fullCircle;
			}
		
			this.facing.set(1, 0); // make right vector
			Vector2f.Vector2fRotate(this.facing, this.orientation, this.facing);
			return true;
		}		
		return false;
	}

	@Override
	public void update(TimeStep timeStep) {
		this.moved = updateOrientation(timeStep);
	}

}
