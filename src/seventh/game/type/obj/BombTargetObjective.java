/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.type.obj;

import seventh.game.Game;
import seventh.game.GameInfo;
import seventh.game.entities.Bomb;
import seventh.game.entities.BombTarget;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class BombTargetObjective implements Objective {

    private BombTarget target;
    private Vector2f position;
    private String name;
    private boolean rotated;
    
    /**
     * @param name 
     */
    public BombTargetObjective(Vector2f position) {        
        this(position, null, false);
    }
    
    /**
     * @param position
     * @param name 
     */
    public BombTargetObjective(Vector2f position, String name, Boolean rotated) {
        this.position = position;        
        this.name = name != null ? name : "Bomb Target";
        this.rotated = rotated != null && rotated;
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
        target = game.newBombTarget(game.getGameType().getDefender(), position);
        if(rotated) {
            target.rotate90();
        }
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
        return target != null && (target.bombActive()||target.isBeingDestroyed());
    }

    /* (non-Javadoc)
     * @see seventh.game.type.Objective#getName()
     */
    @Override
    public String getName() {    
        return this.name;
    }
}
