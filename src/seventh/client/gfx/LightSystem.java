/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.client.ClientGame.ClientEntityListener;
import seventh.math.Vector2f;
import seventh.math.Vector3f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public interface LightSystem extends FrameBufferRenderable {

	static enum LightType {
		CONE,
		POSITIONAL,
		IMAGE,
	}
	
	/**
	 * @return the {@link ClientEntityListener}
	 */
	public ClientEntityListener getClientEntityListener();
	
	/**
	 * Registers the light.  Use if {@link ImageBasedLightSystem#addLight(ImageLight)} was not used
	 * to construct the light.
	 * 
	 * @param light
	 */
	public abstract void addLight(Light light);

	/**
	 * Creates a new Light source
	 * @return a {@link Light}
	 */
	public abstract Light newPointLight();

	/**
	 * Creates a new Light source
	 * @param pos
	 * @return a {@link Light}
	 */
	public abstract Light newPointLight(Vector2f pos);

	/**
	 * Creates a new Light source
	 * @return a {@link Light}
	 */
	public abstract Light newConeLight();

	/**
	 * Creates a new Light source
	 * @param pos
	 * @return a {@link Light}
	 */
	public abstract Light newConeLight(Vector2f pos);
	
	/**
	 * Removes the light from the world
	 * @param light
	 */
	public abstract void removeLight(Light light);

	/**
	 * Remove all lights
	 */
	public abstract void removeAllLights();

	/**
	 * Destroys the light system, freeing resources
	 */
	public abstract void destroy();

	/**
	 * @return the enabled
	 */
	public abstract boolean isEnabled();

	/**
	 * @param enabled the enabled to set
	 */
	public abstract void setEnabled(boolean enabled);

	/**
	 * @param ambientColor the ambientColor to set
	 */
	public abstract void setAmbientColor(Vector3f ambientColor);

	public abstract void setAmbientColor(float r, float g, float b);

	/**
	 * @param ambientIntensity the ambientIntensity to set
	 */
	public abstract void setAmbientIntensity(float ambientIntensity);

	/**
	 * @return the ambientIntensity
	 */
	public abstract float getAmbientIntensity();

	/**
	 * @return the ambientColor
	 */
	public abstract Vector3f getAmbientColor();

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */
	public abstract void update(TimeStep timeStep);

	/* (non-Javadoc)
	 * @see seventh.client.gfx.FrameBufferRenderable#frameBufferRender(seventh.client.gfx.Canvas, seventh.client.gfx.Camera)
	 */
	public abstract void frameBufferRender(Canvas canvas, Camera camera);

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	public abstract void render(Canvas canvas, Camera camera, long alpha);

}