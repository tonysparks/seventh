/*
 * see license.txt 
 */
package seventh.client;

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
	/**
	 * 
	 */
	public ControllerInput() {
		this.povDirections = new boolean[PovDirection.values().length];
		this.buttons = new boolean[64];
		this.isConnected = Controllers.getControllers().size > 0;
		this.triggerSensitivity = 0.3f;
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
		
		return false;
	}
	
	@Override
	public boolean accelerometerMoved(Controller controller, int accelCode, Vector3 value) {
		return false;
	}	
}
