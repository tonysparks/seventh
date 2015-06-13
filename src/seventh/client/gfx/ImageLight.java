/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.math.Vector2f;
import seventh.math.Vector3f;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * A light source
 * 
 * @author Tony
 *
 */
public class ImageLight implements Updatable, Light {

	private Vector2f pos;
	private float orientation;
	
	private float luminacity;	
	private Vector3f color;
	private boolean lightOscillate;	
		
	private TextureRegion texture;
	private int lightSize;
	
	private boolean on;
	private long flickerTime;
	private long timeToNextFlicker;
	
	private LightSystem lightSystem;
	
	public ImageLight(LightSystem lightSystem) {
		this(lightSystem, new Vector2f());
	}
	
	/**
	 * @param pos
	 */
	public ImageLight(LightSystem lightSystem, Vector2f pos) {
		this.lightSystem = lightSystem;
		this.pos = pos;
		this.orientation = 0;
		
		this.color = new Vector3f(0.3f, 0.3f, 0.7f);		
		this.luminacity = 0.5f;
		this.lightOscillate = false;
		this.on = true;
		
		this.flickerTime = 70;
		
		setTexture(Art.lightMap);
		
		lightSize = 512;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#destroy()
	 */
	@Override
	public void destroy() {
		this.lightSystem.removeLight(this);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#isOn()
	 */
	@Override
	public boolean isOn() {
		return on;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#setOn(boolean)
	 */
	@Override
	public void setOn(boolean on) {
		this.on = on;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#getLightSize()
	 */
	@Override
	public int getLightSize() {
		return lightSize;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#setLightSize(int)
	 */
	@Override
	public void setLightSize(int lightSize) {
		this.lightSize = lightSize;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#setLightOscillate(boolean)
	 */
	@Override
	public void setLightOscillate(boolean lightOscillate) {
		this.lightOscillate = lightOscillate;
	}
	
	/**
	 * @param texture the texture to set
	 */
	public void setTexture(TextureRegion texture) {
		this.texture = texture;
	}
	
	/**
	 * @return the texture
	 */
	public TextureRegion getTexture() {
		return texture;
	}
	
	/**
	 * @param flickerTime the flickerTime to set
	 */
	public void setFlickerTime(long flickerTime) {
		this.flickerTime = flickerTime;
	}
	
	/**
	 * @return the flickerTime
	 */
	public long getFlickerTime() {
		return flickerTime;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#getLuminacity()
	 */
	@Override
	public float getLuminacity() {
		return luminacity;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#setLuminacity(float)
	 */
	@Override
	public void setLuminacity(float luminacity) {
		this.luminacity = luminacity;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#getColor()
	 */
	@Override
	public Vector3f getColor() {
		return color;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#setColor(seventh.math.Vector3f)
	 */
	@Override
	public void setColor(Vector3f color) {
		this.color.set(color);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#setColor(float, float, float)
	 */
	@Override
	public void setColor(float r, float g, float b) {
		this.color.set(r, g, b);
	}
	
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#getOrientation()
	 */
	@Override
	public float getOrientation() {
		return orientation;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#setOrientation(float)
	 */
	@Override
	public void setOrientation(float orientation) {
		this.orientation = (float)Math.toDegrees(orientation) + 90;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#setPos(seventh.math.Vector2f)
	 */
	@Override
	public void setPos(Vector2f pos) {		
		this.pos.set(pos);
		this.pos.x -= lightSize/2;
		this.pos.y -= lightSize/2;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#setPos(float, float)
	 */
	@Override
	public void setPos(float x, float y) {
		this.pos.set(x,y);
		this.pos.x -= lightSize/2;
		this.pos.y -= lightSize/2;
	}
	
	/**
	 * Sets the absolute position (the other setPos functions account 
	 * for the lightSize)
	 * 
	 * @param pos
	 */
	public void setAbsolutePos(Vector2f pos) {
		this.pos.set(pos);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Light#getPos()
	 */
	@Override
	public Vector2f getPos() {
		return pos;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */	
	@Override
	public void update(TimeStep timeStep) {		
		if(this.lightOscillate) {
			this.timeToNextFlicker -= timeStep.getDeltaTime();
			if(this.timeToNextFlicker < 0) {
				this.setOn(!isOn());
				this.timeToNextFlicker = this.flickerTime;
			}
		}
	}

}
