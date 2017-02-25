/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.atom.MoveToAction;
import seventh.game.entities.Entity;
import seventh.game.entities.PlayerEntity;
import seventh.game.entities.Entity.Type;
import seventh.map.Map;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.SeventhConstants;

/**
 * If the bot is shot, see if it can defend itself
 * 
 * @author Tony
 *
 */
public class AvoidGrenadeActionEvaluator extends ActionEvaluator {

    private MoveToAction moveToAction;
    private Rectangle dangerArea;
    private Vector2f ray;
    private Entity danger;
    private long frameLinger;
    
    /**
     * @param goals
     * @param characterBias
     */
    public AvoidGrenadeActionEvaluator(Actions goals, double characterBias, double keepBias) {
        super(goals, characterBias, keepBias);
        this.moveToAction = new MoveToAction(new Vector2f());
        this.dangerArea = new Rectangle(150, 150);
        this.ray = new Vector2f();
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double calculateDesirability(Brain brain) {
        double desirability = 0.0;
                
        PlayerEntity me = brain.getEntityOwner();
        Entity[] entities = brain.getWorld().getEntities();
        for(int i = SeventhConstants.MAX_PERSISTANT_ENTITIES; i < entities.length; i++) {
            Entity ent = entities[i];
            if(ent!=null) {
                if(ent.getType().equals(Type.GRENADE)||
                   ent.getType().equals(Type.EXPLOSION)) {
                    
                    this.dangerArea.centerAround(ent.getCenterPos());
                    if(this.dangerArea.intersects(me.getBounds())) {
                        desirability += brain.getRandomRange(0.90f, 1.0f);
                        danger = ent;
                        frameLinger = 2;
                        break;
                    }
                }
            }
        }
        
        // because it takes a frame or two to register
        // an explosion from a grenade, we need to linger
        // around otherwise the bot thinks the grenade
        // is no longer a danger and moves back into the damage
        // radius
        if(desirability==0&&frameLinger>0) {
            desirability += brain.getRandomRange(0.90f, 1.0f);
            frameLinger--;
        }
        
        desirability *= getCharacterBias();
        return desirability;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {        
        if(danger!=null) {
            // shoot a ray in a number of directions to see which ones are able
            // to be used for running out of the danger zone
            PlayerEntity me = brain.getEntityOwner();
            Vector2f pos = me.getCenterPos();
            Rectangle bounds = me.getBounds();
            
            Map map = brain.getWorld().getMap();
            
            boolean keepLooking = true;
            
            // check the top
            ray.x = pos.x;
            ray.y = pos.y - (pos.y - dangerArea.y) - dangerArea.height;
            if(!map.lineCollides(pos, ray)) {
                this.moveToAction.reset(brain, ray);
                keepLooking = false;
            }
            
            if(keepLooking) {
                // check the right
                ray.x = pos.x + dangerArea.width;
                ray.y = pos.y;
                
                if(!map.lineCollides(pos, ray)) {
                    this.moveToAction.reset(brain, ray);
                    keepLooking = false;
                }
            }
            
            if(keepLooking) {
                // check the bottom
                ray.x = pos.x;
                ray.y = pos.y + bounds.height + (dangerArea.height - (pos.y  - dangerArea.y));
                
                if(!map.lineCollides(pos, ray)) {
                    this.moveToAction.reset(brain, ray);
                    keepLooking = false;
                }
            }
            
            if(keepLooking) {
                // check the left
                ray.x = pos.x - ((pos.x + bounds.width) - dangerArea.x);
                ray.y = pos.y;
                
                if(!map.lineCollides(pos, ray)) {
                    this.moveToAction.reset(brain, ray);
                    keepLooking = false;
                }
            }
            
        }
        return this.moveToAction;
    }

}
