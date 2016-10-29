/*
 * see license.txt 
 */
package seventh.game.weapons;

import leola.vm.types.LeoMap;
import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.entities.Entity.Type;
import seventh.math.Vector2f;
import seventh.shared.SoundType;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class MP40 extends Weapon {

	private int roundsPerSecond;
	private final int spread;
	
	/**
	 * @param game
	 * @param owner
	 */
	public MP40(Game game, Entity owner) {
		super(game, owner, Type.MP40);
		
		this.roundsPerSecond = 7;
		this.damage = 20;
		this.reloadTime = 1800;
		this.clipSize = 32;
		this.totalAmmo = 190;
		this.spread = 9;
		this.bulletsInClip = this.clipSize;		
		
		this.lineOfSight = WeaponConstants.MP40_LINE_OF_SIGHT;		
		this.weaponWeight = WeaponConstants.MP40_WEIGHT;
		
		this.netWeapon.type = Type.MP40.netValue();
		
		applyScriptAttributes("mp40");
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
			game.emitSound(getOwnerId(), SoundType.MP40_RELOAD, getPos());
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
			game.emitSound(getOwnerId(), SoundType.MP40_FIRE, getPos());
			
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
