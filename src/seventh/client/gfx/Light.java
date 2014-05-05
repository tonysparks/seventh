/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.math.Vector2f;
import seventh.math.Vector3f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public interface Light {

	public abstract TextureRegion getTexture();
	public abstract void setTexture(TextureRegion tex);
	/**
	 * @return the on
	 */
	public abstract boolean isOn();

	/**
	 * @param on the on to set
	 */
	public abstract void setOn(boolean on);

	/**
	 * @return the lightSize
	 */
	public abstract int getLightSize();

	/**
	 * @param lightSize the lightSize to set
	 */
	public abstract void setLightSize(int lightSize);

	/**
	 * @param lightOscillate the lightOscillate to set
	 */
	public abstract void setLightOscillate(boolean lightOscillate);

	/**
	 * @return the luminacity
	 */
	public abstract float getLuminacity();

	/**
	 * @param luminacity the luminacity to set
	 */
	public abstract void setLuminacity(float luminacity);

	/**
	 * @return the color
	 */
	public abstract Vector3f getColor();

	/**
	 * @param color the color to set
	 */
	public abstract void setColor(Vector3f color);

	/**
	 * @param r
	 * @param g
	 * @param b
	 */
	public abstract void setColor(float r, float g, float b);

	/**
	 * @return the orientation
	 */
	public abstract float getOrientation();

	/**
	 * @param orientation the orientation to set
	 */
	public abstract void setOrientation(float orientation);

	/**
	 * @param pos the pos to set
	 */
	public abstract void setPos(Vector2f pos);

	/**
	 * Sets the position
	 * @param x
	 * @param y
	 */
	public abstract void setPos(float x, float y);

	/**
	 * @return the pos
	 */
	public abstract Vector2f getPos();

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */
	public abstract void update(TimeStep timeStep);

}