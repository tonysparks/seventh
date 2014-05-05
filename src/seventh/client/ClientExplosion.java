/*
 * see license.txt 
 */
package seventh.client;

import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Light;
import seventh.client.gfx.particle.ExplosionEmitter;
import seventh.client.sfx.Sounds;
import seventh.game.net.NetEntity;
import seventh.game.net.NetExplosion;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientExplosion extends ClientEntity {

	private AnimatedImage anim;
	private boolean soundPlayed;	
	private long ownerId;	
	private long explositionLightTime;
	private Light light;
	/**
	 * 
	 */
	public ClientExplosion(ClientGame game, Vector2f pos) {
		super(game, pos);
		
		anim = Art.newExplosionAnim();
		anim.loop(false);
		this.soundPlayed = false;	
		
		game.addForegroundEffect(new ExplosionEmitter(pos, 800, 0));
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
	 * @see palisma.client.ClientEntity#updateState(palisma.game.net.NetEntity, long)
	 */
	@Override
	public void updateState(NetEntity state, long time) {	
		super.updateState(state, time);
		
		NetExplosion explosion = (NetExplosion)state;
		ownerId = explosion.ownerId;
		
	}
	
		
	/* (non-Javadoc)
	 * @see palisma.client.ClientEntity#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
		
		if(!soundPlayed) {
			Sounds.playSound(Sounds.explodeSnd, ownerId, pos);
			this.soundPlayed = true;
		}
		
		anim.update(timeStep);	
		
		explositionLightTime -= timeStep.getDeltaTime();
		if(explositionLightTime < 0) {
			game.getLightSystem().removeLight(light);
		}
	}

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
//		Vector2f cameraPos = camera.getPosition();
//		int x = (int)(pos.x - cameraPos.x);
//		int y = (int)(pos.y - cameraPos.y);
//		
//		canvas.drawImage(anim.getCurrentImage(), x-bounds.width/4, y-bounds.height/4, 0x3fffffff);
		//canvas.fillRect(x-bounds.width/2, y-bounds.height/2, this.bounds.width, this.bounds.height, 0x1fff0000);		
	}

}

