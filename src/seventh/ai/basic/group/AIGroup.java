/*
 * see license.txt
 */
package seventh.ai.basic.group;

import seventh.ai.basic.Brain;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.World;
import seventh.ai.basic.teamstrategy.Roles;
import seventh.game.PlayerInfo;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * Represents a small AI Group of bots that can take on team work type activities
 * 
 * @author Tony
 *
 */
public class AIGroup implements Updatable {

    private DefaultAISystem aiSystem;
    private Brain[] members;
    private Roles roles;
    
    private AIGroupAction aIGroupAction;
    private int size;
    /**
     * 
     */
    public AIGroup(DefaultAISystem aiSystem) {
        this.aiSystem = aiSystem;
        this.members = new Brain[SeventhConstants.MAX_PLAYERS];
        this.roles = new Roles();
    }
    
    /**
     * @return the roles
     */
    public Roles getRoles() {
        return roles;
    }

    public void addMember(Brain bot) {
        if(bot.getPlayer().isAlive()) {
            if(this.members[bot.getPlayer().getId()] == null) {
                this.size++;    
            }
            
            this.members[bot.getPlayer().getId()] = bot;
        }
    }
    
    public boolean inGroup(Brain bot) {
        for(int i = 0; i < this.members.length; i++) {
            Brain member = this.members[i];
            if(member.getPlayer().getId() == bot.getPlayer().getId()) {
                return true;
            }
        }
        return false;
    }
    
    public int groupSize() {
        return size;
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
    public Brain[] getMembers() {
        return members;
    }
    
    public void doAction(AIGroupAction action) {
        if(this.aIGroupAction!=null) {
            this.aIGroupAction.cancel(this);
        }
        this.aIGroupAction = action;
        this.aIGroupAction.start(this);
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        if(this.aIGroupAction != null) {
            this.aIGroupAction.update(timeStep);
            if(this.aIGroupAction.isFinished(this)) {
                this.aIGroupAction.end(this);
                this.aIGroupAction = null;
            }
        }
    }
    
    public void onPlayerKilled(PlayerInfo player) {
        this.members[player.getId()] = null;
        if(this.size>0) {
            this.size--;
        }
        
        this.roles.removeDeadPlayer(player);
    }    
}
