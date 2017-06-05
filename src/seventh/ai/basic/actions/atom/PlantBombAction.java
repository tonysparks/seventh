/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.BombTarget;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class PlantBombAction extends AdapterAction {

    private BombTarget target;    
    
    /**
     */
    public PlantBombAction() {
        this(null);
    }
    
    /**
     * @param target 
     */
    public PlantBombAction(BombTarget target) {
        this.target = target;
    }
    
    /**
     * Resets the action
     * @param target
     */
    public void reset(BombTarget target) {
        this.target = target;
        getActionResult().setFailure();
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
     */
    @Override
    public void start(Brain brain) {    
        if(target != null && target.bombActive()) {
            getActionResult().setSuccess();
        }
        else {
            getActionResult().setFailure();
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#end(seventh.ai.basic.Brain)
     */
    @Override
    public void end(Brain brain) {
        brain.getMotion().stopUsingHands();
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
     */
    @Override
    public void interrupt(Brain brain) {
        brain.getMotion().stopUsingHands();
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
     */
    @Override
    public void resume(Brain brain) {
        start(brain);
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
     */
    @Override
    public void update(Brain brain, TimeStep timeStep) {
       if (target == null){
          //do nothing
       }
       else if (target.bombActive()){
          getActionResult().setSuccess();
       }
       else {
          Locomotion motion = brain.getMotion();
          if(!target.bombPlanting() || !motion.isPlanting()) {
             motion.plantBomb(target);
          }
          getActionResult().setFailure(); 
       }
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#isFinished()
     */
    @Override
    public boolean isFinished(Brain brain) {
        
        /* error cases, we must finish then */
        if(target == null || !target.isAlive()) {
            return true;
        }
        
        /* check and see if we planted the bomb */
        if(target.bombActive()) {
            return true;
        }
        
        if(!brain.getEntityOwner().isTouching(target)) {
            return true;
        }
        
        /* still working on being the hero */
        return false;
    }
    
    @Override
    public DebugInformation getDebugInformation() {    
        return super.getDebugInformation().add("target", this.target.getId());
    }
}
