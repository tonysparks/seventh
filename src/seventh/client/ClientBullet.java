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
	private int trailSize;
	
	private Vector2f renderPosStart, renderPosEnd;
	
	static class BulletOnRemove implements OnRemove {
		private Vector2f vel = new Vector2f();
		
		@Override
		public void onRemove(ClientEntity me, ClientGame game) {
			ClientBullet bullet = (ClientBullet)me;			 
			Vector2f.Vector2fSubtract(bullet.getPos(), bullet.getOrigin(), vel);
			Vector2f.Vector2fNormalize(vel, vel);
			
			Vector2f pos = me.getCenterPos();
			Vector2f.Vector2fMA(pos, vel, 5.0f, pos);
//			
//			boolean hitWall = game.doesCollide(pos, 32, 32);
//			
//			if(hitWall) {
//				game.addBackgroundEffect(new BulletImpactEmitter(pos, vel, 200, 0, false));
//			}
			
			if(!game.doesEntityTouchOther(me)) {
				game.addBackgroundEffect(new BulletImpactEmitter(pos, vel, 200, 0, false));
			}
			
		}
	}
	
	/**
	 * @param game
	 * @param pos
	 */
	public ClientBullet(ClientGame game, Vector2f pos) {
		super(game, pos);
		origin = new Vector2f(pos);
		oldPos = new Vector2f(pos);
		
		renderPosEnd = new Vector2f();
		renderPosStart = new Vector2f();
				
		Random random = game.getRandom();
		trailSize = random.nextInt(2) + 1;
		
		setOnRemove(new BulletOnRemove());
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#reset()
	 */
	@Override
	public void reset() {	
		super.reset();
		
		origin.zeroOut();
		oldPos.zeroOut();
		
		ownerId = 0;
		count = 0;
		trailSize = game.getRandom().nextInt(2) + 1;
		
		lastUpdate = 0;

		prevState = null;
		nextState = null;
	}
	
	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Vector2f origin) {
		this.pos.set(origin);
		this.origin.set(origin);
		this.oldPos.set(origin);		
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

			renderPosStart.x = (oldPos.x-cameraPos.x);
			renderPosStart.y = (oldPos.y-cameraPos.y);
					
			renderPosEnd.x = (pos.x-cameraPos.x);
			renderPosEnd.y = (pos.y-cameraPos.y);
				
			canvas.drawLine( (int)renderPosStart.x, (int)renderPosStart.y, (int)renderPosEnd.x, (int)renderPosEnd.y, 0x6fEE9A00);
			
			Vector2f.Vector2fSubtract(renderPosStart, 1.0f, renderPosStart);
			Vector2f.Vector2fSubtract(renderPosEnd, 1.0f, renderPosEnd);
			canvas.drawLine( (int)renderPosStart.x, (int)renderPosStart.y, (int)renderPosEnd.x, (int)renderPosEnd.y, 0x51ffff00);
			
			Vector2f.Vector2fSubtract(renderPosStart, -1.0f, renderPosStart);
			Vector2f.Vector2fSubtract(renderPosEnd, -1.0f, renderPosEnd);
			canvas.drawLine( (int)renderPosStart.x, (int)renderPosStart.y, (int)renderPosEnd.x, (int)renderPosEnd.y, 0x51ffff00);
				
		}
	}

}
