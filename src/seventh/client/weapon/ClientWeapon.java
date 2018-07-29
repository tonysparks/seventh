/*
 * see license.txt 
 */
package seventh.client.weapon;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.entities.ClientPlayerEntity;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Camera;
import seventh.game.net.NetWeapon;
import seventh.game.weapons.Weapon.WeaponState;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class ClientWeapon {

    private static final long boltActionTime = 500;
    private static final long pumpActionTime = 100;
    
    protected final int channelId;
    private NetWeapon prevState, nextState;
    
    protected float beginFireKick, endFireKick;
    protected int weaponKickTime;
    protected int weaponWeight;
    private boolean startReloading;
            
    private int firstFire;
    
    protected TextureRegion weaponIcon, weaponImage;
    
    protected AnimatedImage muzzleFlash;
        
    protected final ClientPlayerEntity owner;
    
    private Timer specialReloadActionReloadTimer;
    private Timer fireWaitTimer;
    
    
    /**
     * 
     */
    public ClientWeapon(ClientPlayerEntity owner) {
        this.owner = owner;
        this.channelId = this.owner.getPlayerId();
        this.firstFire = 0;
        this.specialReloadActionReloadTimer = new Timer(false, boltActionTime);
        this.fireWaitTimer = new Timer(false, 100);
        this.weaponKickTime = 250;
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
        this.prevState = nextState;
        this.nextState = state;                        
    }
    
    /**
     * @return the firstFire
     */
    public boolean isFirstFire() {
        return firstFire==1;
    }
    
    public WeaponState getState() {
        if(this.nextState!=null) {
            WeaponState weaponState = nextState.weaponState;
            return weaponState;
        }
        return WeaponState.UNKNOWN;
    }
    
    public WeaponState getPreviousState() {
        if(this.prevState!=null) {
            WeaponState weaponState = prevState.weaponState;
            return weaponState;
        }
        return WeaponState.UNKNOWN;
    }
    
    protected boolean onFire() {
        return false;
    }
    
    public boolean isHeavyWeapon() {
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
    
    public boolean emitBulletCasing() {
        return true;
    }
    
    public boolean emitBarrelSmoke() {
        return true;
    }
    
    public boolean isSpecialReloadingAction() {
        return this.specialReloadActionReloadTimer.isUpdating();
    }
    
    /**
     * @return true if the weapon is currently in the reloading state
     */
    public boolean isReloading() {
        return getState() == WeaponState.RELOADING;
    }
    
    private void startSpecialReloadActionTimer() {
        if(isBoltAction()) this.specialReloadActionReloadTimer.setEndTime(boltActionTime);
        else if(isPumpAction()) this.specialReloadActionReloadTimer.setEndTime(pumpActionTime);
        
        this.specialReloadActionReloadTimer.reset();
        this.specialReloadActionReloadTimer.start();
    }
    
    public void update(TimeStep timeStep) {
        
        this.fireWaitTimer.update(timeStep);
        this.specialReloadActionReloadTimer.update(timeStep);
        
        if(this.nextState!=null) {
            WeaponState weaponState = nextState.weaponState;    
            if(weaponState != WeaponState.FIRING) {
                firstFire = 0;
            }
            
            if(isPumpAction() /*|| isBoltAction()*/) {
                if(startReloading) {
                    if(weaponState!=WeaponState.RELOADING) {
                        startSpecialReloadActionTimer();                        
                    }
                }
            }
                        
            switch(weaponState) {
                case FIRE_EMPTY: {                                                            
                    break;
                }
                case FIRING: {
                    firstFire++;
                    
                    if(!this.fireWaitTimer.isUpdating() && isFirstFire() && !isAutomatic()) {                        
                        this.fireWaitTimer.setEndTime(isBoltAction() ? 50 : 300);
                        this.fireWaitTimer.reset();
                        this.fireWaitTimer.start();
                        
                        if(emitBulletCasing()) {                        
                            this.owner.emitBulletCasing();
                        }
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
        
//        this.weaponKickTime = 150; 
//        this.endFireKick = 18.7f; 
//        this.beginFireKick = 0f; 
          //this.weaponKickTime = 100;
          //this.beginFireKick = 3.8f;
        
        if(getState() == WeaponState.FIRING && weaponKickTime>0) {
            if(firstFire==1) {
                camera.shakeFrom(weaponKickTime, this.owner.getFacing(), this.endFireKick);
                //camera.shake(weaponKickTime, endFireKick);
            }
            else if (beginFireKick > 0 ) {
                //camera.shake(weaponKickTime, beginFireKick);
                camera.shakeFrom(weaponKickTime, this.owner.getFacing(), this.beginFireKick);
            }
        }
    }
}
