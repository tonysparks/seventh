/*
 * see license.txt 
 */
package seventh.game.weapons;

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
public class RocketLauncher extends Weapon {
	
	/**
	 * @param game
	 * @param owner
	 */
	public RocketLauncher(Game game, Entity owner) {
		super(game, owner, Type.ROCKET_LAUNCHER);
		
		this.damage = 120;
		this.reloadTime = 0;
		this.clipSize = 50;
		this.totalAmmo = 0;
		this.bulletsInClip = this.clipSize;
		this.lineOfSight = WeaponConstants.RPG_LINE_OF_SIGHT;
		this.weaponWeight = WeaponConstants.RPG_WEIGHT;
		
		this.netWeapon.type = Type.ROCKET_LAUNCHER.netValue();
		
		applyScriptAttributes("rocket_launcher");
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Weapon#endFire()
	 */
	@Override
	public boolean endFire() {
		if(canFire()) {
			newRocket();
			game.emitSound(getOwnerId(), SoundType.RPG_FIRE, getPos());
			
			bulletsInClip--;
			weaponTime = 1500;
			
			setFireState();
			return true;
		}
		else if (bulletsInClip <= 0 ) {
			setFireEmptyState();
		}
//		else {
//			setWaitingState();
//		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Weapon#calculateVelocity(palisma.game.Direction)
	 */
	@Override
	protected Vector2f calculateVelocity(Vector2f facing) {	
		return facing.createClone();
	}

}
