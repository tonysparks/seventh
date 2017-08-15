/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.World;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.WaitAction;
import seventh.ai.basic.actions.atom.MoveToAction;
import seventh.game.entities.PlayerEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class DegroupActionEvaluator extends ActionEvaluator {

    private List<PlayerEntity> playersNearMe;    
    private Rectangle personalSpace, checkBounds;
    
    
    /**
     * @param goals
     * @param characterBias
     * @param keepBias
     */
    public DegroupActionEvaluator(Actions goals, double characterBias, double keepBias) {
        super(goals, characterBias, keepBias);
        
        this.playersNearMe = new ArrayList<>();
        this.personalSpace = new Rectangle(64, 64);
        this.checkBounds = new Rectangle(166, 166);
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double calculateDesirability(Brain brain) {
        double score = 0;
        Locomotion motion = brain.getMotion();
        PlayerEntity bot = brain.getEntityOwner();
                
        if(!motion.isMoving()) {
            if(isTooCloseToTeammates(brain, bot.getCenterPos())) {
                score = 1;                
            }            
        }
        
        return score;
    }

    private boolean isTooCloseToTeammates(Brain brain, Vector2f pos) {        
        PlayerEntity bot = brain.getEntityOwner();
        
        personalSpace.centerAround(pos);
        playersNearMe.clear();
        
        World world = brain.getWorld();
        world.playersIn(playersNearMe, personalSpace);
        if(playersNearMe.size() > 1) {
            for(int i = 0; i < playersNearMe.size(); i++) {
                PlayerEntity ent = playersNearMe.get(i);
                if(ent != bot && ent.isAlive() && !world.isEnemyOf(brain, ent)) {
                    return true;
                }
            }
        }
        
        return false;
    
    }
    
    @Override
    public Action getAction(Brain brain) {
        World world = brain.getWorld();
        PlayerEntity bot = brain.getEntityOwner();
        checkBounds.centerAround(bot.getCenterPos());
        
        
        int attempts = 20;
        while(attempts > 0) {
            Vector2f pos = world.getRandomSpot(brain.getEntityOwner(), checkBounds);
            if(pos!=null) {
                if(!isTooCloseToTeammates(brain, pos)) {
                    return new MoveToAction(pos);
                }
            }
            
            attempts--;
        }
        
        return new WaitAction(100);
    }

}
