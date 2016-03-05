/*
 * see license.txt 
 */
package seventh.client;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Light;
import seventh.client.gfx.particle.ExplosionEmitter;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientExplosion extends ClientEntity {
	
	private long explositionLightTime;
	private Light light;

	/**
	 * 
	 */
	public ClientExplosion(ClientGame game, Vector2f pos) {
		super(game, pos);
		
		game.addForegroundEffect(new ExplosionEmitter(pos, 4000, 0,10));
		light = game.getLightSystem().newPointLight();
		light.setColor(0.7f, 0.6f, 0.3f);
		light.setPos(pos);
		
	
		explositionLightTime = 250;
		
		setOnRemove(new OnRemove() {
			
			@Override
			public void onRemove(ClientEntity me, ClientGame game) {				
				game.getLightSystem().removeLight(light);
			}
		});
	}
		
	/* (non-Javadoc)
	 * @see palisma.client.ClientEntity#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
		
		explositionLightTime -= timeStep.getDeltaTime();
		if(explositionLightTime < 0) {
			game.getLightSystem().removeLight(light);
		}
	}

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
	}

}

