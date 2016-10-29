/*
 * see license.txt 
 */
package seventh.client.entities;

import java.util.Random;

import seventh.client.ClientGame;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.particle.BulletImpactEmitter;
import seventh.game.net.NetBullet;
import seventh.game.net.NetEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientBullet extends ClientEntity {
	
	private Vector2f oldPos;
	private Vector2f origin;
	private int ownerId;
		
	private Vector2f vel;
	private int tracerLength;
	
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
		
		vel = new Vector2f();
		
		renderPosEnd = new Vector2f();
		renderPosStart = new Vector2f();
						
		bounds.width = 5;
		bounds.height = 5;
		
		reset();
		
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
		vel.zeroOut();
		
		ownerId = 0;		
		lastUpdate = 0;

		prevState = null;
		nextState = null;
		
		Random rand = game.getRandom();
		tracerLength = 50 + rand.nextInt(30);
	}
	
	public void setId(int id) {
		this.id = id;
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
			
		NetBullet bullet = (NetBullet)state;
		this.ownerId = bullet.ownerId;
		

		
		if(vel.isZero()) {			
			/*ClientPlayer player = game.getPlayers().getPlayer(bullet.ownerId);
			if(player!=null&&player.isAlive()) {
				Vector2f.Vector2fCopy(player.getEntity().getFacing(), vel);
			}
			else*/ 
			{
				Vector2f.Vector2fSubtract(getPos(), getOrigin(), vel);
				Vector2f.Vector2fNormalize(vel, vel);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		super.update(timeStep);
		
		
		Vector2f.Vector2fSubtract(getPos(), getOrigin(), vel);
		Vector2f.Vector2fNormalize(vel, vel);
		if(Vector2f.Vector2fDistanceSq(getOrigin(), getPos()) > 20 ) {
			Vector2f.Vector2fMS(getPos(), vel, tracerLength, oldPos);
		}
	}

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		//if(!oldPos.isZero()) 
		{
			Vector2f cameraPos = camera.getRenderPosition(alpha);

			renderPosStart.x = (oldPos.x-cameraPos.x);
			renderPosStart.y = (oldPos.y-cameraPos.y);
					
			renderPosEnd.x = (pos.x-cameraPos.x);
			renderPosEnd.y = (pos.y-cameraPos.y);
				
			canvas.drawLine(renderPosStart.x, renderPosStart.y, renderPosEnd.x, renderPosEnd.y, 0x6fEE9A00);
			
			Vector2f.Vector2fSubtract(renderPosStart, 1.0f, renderPosStart);
			Vector2f.Vector2fSubtract(renderPosEnd, 1.0f, renderPosEnd);
			canvas.drawLine(renderPosStart.x, renderPosStart.y, renderPosEnd.x, renderPosEnd.y, 0x51ffff00);
			
			Vector2f.Vector2fSubtract(renderPosStart, -1.0f, renderPosStart);
			Vector2f.Vector2fSubtract(renderPosEnd, -1.0f, renderPosEnd);
			canvas.drawLine(renderPosStart.x, renderPosStart.y, renderPosEnd.x, renderPosEnd.y, 0x51ffff00);
		}
	}

}
