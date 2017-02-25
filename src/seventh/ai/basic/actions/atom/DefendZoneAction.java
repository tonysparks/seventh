/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.Zone;
import seventh.ai.basic.actions.AdapterAction;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class DefendZoneAction extends AdapterAction {
    
    private Zone zone;
    
    private long timeToDefend;
    private long timeSpentDefending;
    
    /**
     */
    public DefendZoneAction() {
        this(null);
    }
    
    /**
     * 
     */
    public DefendZoneAction(Zone zone) {
        this.zone = zone;
        
        timeToDefend = 5_000; /* time spent before we ask what to do next */
        timeSpentDefending = 0;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
     */
    @Override
    public void start(Brain brain) {
        timeToDefend = 5_000; /* time spent before we ask what to do next */
        timeSpentDefending = 0;
        
        if(zone==null) {
            zone = brain.getWorld().getZone(brain.getEntityOwner().getCenterPos());
        }        
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
     */
    @Override
    public void resume(Brain brain) {
        if(zone==null) {
            zone = brain.getWorld().getZone(brain.getEntityOwner().getCenterPos());
        }
        Vector2f pos = brain.getWorld().getRandomSpot(brain.getEntityOwner(), zone.getBounds());
        
        Locomotion motion = brain.getMotion();
        motion.moveTo(pos);
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
     */
    @Override
    public void update(Brain brain, TimeStep timeStep) {
        Locomotion motion = brain.getMotion();
        if(!motion.isMoving()) {
            if(zone==null) {
                zone = brain.getWorld().getZone(brain.getEntityOwner().getCenterPos());
            }
            Vector2f pos = brain.getWorld().getRandomSpot(brain.getEntityOwner(), zone.getBounds());
            motion.moveTo(pos);
        }
        
        timeSpentDefending += timeStep.getDeltaTime();
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#isFinished()
     */
    @Override
    public boolean isFinished(Brain brain) {
        return timeSpentDefending >= timeToDefend;
        
    }
    
    @Override
    public DebugInformation getDebugInformation() {    
        return super.getDebugInformation().add("zone", zone);
    }

}
