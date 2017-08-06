/*
 * see license.txt 
 */
package seventh.client.gfx.hud;

import seventh.client.ClientGame;

/**
 * @author Tony
 *
 */
public abstract class AIGroupCommand {

    private int shortcutKey;
    private String description;
    
    /**
     * @param shortcutKey 
     */
    public AIGroupCommand(int shortcutKey, String description) {
        this.shortcutKey = shortcutKey;
        this.description = description;
    }
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the shortcutKey
     */
    public int getShortcutKey() {
        return shortcutKey;
    }

    
    
    public abstract void execute(ClientGame game);

}
