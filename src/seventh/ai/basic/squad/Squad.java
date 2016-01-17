/*
 * see license.txt
 */
package seventh.ai.basic.squad;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.World;
import seventh.ai.basic.squad.SquadMember.Role;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * Represents a small squad of bots that can take on team work type activities
 * 
 * @author Tony
 *
 */
public class Squad implements Updatable {

    private DefaultAISystem aiSystem;
    private List<SquadMember> members;
    
    private SquadAction squadAction;
    /**
     * 
     */
    public Squad(DefaultAISystem aiSystem) {
        this.aiSystem = aiSystem;
        this.members = new ArrayList<SquadMember>();
    }

    public void addSquadMember(Brain bot) {
        if(this.members.isEmpty()) {
            this.members.add(new SquadMember(bot, Role.LEADER));
        }
        else {
            this.members.add(new SquadMember(bot, assignRole(bot)));
        }
    }
    
    public boolean isInSquad(Brain bot) {
        for(int i = 0; i < this.members.size(); i++) {
            SquadMember member = this.members.get(i);
            if(member.getBot().getPlayer().getId() == bot.getPlayer().getId()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasRole(Role role) {
        for(int i = 0; i < this.members.size(); i++) {
            SquadMember member = this.members.get(i);
            if(member.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }
    public Role assignRole(Brain bot) {
        return Role.LEFT_FLANK;
    }
    
    public World getWorld() {
        return this.aiSystem.getWorld();
    }
    
    /**
     * @return the aiSystem
     */
    public DefaultAISystem getAISystem() {
        return aiSystem;
    }
    
    /**
     * @return the members
     */
    public List<SquadMember> getMembers() {
        return members;
    }
    
    public void doAction(SquadAction action) {
        if(this.squadAction!=null) {
            this.squadAction.cancel(this);
        }
        this.squadAction = action;
        this.squadAction.start(this);
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        if(this.squadAction != null) {
            this.squadAction.update(timeStep);
            if(this.squadAction.isFinished(this)) {
                this.squadAction.end(this);
                this.squadAction = null;
            }
        }
    }
}
