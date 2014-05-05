/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.game.BombTarget;

/**
 * @author Tony
 *
 */
public class MoveToBombAction extends MoveToAction {	
	/**
	 * 
	 */
	public MoveToBombAction(BombTarget bomb) {
		super(bomb.getCenterPos());
	}	
}
