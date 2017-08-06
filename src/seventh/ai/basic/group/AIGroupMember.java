/*
 * see license.txt
 */
package seventh.ai.basic.group;

import seventh.ai.basic.Brain;

/** 
 * @author Tony
 *
 */
public class AIGroupMember {

    public static enum Role {
        LEADER,
        LEFT_FLANK,
        RIGHT_FLANK,
        REAR_FLANK,
    }
    
    private Role type;
    private Brain bot;
    
    /**
     * 
     */
    public AIGroupMember(Brain brain, Role type) {
        this.bot = brain;
        this.type = type;
    }

    /**
     * @return the bot
     */
    public Brain getBot() {
        return bot;
    }
    
    public Role getRole() {
        return this.type;
    }
}
