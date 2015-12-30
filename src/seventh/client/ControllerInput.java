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
 * Controller Input, this is optimized for an XBox controller.
 * 
 * @author Tony
 *
 */
public class ControllerInput implements ControllerListener {

    /**
     * Buttons as they relate to an XBox controller
     * 
     * @author Tony
     *
     */
    public static enum ControllerButtons {        
        LEFT_TRIGGER_BTN,
        RIGHT_TRIGGER_BTN,
        
        LEFT_JOYSTICK_BTN,
        RIGHT_JOYSTICK_BTN,
        
        Y_BTN,
        X_BTN,
        A_BTN,
        B_BTN,
        
        START_BTN,
        SELECT_BTN,
        
        LEFT_BUMPER_BTN,
        RIGHT_BUMPER_BTN,
        
        NORTH_DPAD_BTN,
        NE_DPAD_BTN,
        EAST_DPAD_BTN,
        SE_DPAD_BTN,
        SOUTH_DPAD_BTN,
        SW_DPAD_BTN,
        WEST_DPAD_BTN,
        NW_DPAD_BTN,
        ;
        
        private static final int NumberOfKeyboardKeys = 256;
        /**
         * Returns the pseudo-key code of the button.  Note, this does not
         * relate to the underlying controller button number, but rather is used
         * to relate the {@link ControllerButtons} to the approach enum.
         * 
         * @return the pseudo-key code
         */
        public int getKey() {
            // offset by the number of keyboard keys, we add it so that we can
            // safely and correctly index into the KeyMap
            return NumberOfKeyboardKeys + ordinal();
        }
        
        private static final ControllerButtons[] values = values();
        
        public static ControllerButtons fromKey(int key) {
            // adjust offset from the keyboard keys 
            key -= NumberOfKeyboardKeys;
            
            if(key>-1&&key<values.length) {
                return values[key];
            }
            return null;
        }
        
        public static ControllerButtons fromString(String str) {
           for(ControllerButtons b : values) {
               if(b.name().equalsIgnoreCase(str)) {
                   return b;
               }
           }
           
           return null;
        }
    }
    
	private boolean[] povDirections;
	private boolean[] buttons;
	
	private boolean isConnected;
	
	private float triggers;
	private float triggerSensitivity;
	
	private float leftJoystickSensitivity;
	private float rightJoystickSensitivity;
	
	private float[] movements;
	
	/**
	 */
	public ControllerInput() {
		this.povDirections = new boolean[PovDirection.values().length];
		this.buttons = new boolean[64];
		this.isConnected = Controllers.getControllers().size > 0;
		this.triggerSensitivity = 0.3f;
		this.leftJoystickSensitivity = 0.2f;
		this.rightJoystickSensitivity = 0.3f;
		
		this.movements = new float[4];
	}
	
    /**
     * @return the leftJoystickSensitivity
     */
    public float getLeftJoystickSensitivity() {
        return leftJoystickSensitivity;
    }
    
    /**
     * @return the rightJoystickSensitivity
     */
    public float getRightJoystickSensitivity() {
        return rightJoystickSensitivity;
    }
    
    /**
     * @param leftJoystickSensitivity the leftJoystickSensitivity to set
     */
    public void setLeftJoystickSensitivity(float leftJoystickSensitivity) {
        this.leftJoystickSensitivity = leftJoystickSensitivity;
    }
    
