/*
 * see license.txt
 */
package seventh.client;

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
        if(isKeyDown(keyMap.getWalkKey())) {
            inputKeys |= Actions.WALK.getMask();
        }
        
        if(isKeyDown(keyMap.getCrouchKey())) {
            inputKeys |= Actions.CROUCH.getMask();
        }
        
        if(isKeyDown(keyMap.getSprintKey())) {
            inputKeys |= Actions.SPRINT.getMask();
        }
        
        if(isKeyDown(keyMap.getUseKey())) {
            inputKeys |= Actions.USE.getMask();
        }
        
        if(isKeyDown(keyMap.getDropWeaponKey())) {
            inputKeys |= Actions.DROP_WEAPON.getMask();
        }
        
        if(isKeyDown(keyMap.getMeleeAttackKey())) {
            inputKeys |= Actions.MELEE_ATTACK.getMask();
        }
        
        if(isKeyDown(keyMap.getReloadKey()) ) {
            inputKeys |= Actions.RELOAD.getMask();
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
        
        if(isButtonDown(keyMap.getFireKey()) || isKeyDown(keyMap.getFireKey()) ) {
            inputKeys |= Actions.FIRE.getMask();
        }
        
        if(isButtonDown(keyMap.getThrowGrenadeKey()) || isKeyDown(keyMap.getThrowGrenadeKey())) {
            inputKeys |= Actions.THROW_GRENADE.getMask();
        }
        
        return inputKeys;
    }
}
