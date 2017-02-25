/*
 * see license.txt 
 */
package seventh.game;

import java.util.ArrayList;
import java.util.List;

import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * Handles management of triggers
 * 
 * @author Tony
 *
 */
public class Triggers implements Updatable {

    private List<Trigger> triggers, pendingTriggers;
    private Game game;
    
    /**
     * 
     */
    public Triggers(Game game) {
        this.game = game;
        this.triggers = new ArrayList<>();
        this.pendingTriggers = new ArrayList<>();
    }
    
    @Override
    public void update(TimeStep timeStep) {
        this.pendingTriggers.clear();
        
        for(int i = 0; i < this.triggers.size(); i++) {
            Trigger trigger = this.triggers.get(i); 
            if(!trigger.checkCondition(game)) {
                this.pendingTriggers.add(trigger);
            }
            else {
                trigger.execute(game);
            }
        }
        
        this.triggers.clear();
        this.triggers.addAll(pendingTriggers);
        
    }

    public Triggers addTrigger(Trigger trigger) {
        this.triggers.add(trigger);
        return this;
    }
    
    /**
     * Removes all triggers
     * @return
     */
    public Triggers removeTriggers() {
        this.triggers.clear();
        return this;
    }
}
