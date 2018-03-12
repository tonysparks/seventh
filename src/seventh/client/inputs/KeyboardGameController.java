/*
 * see license.txt
 */
package seventh.client.inputs;

import seventh.client.gfx.Cursor;
import seventh.client.screens.InGameScreen.Actions;
import seventh.shared.TimeStep;

/**
 * Handles Keyboard game input
 * 
 * @author Tony
 *
 */
public class KeyboardGameController extends Inputs implements GameController {

    /*
     * (non-Javadoc)
     * @see seventh.client.GameController#pollInputs(seventh.shared.TimeStep, seventh.client.KeyMap, seventh.client.gfx.Cursor, int)
     */
    @Override
    public int pollInputs(TimeStep timeStep, KeyMap keyMap, Cursor cursor, int inputKeys) {
        if(isKeyOrButtonDown(keyMap.getWalkKey())) {
            inputKeys |= Actions.WALK.getMask();
        }
        
        if(isKeyOrButtonDown(keyMap.getCrouchKey())) {
            inputKeys |= Actions.CROUCH.getMask();
        }
        
        if(isKeyOrButtonDown(keyMap.getSprintKey())) {
            inputKeys |= Actions.SPRINT.getMask();
        }
        
        if(isKeyOrButtonDown(keyMap.getUseKey())) {
            inputKeys |= Actions.USE.getMask();
        }
        
        if(isKeyOrButtonDown(keyMap.getDropWeaponKey())) {
            inputKeys |= Actions.DROP_WEAPON.getMask();
        }
        
        if(isKeyOrButtonDown(keyMap.getMeleeAttackKey())) {
            inputKeys |= Actions.MELEE_ATTACK.getMask();
        }
        
        if(isKeyOrButtonDown(keyMap.getReloadKey()) ) {
            inputKeys |= Actions.RELOAD.getMask();
        }
        
        if(isKeyOrButtonDown(keyMap.getIronSightsKey())) {
            inputKeys |= Actions.IRON_SIGHTS.getMask();
            inputKeys |= Actions.WALK.getMask();
        }
        
        if(isKeyOrButtonDown(keyMap.getFireKey())) {
            inputKeys |= Actions.FIRE.getMask();
        }
        
        if(isKeyOrButtonDown(keyMap.getThrowGrenadeKey())) {
            inputKeys |= Actions.THROW_GRENADE.getMask();
        }
        
        
        if(isKeyDown(keyMap.getUpKey())) {
            inputKeys |= Actions.UP.getMask();
        }
        else if(isKeyDown(keyMap.getDownKey())) {
            inputKeys |= Actions.DOWN.getMask();
        }
        
        if(isKeyDown(keyMap.getLeftKey())) {
            inputKeys |= Actions.LEFT.getMask();
        }
        else if(isKeyDown(keyMap.getRightKey())) {
            inputKeys |= Actions.RIGHT.getMask();
        }
        

        
        return inputKeys;
    }
    
    private boolean isKeyOrButtonDown(int key) {
        if(key < 5) {
            return isButtonDown(key);
        }
        
        return isKeyDown(key);
    }
}
