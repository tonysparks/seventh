/*
 * see license.txt 
 */
package seventh.client;

import seventh.client.gfx.Cursor;
import seventh.client.screens.InGameScreen.Actions;
import seventh.shared.TimeStep;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

/**
 * @author Tony
 *
 */
public class ControllerInput implements ControllerListener {

	private boolean[] povDirections;
	private boolean[] buttons;
	
	private boolean isConnected;
	
	private float triggers;
	private float triggerSensitivity;
	
	private float[] movements;
	/**
	 * 
	 */
	public ControllerInput() {
		this.povDirections = new boolean[PovDirection.values().length];
		this.buttons = new boolean[64];
		this.isConnected = Controllers.getControllers().size > 0;
		this.triggerSensitivity = 0.3f;
		this.movements = new float[4];
	}
	
	/**
	 * @param triggerSensitivity the triggerSensitivity to set
	 */
	public void setTriggerSensitivity(float triggerSensitivity) {
		this.triggerSensitivity = triggerSensitivity;
	}
	
	/**
	 * @return true if the left trigger is down.
	 */
	public boolean isLeftTriggerDown() {
		return this.triggers > this.triggerSensitivity;
	}
	
	/**
	 * @return true if the right trigger is down.
	 */
	public boolean isRightTriggerDown() {
		return this.triggers < -this.triggerSensitivity;
	}
	
	/**
	 * @return the triggerSensitivity
	 */
	public float getTriggerSensitivity() {
		return triggerSensitivity;
	}
	
	/**
	 * @param dir
	 * @return true if the supplied {@link PovDirection} is down
	 */
	public boolean isPovDirectionDown(PovDirection dir) {
		return povDirections[dir.ordinal()];
	}
	
	/**
	 * @param button
	 * @return true if the supplied button is down
	 */
	public boolean isButtonDown(int button) {
		if(button < 0 || button > buttons.length) {
			return false;
		}
		
		return this.buttons[button];
	}
	
	/**
	 * @return the isConnected
	 */
	public boolean isConnected() {
		return isConnected;
	}
	

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {		
		return false;
	}
	
	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {		
		return false;
	}
	
	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection dir) {
		for(int i = 0; i<this.povDirections.length; i++) {
			this.povDirections[i] = false;
		}
		this.povDirections[dir.ordinal()] = true;				
		return false;
	}
	
	@Override
	public void disconnected(Controller controller) {
		this.isConnected = false;
	}
	
	@Override
	public void connected(Controller controller) {
		this.isConnected = true;
	}
	
	@Override
	public boolean buttonUp(Controller controller, int button) {
		this.buttons[button] = false;
//		System.out.println("ButtonUp:" + button);
		return false;
	}
	
	@Override
	public boolean buttonDown(Controller controller, int button) {
		this.buttons[button] = true;
//		System.out.println("ButtonDown:" + button);
		return false;
	}
	
	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		if(axisCode==4) {
			triggers = value;
		}
				        
        if(axisCode<4) {
            movements[axisCode] = value;
        }
                    
        return true;
		
		
	}
	
	@Override
	public boolean accelerometerMoved(Controller controller, int accelCode, Vector3 value) {
		return false;
	}	
	
	
	/**
	 * Polls the input to determine if any of the mapped buttons have been triggerd for 
	 * action.
	 * 
	 * @param timeStep
	 * @param cursor
	 * @param inputKeys
	 * @return the inputKeys
	 */
	public int pollInput(TimeStep timeStep, Cursor cursor, int inputKeys) {
	    if(isConnected()) {
            float sensitivity = 0.2f;
            
            if(movements[0] > sensitivity) {
                inputKeys |= Actions.DOWN.getMask();
            }
            if(movements[0] < -sensitivity) {
                inputKeys |= Actions.UP.getMask();
            }
            
            if(movements[1] > sensitivity) {
                inputKeys |= Actions.RIGHT.getMask();
            }
            if(movements[1] < -sensitivity) {
                inputKeys |= Actions.LEFT.getMask();
            }
            
            sensitivity = 0.3f;
            
            float dx = movements[3];
            float dy = movements[2];
            
            if( Math.abs(dx) > sensitivity || Math.abs(dy) > sensitivity) {
                cursor.moveByDelta(dx, dy);
            }
            
//          for(int i = 0; i < movements.length;i++) {
//              movements[i] = false;
//          }
            
            if(isRightTriggerDown()) {
                inputKeys |= Actions.FIRE.getMask();
            }
            if(isLeftTriggerDown()) {
                inputKeys |= Actions.THROW_GRENADE.getMask();
            }
            if(isButtonDown(2)) {
                inputKeys |= Actions.RELOAD.getMask();
            }
//          if(controllerInput.isButtonDown(3)) {
//              inputKeys |= Actions.WEAPON_SWITCH_UP.getMask();
//          }
            if(isButtonDown(1)||isButtonDown(5)) {
                inputKeys |= Actions.MELEE_ATTACK.getMask();
            }
            if(isButtonDown(4)) {
                inputKeys |= Actions.CROUCH.getMask();
            }
            
            
            if(isPovDirectionDown(PovDirection.north)) {
                inputKeys |= Actions.UP.getMask();
            }
            else if(isPovDirectionDown(PovDirection.northEast)) {
                inputKeys |= Actions.UP.getMask();
                inputKeys |= Actions.RIGHT.getMask();
            }
            
            else if(isPovDirectionDown(PovDirection.northWest)) {
                inputKeys |= Actions.UP.getMask();
                inputKeys |= Actions.LEFT.getMask();
            }
            
            else if(isPovDirectionDown(PovDirection.south)) {
                inputKeys |= Actions.DOWN.getMask();
            }
            else if(isPovDirectionDown(PovDirection.southEast)) {
                inputKeys |= Actions.DOWN.getMask();
                inputKeys |= Actions.RIGHT.getMask();
            }
            else if(isPovDirectionDown(PovDirection.southWest)) {
                inputKeys |= Actions.DOWN.getMask();
                inputKeys |= Actions.LEFT.getMask();
            }
            else if(isPovDirectionDown(PovDirection.east)) {                    
                inputKeys |= Actions.RIGHT.getMask();
            }
            else if(isPovDirectionDown(PovDirection.west)) {                    
                inputKeys |= Actions.LEFT.getMask();
            }
        }
	    
	    return inputKeys;
	}
}
