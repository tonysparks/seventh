/*
 * see license.txt
 */
package seventh.client;

import seventh.client.gfx.Cursor;
import seventh.client.screens.InGameScreen.Actions;
import seventh.shared.TimeStep;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;

/**
 * Handles Joystick inputs and maps them to game actions
 * 
 * @author Tony
 *
 */
public class JoystickGameController extends ControllerInput implements GameController {
    
    private boolean[] isButtonReleased;
    
    public JoystickGameController() {
        this.isButtonReleased = new boolean[64];
    }
    
    @Override
    public boolean buttonDown(Controller controller, int button) {
        boolean result = super.buttonDown(controller, button);
        if(button >-1 && button < this.isButtonReleased.length)
            this.isButtonReleased[button] = false;
        return result;
    }
    
    @Override
    public boolean buttonUp(Controller controller, int button) {
        boolean result = super.buttonUp(controller, button);
        
        if(button >-1 && button < this.isButtonReleased.length)
            this.isButtonReleased[button] = true;
        return result;
    }
    
    private boolean isButtonReleased(ControllerButtons button) {
        switch(button) {
            case NORTH_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.north);
            case NE_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.northEast);
            case EAST_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.east);
            case SE_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.southEast);
            case SOUTH_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.south);
            case SW_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.southWest);
            case WEST_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.west);
            case NW_DPAD_BTN:
                return !isPovDirectionDown(PovDirection.northWest);
            
            case LEFT_TRIGGER_BTN:
                return !isLeftTriggerDown();
            case RIGHT_TRIGGER_BTN:
                return !isRightTriggerDown();
            
            case LEFT_BUMPER_BTN:
                return this.isButtonReleased[0];
            case RIGHT_BUMPER_BTN:
                return this.isButtonReleased[0];

            case LEFT_JOYSTICK_BTN:
                return this.isButtonReleased[0];
            case RIGHT_JOYSTICK_BTN:
                return this.isButtonReleased[0];
            
            case START_BTN:
                return this.isButtonReleased[0];
            case SELECT_BTN:
                return this.isButtonReleased[0];
              
            
            case A_BTN:
                return this.isButtonReleased[0];
            case B_BTN:
                return this.isButtonReleased[0];
            case X_BTN:
                return this.isButtonReleased[0];
            case Y_BTN:
                return this.isButtonReleased[0];                        
            default:
                return false;
        }
    }
    
    private void flushButtonReleaseState() {
        for(int i = 0; i < this.isButtonReleased.length; i++) {
            this.isButtonReleased[i] = false;
        }
    }
    
    private int handleDPad(int inputKeys) {
        if (isPovDirectionDown(PovDirection.north)) {
            inputKeys |= Actions.UP.getMask();
        }
        else if (isPovDirectionDown(PovDirection.northEast)) {
            inputKeys |= Actions.UP.getMask();
            inputKeys |= Actions.RIGHT.getMask();
        }

        else if (isPovDirectionDown(PovDirection.northWest)) {
            inputKeys |= Actions.UP.getMask();
            inputKeys |= Actions.LEFT.getMask();
        }

        else if (isPovDirectionDown(PovDirection.south)) {
            inputKeys |= Actions.DOWN.getMask();
        }
        else if (isPovDirectionDown(PovDirection.southEast)) {
            inputKeys |= Actions.DOWN.getMask();
            inputKeys |= Actions.RIGHT.getMask();
        }
        else if (isPovDirectionDown(PovDirection.southWest)) {
            inputKeys |= Actions.DOWN.getMask();
            inputKeys |= Actions.LEFT.getMask();
        }
        else if (isPovDirectionDown(PovDirection.east)) {
            inputKeys |= Actions.RIGHT.getMask();
        }
        else if (isPovDirectionDown(PovDirection.west)) {
            inputKeys |= Actions.LEFT.getMask();
        }
        
        return inputKeys;
    }
    
    private int handleMovement(KeyMap keyMap, Cursor cursor, int inputKeys) {
        if(keyMap.isSouthPaw()) {
            if (isYAxisMovedDownOnRightJoystick()) {
                inputKeys |= Actions.DOWN.getMask();
            }
            if (isYAxisMovedUpOnRightJoystick()) {
                inputKeys |= Actions.UP.getMask();
            }

            if (isXAxisMovedRightOnRightJoystick()) {
                inputKeys |= Actions.RIGHT.getMask();
            }
            if (isXAxisMovedLeftOnRightJoystick()) {
                inputKeys |= Actions.LEFT.getMask();
            }

            if (isXAxisOnLeftJoystickMoved() || isYAxisOnLeftJoystickMoved()) {
                float dx = getLeftJoystickXAxis();
                float dy = getLeftJoystickYAxis();
                
                if(keyMap.isJoystickInverted()) {
                    cursor.moveByDelta(-dx, -dy);
                }
                else {
                    cursor.moveByDelta(dx, dy);
                }
            }
        }
        else {
            if (isYAxisMovedDownOnLeftJoystick()) {
                inputKeys |= Actions.DOWN.getMask();
            }
            if (isYAxisMovedUpOnLeftJoystick()) {
                inputKeys |= Actions.UP.getMask();
            }

            if (isXAxisMovedRightOnLeftJoystick()) {
                inputKeys |= Actions.RIGHT.getMask();
            }
            if (isXAxisMovedLeftOnLeftJoystick()) {
                inputKeys |= Actions.LEFT.getMask();
            }

            if (isXAxisOnRightJoystickMoved() || isYAxisOnRightJoystickMoved()) {
                float dx = getRightJoystickXAxis();
                float dy = getRightJoystickYAxis();
                
                if(keyMap.isJoystickInverted()) {
                    cursor.moveByDelta(-dx, -dy);
                }
                else {
                    cursor.moveByDelta(dx, dy);
                }
            }
        }
        
        inputKeys = handleDPad(inputKeys);
        
        return inputKeys;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see seventh.client.GameController#pollInputs(seventh.shared.TimeStep,
     * seventh.client.KeyMap, seventh.client.gfx.Cursor, int)
     */
    @Override
    public int pollInputs(TimeStep timeStep, KeyMap keyMap, Cursor cursor, int inputKeys) {

        if (isConnected()) {
            inputKeys = handleMovement(keyMap, cursor, inputKeys);
            
            if (isButtonDown(keyMap.getFireBtn())) {
                inputKeys |= Actions.FIRE.getMask();
            }
            if (isButtonDown(keyMap.getThrowGrenadeBtn())) {
                inputKeys |= Actions.THROW_GRENADE.getMask();
            }
            if (isButtonDown(keyMap.getReloadBtn())) {
                inputKeys |= Actions.RELOAD.getMask();
            }
            
            // TODO: Make configurable
            if (isButtonReleased(ControllerButtons.Y_BTN)) {
                inputKeys |= Actions.WEAPON_SWITCH_UP.getMask();
            }
            
            if (isButtonDown(keyMap.getMeleeAttackBtn())) {
                inputKeys |= Actions.MELEE_ATTACK.getMask();
            }
            
            if (isButtonDown(keyMap.getCrouchBtn())) {
                inputKeys |= Actions.CROUCH.getMask();
            }            
            
            if (isButtonDown(keyMap.getSprintBtn())) {
                inputKeys |= Actions.SPRINT.getMask();
            }
            
            if (isButtonDown(keyMap.getWalkBtn())) {
                inputKeys |= Actions.WALK.getMask();
            }
            
            if (isButtonDown(keyMap.getDropWeaponBtn())) {
                inputKeys |= Actions.DROP_WEAPON.getMask();
            }
            
            if (isButtonDown(keyMap.getUseBtn())) {
                inputKeys |= Actions.USE.getMask();
            }
        }

        flushButtonReleaseState();
        
        return inputKeys;
    }

}