    /**
     * @param rightJoystickSensitivity the rightJoystickSensitivity to set
     */
    public void setRightJoystickSensitivity(float rightJoystickSensitivity) {
        this.rightJoystickSensitivity = rightJoystickSensitivity;
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
	 * Determines if a particular button is down
	 * 
	 * @param button
	 * @return true if the button is down, false otherwise
	 */
	public boolean isButtonDown(ControllerButtons button) {
	    switch(button) {
    	    case NORTH_DPAD_BTN:
    	        return isPovDirectionDown(PovDirection.north);
    	    case NE_DPAD_BTN:
    	        return isPovDirectionDown(PovDirection.northEast);
            case EAST_DPAD_BTN:
                return isPovDirectionDown(PovDirection.east);
            case SE_DPAD_BTN:
                return isPovDirectionDown(PovDirection.southEast);
            case SOUTH_DPAD_BTN:
                return isPovDirectionDown(PovDirection.south);
            case SW_DPAD_BTN:
                return isPovDirectionDown(PovDirection.southWest);
            case WEST_DPAD_BTN:
                return isPovDirectionDown(PovDirection.west);
            case NW_DPAD_BTN:
                return isPovDirectionDown(PovDirection.northWest);
            
            case LEFT_TRIGGER_BTN:
                return isLeftTriggerDown();
            case RIGHT_TRIGGER_BTN:
                return isRightTriggerDown();
            
            case LEFT_BUMPER_BTN:
                return this.buttons[0];
            case RIGHT_BUMPER_BTN:
                return this.buttons[0];

            case LEFT_JOYSTICK_BTN:
                return this.buttons[0];
            case RIGHT_JOYSTICK_BTN:
                return this.buttons[0];
            
            case START_BTN:
                return this.buttons[0];
            case SELECT_BTN:
                return this.buttons[0];
              
            
            case A_BTN:
                return this.buttons[0];
            case B_BTN:
                return this.buttons[0];
            case X_BTN:
                return this.buttons[0];
            case Y_BTN:
                return this.buttons[0];                        
            default:
                return false;
	    }
	}
	
	public boolean isXAxisMovedLeftOnLeftJoystick() {
	    return movements[1] < -this.leftJoystickSensitivity;
	}
	public boolean isXAxisMovedRightOnLeftJoystick() {
        return movements[1] > this.leftJoystickSensitivity;
    }
	
	public boolean isXAxisMovedLeftOnRightJoystick() {
        return movements[3] < -this.rightJoystickSensitivity;
    }
    public boolean isXAxisMovedRightOnRightJoystick() {
        return movements[3] > this.rightJoystickSensitivity;
    }
	
    public boolean isYAxisMovedUpOnLeftJoystick() {
        return movements[0] < -this.leftJoystickSensitivity;
    }    
    public boolean isYAxisMovedDownOnLeftJoystick() {
        return movements[0] > this.leftJoystickSensitivity;
    }
    
    public boolean isYAxisMovedUpOnRightJoystick() {
        return movements[2] < -this.rightJoystickSensitivity;
    }    
    public boolean isYAxisMovedDownOnRightJoystick() {
        return movements[2] > this.rightJoystickSensitivity;
    }
    
    public boolean isXAxisOnRightJoystickMoved() {
        return Math.abs(movements[3]) > this.rightJoystickSensitivity;
    }
    public boolean isYAxisOnRightJoystickMoved() {
        return Math.abs(movements[2]) > this.rightJoystickSensitivity;
    }
    
    public boolean isXAxisOnLeftJoystickMoved() {
        return Math.abs(movements[1]) > this.leftJoystickSensitivity;
    }
    public boolean isYAxisOnLeftJoystickMoved() {
        return Math.abs(movements[0]) > this.leftJoystickSensitivity;
    }
	
	public float getLeftJoystickXAxis() {
	    return movements[1];
	}
	public float getLeftJoystickYAxis() {
        return movements[0];
    }
	
	public float getRightJoystickXAxis() {
        return movements[3];
    }
    public float getRightJoystickYAxis() {
        return movements[2];
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
	    if(button >-1 && button < this.buttons.length)
	        this.buttons[button] = false;
	    
//		System.out.println("ButtonUp:" + button);
		return false;
	}
	
	@Override
	public boolean buttonDown(Controller controller, int button) {
	    if(button >-1 && button < this.buttons.length)
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
}
