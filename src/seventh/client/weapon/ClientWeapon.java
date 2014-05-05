/*
 * see license.txt 
 */
package seventh.client.weapon;

import seventh.client.ClientPlayerEntity;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Camera;
import seventh.client.sfx.Sounds;
import seventh.game.net.NetWeapon;
import seventh.game.weapons.Weapon.State;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class ClientWeapon {

	protected final int channelId;
	private NetWeapon prevState, nextState;
	
	protected int beginFireKick, endFireKick;
	private boolean startFiring, startSwitch, startReloading, canFireAgain;
			
	private int firstFire;
	
	protected TextureRegion weaponIcon, weaponImage;
	
	protected AnimatedImage muzzleFlash;
		
	protected final ClientPlayerEntity owner;
	/**
	 * 
	 */
	public ClientWeapon(ClientPlayerEntity owner) {
		this.owner = owner;
		this.channelId = this.owner.getPlayerId();
		this.firstFire = 0;
	}
	
	public int getAmmoInClip() {
		return (nextState!=null) ? nextState.ammoInClip : 0;
	}
	
	public int getTotalAmmo() {
		return (nextState!=null) ? nextState.totalAmmo : 0;
	}
	
	/**
	 * @return the weaponIcon
	 */
	public TextureRegion getWeaponIcon() {
		return weaponIcon;
	}
	
	/**
	 * @return the weaponImage
	 */
	public TextureRegion getWeaponImage() {
		return weaponImage;
	}
	
	/**
	 * @return the muzzle flash animation
	 */
	public AnimatedImage getMuzzleFlash() {
		return muzzleFlash;
	}
	
	/**
	 * @return the nextState
	 */
	protected NetWeapon getNextState() {
		return nextState;
	}
	
	/**
	 * @return the prevState
	 */
	protected NetWeapon getPrevState() {
		return prevState;
	}
	

	public void updateState(NetWeapon state, long time) {
//		if(getPreviousState() != State.FIRING 
//			&& State.fromNet(state.state) == State.FIRING) {
//			firstFire++;
//		}
						
		this.prevState = nextState;
		this.nextState = state;						
	}
	
	/**
	 * @return the firstFire
	 */
	public boolean isFirstFire() {
		return firstFire==1;
	}
	
	public State getState() {
		if(this.nextState!=null) {
			State state = State.fromNet(nextState.state);
			return state;
		}
		return State.UNKNOWN;
	}
	
	public State getPreviousState() {
		if(this.prevState!=null) {
			State state = State.fromNet(prevState.state);
			return state;
		}
		return State.UNKNOWN;
	}
	
	protected boolean onFire() {
		return false;
	}
	
	public boolean isAutomatic() {
		return false;
	}
	
	public boolean hasLongBarrel() {
		return true;
	}
	
	public boolean isBurstFire() {
		return false;
	}
	
	
	public void update(TimeStep timeStep) {
		if(this.nextState!=null) {
			State state = State.fromNet(nextState.state);	
			if(state != State.FIRING) {
				firstFire = 0;
			}
			
			switch(state) {
				case FIRE_EMPTY: {															
					break;
				}
				case FIRING: {
					firstFire++;
					if(!onFire()) {
						
						if(canFireAgain) {									
							canFireAgain = false;
						}
						else {
								
						}
					}
					break;
				}
				case RELOADING: {
					if(!startReloading) {						
						startReloading = true;
					}
					break;
				}
				case SWITCHING: {
					if(!startSwitch) {
						Sounds.startPlaySound(Sounds.weaponSwitch, channelId,this.owner.getCenterPos());
					}
					startSwitch = true;
					break;
				}
				default: {
//					startFiring = false;
					startSwitch = false;
					canFireAgain = true;
					
					startReloading = false;					
				}
			}
		}
	}
	
	/**
	 * Kicks the camera if firing
	 * @param camera
	 */
	public void cameraKick(Camera camera) {
		if(getState() == State.FIRING) {
			if(startFiring) {
				camera.shake(250, endFireKick);
			}
			else if (beginFireKick > 0 ) {
				camera.shake(250, beginFireKick);
			}
		}
	}
}
