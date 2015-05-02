/*
 * see license.txt
 */
package seventh.client;

import java.util.List;

import seventh.math.Vector2f;
import seventh.shared.Console;

/**
 * @author Tony
 *
 */
public class AIShortcuts {

    private int[] shortcuts;
    private boolean[] isDown;
    
    private List<AIShortcut> commands;
    /**
     * 
     */
    public AIShortcuts(List<AIShortcut> commands) {
        this.commands = commands;
        this.shortcuts = new int[commands.size()];
        this.isDown = new boolean[this.shortcuts.length];
        
        for(int i = 0; i < this.shortcuts.length; i++) {
            this.shortcuts[i] = commands.get(i).getShortcutKey();
        }
    }

    /**
     * @return the commands
     */
    public List<AIShortcut> getCommands() {
        return commands;
    }
    
    /**
     * Check and see if any of the {@link AIShortcut} should be executed
     * 
     * @param inputs
     * @param game
     */
    public boolean checkShortcuts(Inputs inputs, Console console, ClientGame game) {
        boolean result = false;
        for(int i = 0; i < this.shortcuts.length; i++) {
            boolean isKeyDown = inputs.isKeyDown(this.shortcuts[i]);
            if(isKeyDown) {
                this.isDown[i] = true;
            }
            if(this.isDown[i] && !isKeyDown) {
                this.commands.get(i).execute(console, game);
                result = true;
            }
            
            if(!isKeyDown) {
                this.isDown[i] = false;
            }
        }
        
        return result;
    }
    
    public static class FollowMeAIShortcut extends AIShortcut {

        /**
         * @param shortcutKey
         */
        public FollowMeAIShortcut(int shortcutKey) {
            super(shortcutKey, "To call for cover");
        }

        /* (non-Javadoc)
         * @see seventh.client.AIShortcut#execute(seventh.client.ClientGame)
         */
        @Override
        public void execute(Console console, ClientGame game) {
            ClientPlayer localPlayer = game.getLocalPlayer();
            
            if(!localPlayer.isAlive()) {
                return;
            }
            
            int closestBot = findClosestBot(game);
            if(closestBot > -1) {
                ClientPlayers players = game.getPlayers();
                int playerId = localPlayer.getId();
                console.execute("ai " + closestBot + " followMe " + playerId);
                console.execute("team_say " + players.getPlayer(closestBot).getName() + " cover me!" );
            }
        }
        
    }
    
    public static class SurpressFireAIShortcut extends AIShortcut {

        /**
         * @param shortcutKey
         * @param description
         */
        public SurpressFireAIShortcut(int shortcutKey) {
            super(shortcutKey, "Call for surpressing fire");
        }
        
        /* (non-Javadoc)
         * @see seventh.client.AIShortcut#execute(seventh.shared.Console, seventh.client.ClientGame)
         */
        @Override
        public void execute(Console console, ClientGame game) {
            ClientPlayer localPlayer = game.getLocalPlayer();
            
            if(!localPlayer.isAlive()) {
                return;
            }
            
            int closestBot = findClosestBot(game);
            if(closestBot > -1) {
                ClientPlayers players = game.getPlayers();
                int playerId = localPlayer.getId();
                // TODO console.execute("ai " + closestBot + " followMe " + playerId);
                console.execute("team_say " + players.getPlayer(closestBot).getName() + " surpress fire!" );
            }    
        }
    }
    
    public static class MoveToAIShortcut extends AIShortcut {

        /**
         * @param shortcutKey
         * @param description
         */
        public MoveToAIShortcut(int shortcutKey) {
            super(shortcutKey, "Move to here");
        }
        
        /* (non-Javadoc)
         * @see seventh.client.AIShortcut#execute(seventh.shared.Console, seventh.client.ClientGame)
         */
        @Override
        public void execute(Console console, ClientGame game) {
            ClientPlayer localPlayer = game.getLocalPlayer();
            
            if(!localPlayer.isAlive()) {
                return;
            }
            
            int closestBot = findClosestBot(game);
            if(closestBot > -1) {
                ClientPlayers players = game.getPlayers();
                Vector2f mouse = game.getApp().getUiManager().getCursor().getCursorPos();
                Vector2f worldPosition = game.screenToWorldCoordinates( (int) mouse.x, (int) mouse.y);
                Vector2f.Vector2fSnap(worldPosition, worldPosition);
                console.execute("ai " + closestBot + " moveTo " + worldPosition.x + " " + worldPosition.y);
                console.execute("team_say " + players.getPlayer(closestBot).getName() + " take cover here!" );
            }    
        }
    }
}
