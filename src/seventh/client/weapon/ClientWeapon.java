/*
 * see license.txt 
 */
package seventh.client.weapon;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.ClientPlayerEntity;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Camera;
import seventh.game.net.NetWeapon;
import seventh.game.weapons.Weapon.State;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class ClientWeapon {

	protected final int channelId;
	private NetWeapon prevState, nextState;
	
	protected int beginFireKick, endFireKick;
	protected int weaponWeight;
	private boolean startFiring, startReloading, canFireAgain;
			
	private int firstFire;
	
	protected TextureRegion weaponIcon, weaponImage;
	
	protected AnimatedImage muzzleFlash;
		
	protected final ClientPlayerEntity owner;
	
	private Timer specialReloadActionReloadTimer;
	private Timer fireWaitTimer;
	
	static final long boltActionTime = 1_000;
	static final long pumpActionTime = 100;
	
	/**
	 * 
	 */
	public ClientWeapon(ClientPlayerEntity owner) {
		this.owner = owner;
		this.channelId = this.owner.getPlayerId();
		this.firstFire = 0;
		this.specialReloadActionReloadTimer = new Timer(false, boltActionTime);
		this.fireWaitTimer = new Timer(false, 100);
	}
	
	/**
	 * @return the weaponWeight
	 */
	public int getWeaponWeight() {
		return weaponWeight;
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
	
	public boolean isBoltAction() {
		return false;
	}
	
	public boolean isPumpAction() {
		return false;
	}
	
	public boolean isSpecialReloadingAction() {
		return this.specialReloadActionReloadTimer.isUpdating();
	}
	
	private void startSpecialReloadActionTimer() {
		if(isBoltAction()) this.specialReloadActionReloadTimer.setEndTime(500);//boltActionTime);
		else if(isPumpAction()) this.specialReloadActionReloadTimer.setEndTime(pumpActionTime);
		
		this.specialReloadActionReloadTimer.reset();
		this.specialReloadActionReloadTimer.start();
	}
	
	public void update(TimeStep timeStep) {
		
		this.fireWaitTimer.update(timeStep);
		this.specialReloadActionReloadTimer.update(timeStep);
		
		if(this.nextState!=null) {
			State state = State.fromNet(nextState.state);	
			if(state != State.FIRING) {
				firstFire = 0;
			}
			
			if(isPumpAction() /*|| isBoltAction()*/) {
				if(startReloading) {
					if(state!=State.RELOADING) {
						startSpecialReloadActionTimer();						
					}
				}
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
					
					if(!this.fireWaitTimer.isUpdating() && firstFire == 1) {						
						this.fireWaitTimer.setEndTime(isBoltAction() ? 50 : 300);
						this.fireWaitTimer.reset();
						this.fireWaitTimer.start();
					}
					
					if(!this.specialReloadActionReloadTimer.isUpdating() && this.fireWaitTimer.isOnFirstTime()) {
						startSpecialReloadActionTimer();
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
					break;
				}
				default: {
//					startFiring = false;
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
