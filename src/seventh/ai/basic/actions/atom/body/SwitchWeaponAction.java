/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom.body;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.Inventory;
import seventh.game.entities.PlayerEntity;
import seventh.game.entities.Entity.Type;
import seventh.game.weapons.Weapon;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class SwitchWeaponAction extends AdapterAction {

    private Type weapon;
    private boolean isDone;
    /**
     * 
     */
    public SwitchWeaponAction(Type weapon) {
        this.weapon = weapon;
        this.isDone = false;
    }
    
    public void reset(Type weapon) {
        this.weapon = weapon;
        this.isDone = false;
    }

    /* (non-Javadoc)
     * @see palisma.ai.Action#isFinished()
     */
    @Override
    public boolean isFinished(Brain brain) {
        return isDone;
    }

    /* (non-Javadoc)
     * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
     */    
    @Override
    public void update(Brain brain, TimeStep timeStep) {
        PlayerEntity player = brain.getEntityOwner();
        
        Inventory inventory = player.getInventory();
        Weapon currentItem = inventory.currentItem();
        if(currentItem != null) {            
            if(currentItem.getType().equals(weapon)) {
                isDone = true;
            }
            else {
                if(currentItem.isReady()) {
                    player.nextWeapon();
                }
            }
        }
        else {
            isDone = true; /* we don't have any items */
        }
        
        if(isDone) {
            getActionResult().setSuccess();
        }
        else {
            getActionResult().setFailure();
        }
        
    }

}
