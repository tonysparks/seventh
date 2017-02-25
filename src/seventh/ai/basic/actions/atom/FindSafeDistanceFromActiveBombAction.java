/**
 * 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.ai.basic.World;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.Bomb;
import seventh.game.entities.BombTarget;
import seventh.game.entities.PlayerEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * @author Tony
 * 
 */
public class FindSafeDistanceFromActiveBombAction extends AdapterAction {

    private BombTarget target;

    /**
     * 
     */
    public FindSafeDistanceFromActiveBombAction(BombTarget target) {
        this.target = target;
    }

    /*
     * (non-Javadoc)
     * 
     * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
     */
    @Override
    public void start(Brain brain) {
        Bomb bomb = target.getBomb();
        if(bomb==null) {
            this.getActionResult().setFailure();
            return;
        }
        
        World world = brain.getWorld();
        
        PlayerEntity bot = brain.getEntityOwner(); 
        Rectangle coverBounds = new Rectangle(300, 300);
        coverBounds.centerAround(target.getCenterPos());
        
        Vector2f moveTo = world.getRandomSpotNotIn(bot, coverBounds.x, coverBounds.y, coverBounds.width, coverBounds.height, bomb.getBlastRadius());
        getActionResult().setSuccess(moveTo);
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
     */
    @Override
    public void resume(Brain brain) {
        start(brain);
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#isFinished(seventh.ai.basic.Brain)
     */
    @Override
    public boolean isFinished(Brain brain) {
        return true;
    }
}
