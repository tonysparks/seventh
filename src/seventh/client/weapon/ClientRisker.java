/*
 * see license.txt 
 */
package seventh.client.weapon;

import seventh.client.ClientPlayerEntity;
import seventh.client.gfx.Art;
import seventh.game.weapons.Weapon.State;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientRisker extends ClientWeapon {

	private final long weaponTime = 1300;
	private long timer;
	
	/**
	 * @param ownerId
	 */
	public ClientRisker(ClientPlayerEntity owner) {
		super(owner);

		this.weaponIcon = Art.riskerIcon;
//		this.weaponImage = Art.sniperRifleImage;
		this.weaponImage = Art.riskerImage;
		this.muzzleFlash = Art.newRiskerMuzzleFlash();
		this.endFireKick = 250;			
	}

	/* (non-Javadoc)
	 * @see palisma.client.weapon.ClientWeapon#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		super.update(timeStep);
		
		if(getState() == State.READY) {			
			timer = -1;			
		}
				
		timer -= timeStep.getDeltaTime();
		
		if(getState() == State.FIRING) {
			if(timer<=0) {
				timer = weaponTime;
				//Sounds.startPlaySound(fireSound, channelId);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see palisma.client.weapon.ClientWeapon#onFire()
	 */
	@Override
	protected boolean onFire() {		
		return true;
	}
}
