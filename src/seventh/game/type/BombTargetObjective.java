/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.type;

import seventh.game.Bomb;
import seventh.game.BombTarget;
import seventh.game.Game;
import seventh.game.GameInfo;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class BombTargetObjective implements Objective {

	private BombTarget target;
	private Vector2f position;
	private String name;
	
	/**
	 * @param name
	 * @param position 
	 */
	public BombTargetObjective(Vector2f position, String name) {		
		this.position = position;		
		this.name = name != null ? name : "Bomb Target";
	}

	/* (non-Javadoc)
	 * @see seventh.game.type.Objective#reset(seventh.game.Game)
	 */
	@Override
	public void reset(Game game) {
		if(target!=null) {
			Bomb bomb = target.getBomb();
			if(bomb!=null) {
				bomb.softKill();
			}
			target.reset();			
			
			/* bug with onKill if the bomb is 
			 * already 'dead' it doesn't invoke the
			 * onKill callback
			 */
			if( target.onKill != null ) {
				target.onKill.onKill(target, target);
			}
			
		}
		
		init(game);	
	}

	/* (non-Javadoc)
	 * @see seventh.game.type.Objective#init(seventh.game.Game)
	 */
	@Override
	public void init(Game game) {
		target = game.newBombTarget(position);
	}

	/* (non-Javadoc)
	 * @see seventh.game.type.Objective#isCompleted(seventh.game.Game)
	 */
	@Override
	public boolean isCompleted(GameInfo game) {
		return target != null && !target.isAlive();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.Objective#isInProgress(seventh.game.GameInfo)
	 */
	@Override
	public boolean isInProgress(GameInfo game) {
		return target != null && target.bombActive();
	}

	/* (non-Javadoc)
	 * @see seventh.game.type.Objective#getName()
	 */
	@Override
	public String getName() {	
		return this.name;
	}
}
