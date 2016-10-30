/*
 * see license.txt 
 */
package seventh.client.gfx;

import java.util.ArrayList;
import java.util.List;

import seventh.client.ClientGame.ClientEntityListener;
import seventh.client.entities.ClientEntity;
import seventh.client.gfx.effects.LightEffectShader;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.math.Vector3f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * @author Tony
 *
 */
public class ImageBasedLightSystem implements LightSystem {

	private Vector3f ambientColor;
	private float ambientIntensity;
	private boolean enabled;
	
	private List<ImageLight> lights;
	
	private Shader shader;
	private Sprite framebufferTexture;
	private Rectangle cameraViewport;
	
	/**
	 * 
	 */
	public ImageBasedLightSystem() {
		this.enabled = true;
		this.lights = new ArrayList<>();
		
//		this.shader = LightEffectShader.getInstance();
		this.shader = new LightEffectShader();
		
		this.ambientColor = new Vector3f(0.3f, 0.3f, 0.7f);
		this.ambientIntensity = 0.7f;
		
		this.framebufferTexture = new Sprite(Art.BLACK_IMAGE);
		this.cameraViewport = new Rectangle();
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.FrameBufferRenderable#isExpired()
	 */
	@Override
	public boolean isExpired() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#getClientEntityListener()
	 */
	@Override
	public ClientEntityListener getClientEntityListener() {	
		return new ClientEntityListener() {
			
			@Override
			public void onEntityDestroyed(ClientEntity ent) {				
			}
			
			@Override
			public void onEntityCreated(ClientEntity ent) {				
			}
		};
	}
	
	
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#addLight(seventh.client.gfx.Light)
	 */
	@Override
	public void addLight(Light light) {
		this.lights.add( (ImageLight)light);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#newConeLight()
	 */
	@Override
	public Light newConeLight() {
		return newConeLight(new Vector2f());
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#newConeLight(seventh.math.Vector2f)
	 */
	@Override
	public Light newConeLight(Vector2f pos) {
		ImageLight light = new ImageLight(this, pos);
		light.setTexture(Art.flashLight);
		addLight(light);
		return light;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#newPointLight()
	 */
	@Override
	public Light newPointLight() {		
		return newPointLight(new Vector2f());
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#newPointLight(seventh.math.Vector2f)
	 */
	@Override
	public Light newPointLight(Vector2f pos) {
		ImageLight light = new ImageLight(this, pos);
		light.setTexture(Art.lightMap);
		addLight(light);
		
		return light;
	}

	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#removeLight(seventh.client.gfx.Light)
	 */
	@Override
	public void removeLight(Light light) {
		this.lights.remove(light);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#removeAllLights()
	 */
	@Override
	public void removeAllLights() {
		this.lights.clear();
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#destroy()
	 */
	@Override
	public void destroy() {
		removeAllLights();
		this.shader.destroy();
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#setAmbientColor(seventh.math.Vector3f)
	 */
	@Override
	public void setAmbientColor(Vector3f ambientColor) {
		this.ambientColor.set(ambientColor);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#setAmbientColor(float, float, float)
	 */
	@Override
	public void setAmbientColor(float r, float g, float b) {
		this.ambientColor.set(r, g, b);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#setAmbientIntensity(float)
	 */
	@Override
	public void setAmbientIntensity(float ambientIntensity) {
		this.ambientIntensity = ambientIntensity;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#getAmbientIntensity()
	 */
	@Override
	public float getAmbientIntensity() {
		return ambientIntensity;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.LightSystem#getAmbientColor()
	 */
	@Override
	public Vector3f getAmbientColor() {
		return ambientColor;
	}
	
		
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */	
	@Override
	public void update(TimeStep timeStep) {
		ShaderProgram shader = this.shader.getShader();
		shader.begin();
		{
//			this.ambientColor.set(0.3f, 0.3f, 0.4f);
//			this.ambientColor.set(0.9f, 0.9f, 0.9f);
//			this.ambientIntensity = 0.4f;
			
			shader.setUniformi("u_lightmap", 1);
			shader.setUniformf("ambientColor", ambientColor.x, ambientColor.y, ambientColor.z, ambientIntensity);		
			shader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		shader.end();
		
		
		for(int i = 0; i < this.lights.size(); i++) {
			Light light = this.lights.get(i);
			light.update(timeStep);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.FrameBufferRenderable#frameBufferRender(seventh.client.gfx.Canvas, seventh.client.gfx.Camera)
	 */
	@Override
	public void frameBufferRender(Canvas canvas, Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		Rectangle viewport = camera.getWorldViewPort();
		final int extended = 530;
		cameraViewport.set( (int)cameraPos.x - extended, (int)cameraPos.y - extended
								, viewport.width + extended, viewport.height + extended);
		
		/*
		canvas.setShader(this.shader.getShader());
		canvas.bindFrameBuffer(1);
		framebufferTexture.getTexture().bind(0);
		*/
		
		for(int i = 0; i < this.lights.size(); i++) {
			ImageLight light = this.lights.get(i);		
			if(light.isOn()) {
				Vector2f pos = light.getPos();
				if(cameraViewport.contains(pos)) {
					
					int rx = (int) (pos.x - cameraPos.x);
					int ry = (int) (pos.y - cameraPos.y);
								
					framebufferTexture.setRegion(light.getTexture());
					framebufferTexture.setRotation(light.getOrientation());
					int lightSize = light.getLightSize();
					framebufferTexture.setBounds(rx, ry, lightSize, lightSize);
					framebufferTexture.setOrigin(lightSize/2, lightSize/2);
					
					Vector3f color = light.getColor();
					framebufferTexture.setColor(color.x, color.y, color.z, light.getLuminacity());
					
					canvas.drawRawSprite(framebufferTexture);
				}				
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {		
		canvas.begin();		
		canvas.setShader(this.shader.getShader());
		canvas.bindFrameBuffer(1);
		Texture tex = framebufferTexture.getTexture();
		if(tex!=null) {
			tex.bind(0);	
		}
		canvas.end();	
	}

}
