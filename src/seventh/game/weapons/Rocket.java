/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Entity;
import seventh.game.Game;
import seventh.game.net.NetBullet;
import seventh.game.net.NetRocket;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class Rocket extends Bullet {
	
	private NetRocket netRocket;
	/**
	 * @param position
	 * @param speed
	 * @param game
	 * @param owner
	 * @param targetVel
	 * @param damage
	 * @param splashDamage
	 */
	public Rocket(final Vector2f position, 
				  final int speed, 
				  final Game game, 
				  final Entity owner, 
				  final Vector2f targetVel, 
				  final int damage,
				  final int splashDamage) {
		super(position, speed, game, owner, targetVel, damage, false );
		
		this.setOrientation(owner.getOrientation());
		this.bounds.width = 8;
		this.bounds.height = 10;
		
		this.netRocket = new NetRocket();
		
		setType(Type.ROCKET); 
		
		this.onKill = new KilledListener() {
			
			@Override
			public void onKill(Entity entity, Entity killer) {				
				final int splashWidth = 20;
				final int maxSpread = 25;
				game.newBigExplosion(position, owner, splashWidth, maxSpread, splashDamage);
			}
		};
	}
	
	/**
	 * Do nothing, rockets shouldn't make impact sounds (the 
	 * explosions do that for us)
	 */
	@Override
	protected void emitImpactSound(int x, int y) {
	}
		
	/* (non-Javadoc)
	 * @see seventh.game.weapons.Bullet#getNetBullet()
	 */
	@Override
	public NetBullet getNetBullet() {	
		this.setNetEntity(netRocket);
		return netRocket;
	}
	
}
