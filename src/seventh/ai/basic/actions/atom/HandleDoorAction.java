/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.Door;
import seventh.game.entities.PlayerEntity;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * Opens or closes a {@link Door}
 * 
 * @author Tony
 *
 */
public class HandleDoorAction extends AdapterAction {

    private Door door;
    private boolean operated;
    private Timer blockCheck;
    
    public HandleDoorAction(Door door) {
        this.door = door;
        this.operated = false;
    
        this.blockCheck = new Timer(true, 800);
        this.blockCheck.start();
        
        getActionResult().setFailure();
    }
    

    /**
     * @return true if the Door has been operated on
     */
    protected boolean isFinished() {
        boolean isFinished = this.operated && (this.door.isClosed() || this.door.isOpened());
        return isFinished;
    }
    
    @Override
    public boolean isFinished(Brain brain) {
        return isFinished();
    }

    @Override
    public void update(Brain brain, TimeStep timeStep) {
        this.blockCheck.update(timeStep);
        
        PlayerEntity player = brain.getEntityOwner();
        if(!this.operated) {
            player.useSingle();            
        }

        this.operated = true;
        
        if(this.door.isBlocked() && this.blockCheck.isOnFirstTime()) {
            this.operated = false;
        }
        
        
        if(isFinished()) {
            getActionResult().setSuccess();
        }
    }

    @Override
    public DebugInformation getDebugInformation() {    
        return super.getDebugInformation()
                .add("door", this.door.getId())
                ;
    }
}
