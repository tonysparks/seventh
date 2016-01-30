/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.game.BombTarget;

/**
 * @author Tony
 *
 */
public class MoveToBombAction extends MoveToAction {
	
	/**
	 * @param target 
	 */
	public MoveToBombAction(BombTarget target) {
		super(target.getCenterPos());		
	}	
}
