/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.game.Flag;

/**
 * @author Tony
 *
 */
public class MoveToFlagAction extends MoveToAction {
	
	/**
	 * @param target 
	 */
	public MoveToFlagAction(Flag target) {
		super(target.getCenterPos());		
	}	
}
