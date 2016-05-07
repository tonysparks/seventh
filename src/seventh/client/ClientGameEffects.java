/*
 * see license.txt 
 */
package seventh.client;

import java.util.ArrayList;
import java.util.List;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Explosion;
import seventh.client.gfx.ExplosionEffect;
import seventh.client.gfx.ExplosionEffectShader;
import seventh.client.gfx.FrameBufferRenderable;
import seventh.client.gfx.ImageBasedLightSystem;
import seventh.client.gfx.LightSystem;
import seventh.client.gfx.TankTrackMarks;
import seventh.client.gfx.particle.BloodEmitter;
import seventh.client.gfx.particle.Effect;
import seventh.client.gfx.particle.Effects;
import seventh.math.Vector2f;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * @author Tony
 *
 */
public class ClientGameEffects {

	
	private final Effects backgroundEffects, foregroundEffects;
	private final LightSystem lightSystem;
	private final ExplosionEffect explosions;

	private final List<FrameBufferRenderable> frameBufferRenderables;
	private final Sprite frameBufferSprite;
	
	private final TankTrackMarks[] trackMarks;
	
	private final BloodEmitter[] playerBloodEmitters;
	
	/**
	 */
	public ClientGameEffects() {
		this.frameBufferRenderables = new ArrayList<>();
		
		this.backgroundEffects = new Effects();
		this.foregroundEffects = new Effects();

		this.playerBloodEmitters = new BloodEmitter[SeventhConstants.MAX_PLAYERS];
		for(int i = 0; i < this.playerBloodEmitters.length; i++) {
		    this.playerBloodEmitters[i] = new BloodEmitter(new Vector2f(), 6, 5200, 4000, 0, 20);
		    this.playerBloodEmitters[i].setPersistent(true);
		    this.playerBloodEmitters[i].pause();
		    
		    this.backgroundEffects.addEffect(this.playerBloodEmitters[i]);
		}
		
		
		this.lightSystem = new ImageBasedLightSystem();		
		this.frameBufferRenderables.add(lightSystem);
		
		this.explosions = new ExplosionEffect(15, 800, 0.6f);
		this.frameBufferSprite = new Sprite();
		
		this.trackMarks = new TankTrackMarks[SeventhConstants.MAX_ENTITIES];
	}
	
	/**
	 * Retrieve the cached {@link BloodEmitter} for a particular player.  Note, this
	 * assumes the supplied playerId is valid.
	 * 
	 * @param playerId
	 * @return the {@link BloodEmitter} for a player.
	 */
	public BloodEmitter getBloodEmitterForPlayer(int playerId) {
	    return this.playerBloodEmitters[playerId];
	}
	
	/**
	 * @return the explosions
	 */
	public ExplosionEffect getExplosions() {
		return explosions;
	}
	
	/**
	 * @return the lightSystem
	 */
	public LightSystem getLightSystem() {
		return lightSystem;
	}
	
	/**
	 * Removes all lights
	 */
	public void removeAllLights() {
		lightSystem.removeAllLights();
	}
	
	/**
	 * Clear all the effects
	 */
	public void clearEffects() {		
		explosions.deactiveAll();
		backgroundEffects.clearEffects();
		foregroundEffects.clearEffects();
		
		for(int i =0; i < trackMarks.length; i++) {
			if(trackMarks[i] != null) {
				trackMarks[i].clear();
				trackMarks[i] = null;
			}
		}
		
		for(int i = 0; i < this.playerBloodEmitters.length; i++) {         
            this.playerBloodEmitters[i].reset();
            this.playerBloodEmitters[i].pause();
            
            this.backgroundEffects.addEffect(this.playerBloodEmitters[i]);
        }
	}
	
	
	/**
	 * Destroys these effects, releasing any resources
	 */
	public void destroy() {
		clearEffects();
		removeAllLights();
		
		lightSystem.destroy();
		frameBufferRenderables.clear();
		
		for(int i = 0; i < this.playerBloodEmitters.length; i++) {         
            this.playerBloodEmitters[i].destroy();
            this.playerBloodEmitters[i] = null;
        }
	}
	
