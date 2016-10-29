/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.entities.Entity.Type;
import seventh.math.Vector2f;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class Pistol extends Weapon {

	private boolean endFire;
	
	/**
	 * @param game
	 * @param owner
	 */
	public Pistol(Game game, Entity owner) {
		super(game, owner, Type.PISTOL);
	
		this.damage = 14;
		this.reloadTime = 2500;
		this.clipSize = 9;
		this.totalAmmo = 27;
		this.spread = 11;
		
		this.bulletsInClip = this.clipSize;
		
		this.weaponWeight = WeaponConstants.PISTOL_WEIGHT;		
		this.lineOfSight = WeaponConstants.PISTOL_LINE_OF_SIGHT;
	
		this.endFire = true;
		
		this.netWeapon.type = Type.PISTOL.netValue();
		applyScriptAttributes("pistol");
	}

	/* (non-Javadoc)
	 * @see palisma.game.Weapon#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);			
	}
	
	@Override
	public boolean isPrimary() {	
		return false;
	}
	
	
	@Override
	public boolean reload() {
		boolean reloaded = super.reload();
		if(reloaded) {			
			game.emitSound(getOwnerId(), SoundType.PISTOL_RELOAD, getPos());
		}
		
		return reloaded;
	}
	
	@Override
	public boolean beginFire() {
		if ( canFire() ) {
			game.emitSound(getOwnerId(), SoundType.PISTOL_FIRE, getPos());
						
			newBullet();
			bulletsInClip--;
			weaponTime = 200;
			
			setFireState(); 
			return true;
		}
		else if (bulletsInClip <= 0 ) {
			setFireEmptyState();			
		}
		
		this.endFire = false;
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Weapon#endFire()
	 */
	@Override
	public boolean endFire() {
		this.endFire = true;
		
		return false;
	}
	
	@Override
	public boolean canFire() {	
		return super.canFire() && this.endFire;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Weapon#calculateVelocity(palisma.game.Direction)
	 */
	@Override
	protected Vector2f calculateVelocity(Vector2f facing) {
		return spread(facing, spread);
	}
}
