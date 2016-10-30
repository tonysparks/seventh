/*
 * see license.txt
 */
package seventh.client.gfx.hud;

import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientPlayers;
import seventh.client.ClientTeam;
import seventh.client.entities.ClientPlayerEntity;
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
     * @param game
     * @return the id of the bot to send the command to
     */
    protected int getBotId(ClientGame game) {
    	int botId = -1;
    	if(game.isLocalPlayerCommander()) {
    		ClientPlayerEntity bot = game.getSelectedEntity();
    		if(bot != null && bot.isAlive()) {
    			botId = bot.getId();
    		}
    	}
    	else {
            ClientPlayer localPlayer = game.getLocalPlayer();
            
            if(localPlayer.isAlive()) {
            	botId = findClosestBot(game, localPlayer);    
            }                        
    	}
    	
    	return botId;
    }
    
    /**
     * Finds the closest friendly bot
     * @param game
     * @return the id of the closest bot, or -1 if no bots are available.
     */
    protected int findClosestBot(ClientGame game, ClientPlayer otherPlayer) {        
        ClientPlayers players = game.getPlayers();
        
        if(!otherPlayer.isAlive()) {
            return -1;
        }
        
        ClientTeam team = otherPlayer.getTeam();
        
        int closestBot = -1;
        float closestDistance = -1f;
        for(int i = 0; i < players.getMaxNumberOfPlayers(); i++) {
            ClientPlayer player = players.getPlayer(i);
            if(player != null && otherPlayer != player && player.isAlive() && player.isBot()) {
                if(player.getTeam().equals(team)) {
                    Vector2f pos = otherPlayer.getEntity().getPos();
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
    
    /**
     * Get the mouse position in world coordinates
     * 
     * @param game
     * @return the mouse position in world coordinates
     */
    protected Vector2f getMouseWorldPosition(ClientGame game) {
    	Vector2f mouse = game.getApp().getUiManager().getCursor().getCursorPos();
        Vector2f worldPosition = game.screenToWorldCoordinates( (int) mouse.x, (int) mouse.y);
        Vector2f.Vector2fSnap(worldPosition, worldPosition);
        return worldPosition;
    }
}
