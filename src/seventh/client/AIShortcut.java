/*
 * see license.txt
 */
package seventh.client;

import seventh.math.Vector2f;
import seventh.shared.Console;

/**
 * Sends an AI command
 * 
 * @author Tony
 *
 */
public abstract class AIShortcut {

    private int shortcutKey;
    private String description;
    
    /**
     * @param shortcutKey 
     */
    public AIShortcut(int shortcutKey, String description) {
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
    
    
    /**
     * Sends the command
     * 
     * @param console
     * @param game
     */
    public abstract void execute(Console console, ClientGame game);
    
    
    /**
     * Finds the closest friendly bot
     * @param game
     * @return the id of the closest bot, or -1 if no bots are available.
     */
    protected int findClosestBot(ClientGame game) {
        ClientPlayer localPlayer = game.getLocalPlayer();
        ClientPlayers players = game.getPlayers();
        
        if(!localPlayer.isAlive()) {
            return -1;
        }
        
        ClientTeam team = localPlayer.getTeam();
        
        int closestBot = -1;
        float closestDistance = -1f;
        for(int i = 0; i < players.getMaxNumberOfPlayers(); i++) {
            ClientPlayer player = players.getPlayer(i);
            if(player != null && player.isAlive() && player.isBot()) {
                if(player.getTeam().equals(team)) {
                    Vector2f pos = localPlayer.getEntity().getPos();
                    Vector2f botPos = player.getEntity().getPos();
                    
                    float dist = Vector2f.Vector2fDistanceSq(pos, botPos);
                    if(closestBot == -1 || dist < closestDistance) {
                        closestBot = i;
                        closestDistance = dist;
                    }
                    
                }
            }
        }
        
        return closestBot;
    }
}
