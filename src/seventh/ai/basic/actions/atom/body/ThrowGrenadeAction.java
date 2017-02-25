/*
 * The Seventh
 * see license.txt 
 */
package seventh.ai.basic.actions.atom.body;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.PlayerEntity;
import seventh.game.weapons.GrenadeBelt;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ThrowGrenadeAction extends AdapterAction {
    
    private int timeToHold, timeHeld;
    private GrenadeBelt belt;
    /**
     * 
     */
    public ThrowGrenadeAction(PlayerEntity me, Vector2f pos) {
        float distance = Vector2f.Vector2fDistance(me.getCenterPos(), pos);
        if(distance < 100) {
            timeToHold = 200;
        }
        else if (distance < 200) {
            timeToHold = 800;
        }
        else if (distance < 300) {
            timeToHold = 1200;
        }
        else {
            timeToHold = 2000;
        }
        
        this.belt=me.getInventory().getGrenades();
        
    }

    /* (non-Javadoc)
     * @see seventh.ai.Action#start(seventh.ai.Brain)
     */
    @Override
    public void start(Brain brain) {
        if( belt.getNumberOfGrenades() > 0 ) {
            belt.beginFire();
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
     */
    @Override
    public void resume(Brain brain) {
        start(brain);
    }

    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
     */
    @Override
    public void interrupt(Brain brain) {
        belt.endFire();
    }

    /* (non-Javadoc)
     * @see seventh.ai.Action#isFinished()
     */
    @Override
    public boolean isFinished(Brain brain) {
        return belt.getNumberOfGrenades() < 1 || this.timeHeld >= this.timeToHold;
    }

    /* (non-Javadoc)
     * @see seventh.ai.Action#update(seventh.ai.Brain, leola.live.TimeStep)
     */
    @Override
    public void update(Brain brain, TimeStep timeStep) {
        this.timeHeld += timeStep.getDeltaTime();
        if(this.timeHeld >= this.timeToHold) {
            belt.endFire();
//            System.out.println("End throwing: " + this.timeToHold);
        }
    }

}
