/*
 * see license.txt 
 */
package seventh.client;

import java.util.Random;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.particle.BulletImpactEmitter;
import seventh.game.net.NetBullet;
import seventh.game.net.NetEntity;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientBullet extends ClientEntity {
	
	private Vector2f oldPos;
	private Vector2f origin;
	private int ownerId;
	private int count;
	private final int trailSize;
	
	/**
	 * 
	 */
	public ClientBullet(ClientGame game, Vector2f pos) {
		super(game, pos);
		origin = new Vector2f(pos);
		oldPos = new Vector2f(pos);
				
		Random random = game.getRandom();
		trailSize = random.nextInt(2) + 1;
		
		setOnRemove(new OnRemove() {
			
			@Override
			public void onRemove(ClientEntity me, ClientGame game) {
				ClientBullet bullet = (ClientBullet)me;
				Vector2f vel = new Vector2f(); 
				Vector2f.Vector2fSubtract(bullet.getPos(), bullet.getOrigin(), vel);
				Vector2f.Vector2fNormalize(vel, vel);
				
				Vector2f pos = me.getCenterPos();
				Vector2f.Vector2fMA(pos, vel, 5.0f, pos);
				
				boolean hitWall = game.doesCollide(pos, 32, 32);
				
				if(hitWall) {
					game.addBackgroundEffect(new BulletImpactEmitter(pos, vel, 200, 0, false));
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#killIfOutdated(long)
	 */
	@Override
	public boolean killIfOutdated(long gameClock) {	
		return (gameClock - lastUpdate) > 800;
	}
	
	/**
	 * @return the ownerId
	 */
	public int getOwnerId() {
		return ownerId;
	}
	
	/**
	 * @return the origin
	 */
	public Vector2f getOrigin() {
		return origin;
	}
	
	/* (non-Javadoc)
	 * @see palisma.client.ClientEntity#updateState(palisma.game.net.NetEntity)
	 */
	@Override
	public void updateState(NetEntity state, long time) {		
		super.updateState(state,time);
	
		
		if(prevState!=null && count % trailSize == 0)					
		{
			
			oldPos.set(prevState.posX, prevState.posY);			
//			oldPos.set(state.pos);
		}
		count++;
		
		NetBullet bullet = (NetBullet)state;
		this.ownerId = bullet.ownerId;
	}

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		if(!oldPos.isZero()) {
		
			
			Vector2f cameraPos = camera.getPosition();
			int ox = (int)(oldPos.x-cameraPos.x);
			int oy = (int)(oldPos.y-cameraPos.y);
					
			int x = (int)(pos.x-cameraPos.x);
			int y = (int)(pos.y-cameraPos.y);
								
//			float oldLen = Vector2f.Vector2fLength(oldPos);
//			float posLen = Vector2f.Vector2fLength(pos);			
			
//			if(Math.abs(posLen-oldLen) > 100.0f ) 
			{												
				//canvas.drawScaledImage(Art.bullet, x, y, 16, 16, null);
				canvas.drawLine( ox, oy, x, y, 0x6fEE9A00);
			}
		}
	}

}