	/**
	 * Adds an explosion
	 * 
	 * @param index
	 * @param pos
	 */
	public void addExplosion(ClientGame game, int index, Vector2f pos) {
		//explosions.activate(index, pos);
		this.foregroundEffects.addEffect(new Explosion(game, pos));
	}

	/**
	 * Adds a background effect
	 * 
	 * @param effect
	 */
	public void addBackgroundEffect(Effect effect) {
		this.backgroundEffects.addEffect(effect);
	}
	
	public void addLightSource(Vector2f pos) {
		this.lightSystem.newConeLight(pos);
	}
	
	/**
	 * Adds a foreground effect
	 * 
	 * @param effect
	 */
	public void addForegroundEffect(Effect effect) {
		this.foregroundEffects.addEffect(effect);
	}

	public void allocateTrackMark(int id) {
		this.trackMarks[id] = new TankTrackMarks(256);
	}
	
	public void addTankTrackMark(int id, Vector2f pos, float orientation) {
		if(this.trackMarks[id]==null) {
			this.trackMarks[id] = new TankTrackMarks(256*2);
		}
		
		this.trackMarks[id].add(pos, orientation);
	}
	
	/**
	 * Updates the special effects, etc.
	 * 
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {
		lightSystem.update(timeStep);
		
		backgroundEffects.update(timeStep);
		foregroundEffects.update(timeStep);
		explosions.update(timeStep);
				
		
		int size = frameBufferRenderables.size();
		for(int i = 0; i < size; i++) {
			FrameBufferRenderable r = this.frameBufferRenderables.get(i);
			r.update(timeStep);
		}
		
		for(int i =0; i < trackMarks.length; i++) {
			if(trackMarks[i] != null) {
				trackMarks[i].update(timeStep);
			}
		}
	}
	
	
	/**
	 * Renders to the frame buffer
	 * @param canvas
	 */
	public void preRenderFrameBuffer(Canvas canvas, Camera camera, float alpha) {
		int size = this.frameBufferRenderables.size();
		if(size>0) {
			canvas.setDefaultTransforms();
			canvas.setShader(null);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
			canvas.begin();

			for(int i = 0; i < size; i++) {
				FrameBufferRenderable r = this.frameBufferRenderables.get(i);
				r.frameBufferRender(canvas, camera, alpha);
			}
			
			canvas.end();
		}
	}
	
	public void postRenderFrameBuffer(Canvas canvas, Camera camera, float alpha) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		canvas.setDefaultTransforms();
		canvas.setShader(null);
		
		for(int i = 0; i < this.frameBufferRenderables.size(); ) {
			FrameBufferRenderable r = this.frameBufferRenderables.get(i);
			if(r.isExpired()) {
				this.frameBufferRenderables.remove(i);
			}
			else {
				r.render(canvas, camera, alpha);
				i++;
			}
		}
	}
	
	
	public void renderFrameBuffer(Canvas canvas, Camera camera, float alpha) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		canvas.setDefaultTransforms();

		frameBufferSprite.setRegion(canvas.getFrameBuffer());
		
		canvas.begin();
		{
			canvas.getFrameBuffer().bind();
			{
				ShaderProgram shader = ExplosionEffectShader.getInstance().getShader();
				
				canvas.setShader(shader);
				canvas.drawImage(frameBufferSprite, 0, 0, 0x0);
			}
		}
		canvas.end();
	}
	
		
	public void renderBackground(Canvas canvas, Camera camera, float alpha) {
		backgroundEffects.render(canvas, camera, alpha);
		
		for(int i =0; i < trackMarks.length; i++) {
			if(trackMarks[i] != null) {
				trackMarks[i].render(canvas, camera, alpha);
			}
		}
	}
	
	public void renderForeground(Canvas canvas, Camera camera, float alpha) {
		foregroundEffects.render(canvas, camera, alpha);
	}
	
	public void renderLightSystem(Canvas canvas, Camera camera, float alpha) {
		lightSystem.render(canvas, camera, alpha);
	}		
}
