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
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class MP44 extends Weapon {

	private int burst, burstCount;
	private boolean firing;	
	private boolean endFire;
	private long burstRate, burstTime;
	
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
		this.burst = 3;
		this.burstRate=70;		
		this.bulletsInClip = this.clipSize;						
		
		this.lineOfSight = WeaponConstants.MP44_LINE_OF_SIGHT;	
		this.weaponWeight = WeaponConstants.MP44_WEIGHT;
		
		this.endFire = true;
		
		this.netWeapon.type = Type.MP44.netValue();
		
		applyScriptAttributes("mp44");
		this.burstCount = burst;
	}

	/* (non-Javadoc)
	 * @see seventh.game.weapons.Weapon#applyScriptAttributes(java.lang.String)
	 */
	@Override
	protected LeoMap applyScriptAttributes(String weaponName) {
		LeoMap attributes = super.applyScriptAttributes(weaponName);
		if(attributes!=null) {
			this.burst = attributes.getInt("burst");
			this.burstRate = attributes.getInt("burst_rate");
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
	 * @see seventh.game.weapons.Weapon#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
	
		super.update(timeStep);
		
		if(firing) {
			burstTime-=timeStep.getDeltaTime();
			if(burstTime<=0 && bulletsInClip>0) {
				game.emitSound(getOwnerId(), SoundType.MP40_FIRE, getPos());
				newBullet(false);
				bulletsInClip--;
				burstCount--;
				burstTime=burstRate;
				if(burstCount<=0) {
					burstCount = burst;
					this.firing = false;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Weapon#beginFire()
	 */
	@Override
	public boolean beginFire() {
		if(canFire()) {			
			weaponTime = 500;
			this.firing = true;
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
