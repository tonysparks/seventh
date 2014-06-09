/*
 * see license.txt 
 */
package seventh.game.weapons;

import leola.vm.types.LeoMap;
import seventh.game.Entity;
import seventh.game.Entity.Type;
import seventh.game.Game;
import seventh.game.SoundType;
import seventh.math.Vector2f;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class MP44 extends Weapon {

	private int roundsPerSecond;
	
	/**
	 * @param game
	 * @param owner
	 */
	public MP44(Game game, Entity owner) {
		super(game, owner, Type.MP44);
		
		this.damage = 34;
//		this.reloadTime = 1200;
		this.reloadTime = 3000;
		this.clipSize = 21;
		this.totalAmmo = 42;
		this.spread = 6;
		this.bulletsInClip = this.clipSize;						
		
		this.lineOfSight = WeaponConstants.MP44_LINE_OF_SIGHT;	
		this.weaponWeight = WeaponConstants.MP44_WEIGHT;
		this.netWeapon.type = Type.MP44.netValue();
		
		applyScriptAttributes("mp44");
	}

	/* (non-Javadoc)
	 * @see seventh.game.weapons.Weapon#applyScriptAttributes(java.lang.String)
	 */
	@Override
	protected LeoMap applyScriptAttributes(String weaponName) {
		LeoMap attributes = super.applyScriptAttributes(weaponName);
		if(attributes!=null) {
			this.roundsPerSecond = attributes.getInt("rounds_per_second");
		}
		return attributes;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Weapon#reload()
	 */
	@Override
	public boolean reload() {	
		boolean reloaded = super.reload();
		if(reloaded) {
			game.emitSound(getOwnerId(), SoundType.MP44_RELOAD, getPos());
		}
		
		return reloaded;
	}
		
	/* (non-Javadoc)
	 * @see palisma.game.Weapon#beginFire()
	 */
	@Override
	public boolean beginFire() {
		if(canFire()) {
			newBullet();
			game.emitSound(getOwnerId(), SoundType.MP44_FIRE, getPos());
			
			weaponTime = 1000/roundsPerSecond;
			bulletsInClip--;
			
			setFireState(); 
			return true;
		}
		else if (bulletsInClip <= 0 ) {				
			setFireEmptyState();			
		}

		return false;		
	}

	/* (non-Javadoc)
	 * @see palisma.game.Weapon#calculateVelocity(palisma.game.Direction)
	 */
	@Override
	protected Vector2f calculateVelocity(Vector2f facing) {	
		return spread(facing, spread);
	}
		
}
