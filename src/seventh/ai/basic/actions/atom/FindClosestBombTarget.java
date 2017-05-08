/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.BombTarget;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class FindClosestBombTarget extends AdapterAction {

    private boolean toDefuse;
    /**
     * 
     */
    public FindClosestBombTarget(boolean toDefuse) {
        this.toDefuse = toDefuse;
    }
    
    
    private boolean isValidBombTarget(Brain brain, BombTarget bomb) {
        return bomb.isAlive() && ( toDefuse ? bomb.bombActive() : (!bomb.isBombAttached() && !bomb.bombActive() && !bomb.bombPlanting()) );
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
     */
    @Override
    public void start(Brain brain) {
        List<BombTarget> targets = brain.getWorld().getBombTargets();
        
        Vector2f botPos = brain.getEntityOwner().getPos();
        if(targets.isEmpty()){
        	getActionResult().setFailure();
        	return;
        }
        
        BombTarget closestBomb = null;
        float distance = -1;
        int index = 0;
        BombTarget bomb = targets.get(index);
        float distanceToBomb = Vector2f.Vector2fDistanceSq(bomb.getPos(), botPos);
            
        while(index<targets.size()&&isValidBombTarget(brain, bomb)){
        	if(closestBomb == null || distanceToBomb < distance) {
                closestBomb = bomb;
                distance = distanceToBomb;
            }
        	index++;
        	bomb = targets.get(index);
        	distanceToBomb = Vector2f.Vector2fDistanceSq(bomb.getPos(), botPos);
        }	

        if(closestBomb != null) {                
            this.getActionResult().setSuccess(closestBomb);
        }
        else getActionResult().setFailure();
   }
            
        

    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
     */
    @Override
    public void resume(Brain brain) {
        start(brain);
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#isFinished()
     */
    @Override
    public boolean isFinished(Brain brain) {
        return true;
    }

}
