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
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class Risker extends Weapon {

		
	private boolean reloading;
	private boolean wasReloading;
	private boolean endFire;
	
	private int burst, burstCount;
	private boolean firing;	
	private long burstRate, burstTime;
	/**
	 * @param game
	 * @param owner
	 */
	public Risker(Game game, Entity owner) {
		super(game, owner, Type.RISKER);
	
		this.damage = 34;
//		this.reloadTime = 1200;
		this.reloadTime = 3000;
		this.clipSize = 21;
		this.totalAmmo = 42;
		this.spread = 6;
		this.burst = 3;
		this.burstRate=70;		
		this.bulletsInClip = this.clipSize;
		
		this.weaponWeight = WeaponConstants.RISKER_WEIGHT;		
		this.lineOfSight = WeaponConstants.RISKER_LINE_OF_SIGHT;
		
		this.reloading = false;	
		this.endFire = true;
		
		this.netWeapon.type = Type.RISKER.netValue();
		
		applyScriptAttributes("risker");
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
	 * @see palisma.game.Weapon#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
		
		if (reloading && bulletsInClip < clipSize) {
			reload();
		}
		else {
			reloading = false;
			
			if(wasReloading) {
			//	game.emitSound(getOwnerId(), SoundType.RISKER_RECHAMBER, getPos());
				wasReloading = false;
			}
		}
		
		if(firing) {
			burstTime-=timeStep.getDeltaTime();
			if(burstTime<=0 && bulletsInClip>0) {
				game.emitSound(getOwnerId(), SoundType.RISKER_FIRE, getPos());
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
	 * @see palisma.game.Weapon#reload()
	 */
	@Override
	public boolean reload() {
		/*if (bulletsInClip < clipSize && weaponTime <= 0) {
			if ( totalAmmo > 0) {
				
				weaponTime = reloadTime;
				bulletsInClip++;
				totalAmmo--;
								
				reloading = true;
				wasReloading = true;
				
				setReloadingState();
				game.emitSound(getOwnerId(), SoundType.RISKER_RELOAD, getPos());
				
				return true;
			}
		}*/		

		boolean reloaded = super.reload();
		if(reloaded) {
			game.emitSound(getOwnerId(), SoundType.RISKER_RELOAD, getPos());
		}
		
		return reloaded;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Weapon#beginFire()
	 */
	@Override
	public boolean beginFire() {
		if ( canFire() ) {
			//game.emitSound(getOwnerId(), SoundType.RISKER_FIRE, getPos());
			//game.emitSound(getOwnerId(), SoundType.RISKER_RECHAMBER, getPos());
			
			//for(int i = 0; i < 3; i++) {
			//	newBullet(false);
			//	bulletsInClip--;
			//}
			weaponTime = 900;
			this.firing = true;
			setFireState(); 
			return true;
		}
		else if (reloading) {
			reloading = false;
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
