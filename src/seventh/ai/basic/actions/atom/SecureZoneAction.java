/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Zone;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.PlayerEntity;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class SecureZoneAction extends AdapterAction {

    private Zone zone;
    
    private List<PlayerEntity> playersInZone;
    /**
     * 
     */
    public SecureZoneAction(Zone zone) {        
        this.zone = zone;
        this.playersInZone = new ArrayList<>();
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
     */
    @Override
    public void start(Brain brain) {
        Vector2f pos = brain.getWorld().getRandomSpot(brain.getEntityOwner(), zone.getBounds());
        if(pos != null) {    
            brain.getMotion().moveTo(pos);
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
     * @see seventh.ai.basic.actions.AdapterAction#isFinished()
     */
    @Override
    public boolean isFinished(Brain brain) {
        getActionResult().setFailure();
        
        /* ensure the player is within the Zone */
//        if (!zone.getBounds().contains(brain.getEntityOwner().getCenterPos())) {
//            return true;
//        }
        
        this.playersInZone.clear();
        brain.getWorld().playersIn(this.playersInZone, zone.getBounds());
        
        boolean isClear = true;
        
        for(int i = 0; i < playersInZone.size(); i++) {
            PlayerEntity ent = playersInZone.get(i);
            if(ent.isAlive()&&!brain.getEntityOwner().isOnTeamWith(ent)) {
                isClear = false;
                return isClear;                
            }
        }

        getActionResult().setSuccess();

                
        return isClear;
        
    }

    @Override
    public DebugInformation getDebugInformation() {    
        return super.getDebugInformation().add("zone", this.zone);
    }
}
