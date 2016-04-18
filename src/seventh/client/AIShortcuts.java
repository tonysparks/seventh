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
    private boolean isMouseButtonDown;
    private boolean isMouseButton2Down;
    
    private List<AIShortcut> commands;
    private AIShortcut hotCommand;
    private AIShortcut secondaryHotCommand;
    
    private KeyMap keyMap;
    /**
     * 
     */
    public AIShortcuts(KeyMap keyMap, List<AIShortcut> commands, AIShortcut hotCommand, AIShortcut secondaryHotCommand) {
    	this.keyMap = keyMap;
        this.commands = commands;
        this.hotCommand = hotCommand;
        this.secondaryHotCommand = secondaryHotCommand;
        
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
        
        this.isMouseButtonDown = checkHotKey(this.hotCommand, this.isMouseButtonDown, inputs.isButtonDown(keyMap.getFireKey()), console, game);
        this.isMouseButton2Down = checkHotKey(this.secondaryHotCommand, this.isMouseButton2Down, inputs.isButtonDown(keyMap.getThrowGrenadeKey()), console, game);
                
        return result;
    }
    
    private boolean checkHotKey(AIShortcut hotCommand, boolean wasDown, boolean isDown, Console console, ClientGame game) {
    	if(isDown) {
    		wasDown = true;
        }
        
        if(wasDown && !isDown) {
        	hotCommand.execute(console, game);
        }
        
        if(!isDown) {
        	wasDown = false;
        }
        
        return wasDown;
    }
    
    public static class FollowMeAIShortcut extends AIShortcut {

        /**
         * @param shortcutKey
         */
        public FollowMeAIShortcut(int shortcutKey) {
            super(shortcutKey, "Call for a partner");
        }

        /* (non-Javadoc)
         * @see seventh.client.AIShortcut#execute(seventh.client.ClientGame)
         */
        @Override
        public void execute(Console console, ClientGame game) {
        	int botId = getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                ClientPlayer bot = players.getPlayer(botId);
                int playerId = findClosestBot(game, bot);
                console.execute("ai " + botId + " followMe " + playerId);
                console.execute("team_say " + bot.getName() + " follow me!" );
                //console.execute("speech " + 2);
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
        	int botId = getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                Vector2f worldPosition = getMouseWorldPosition(game);
                console.execute("ai " + botId+ " surpressFire " + (int)worldPosition.x + " " + (int)worldPosition.y);
                console.execute("team_say " + players.getPlayer(botId).getName() + " surpress fire!" );
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
        	int botId = getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                Vector2f worldPosition = getMouseWorldPosition(game);
                console.execute("ai " + botId + " moveTo " + (int)worldPosition.x + " " + (int)worldPosition.y);
                console.execute("team_say " + players.getPlayer(botId).getName() + " take cover here!" );
               // console.execute("speech " + 5);
            }    
        }
    }
    
    public static class DefuseBombAIShortcut extends AIShortcut {
        /**
         * @param shortcutKey
         */
        public DefuseBombAIShortcut(int shortcutKey) {
            super(shortcutKey, "Defuse bomb");
        }
        
        /* (non-Javadoc)
         * @see seventh.client.AIShortcut#execute(seventh.shared.Console, seventh.client.ClientGame)
         */
        @Override
        public void execute(Console console, ClientGame game) {
        	int botId = getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                console.execute("ai " + botId + " defuseBomb");
                console.execute("team_say " + players.getPlayer(botId).getName() + " defuse the bomb!" );
            }    
        }
    }
    
    public static class PlantBombAIShortcut extends AIShortcut {
        /**
         * @param shortcutKey
         */
        public PlantBombAIShortcut(int shortcutKey) {
            super(shortcutKey, "Plant bomb");
        }
        
        /* (non-Javadoc)
         * @see seventh.client.AIShortcut#execute(seventh.shared.Console, seventh.client.ClientGame)
         */
        @Override
        public void execute(Console console, ClientGame game) {
        	int botId = getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                console.execute("ai " + botId + " plantBomb");
                console.execute("team_say " + players.getPlayer(botId).getName() + " plant the bomb!" );
            }    
        }
    }
    
    
    public static class DefendPlantedBombAIShortcut extends AIShortcut {
        /**
         * @param shortcutKey
         */
        public DefendPlantedBombAIShortcut(int shortcutKey) {
            super(shortcutKey, "Defend Bomb");
        }
        
        /* (non-Javadoc)
         * @see seventh.client.AIShortcut#execute(seventh.shared.Console, seventh.client.ClientGame)
         */
        @Override
        public void execute(Console console, ClientGame game) {
        	int botId = getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                console.execute("ai " + botId + " defendBomb");
                console.execute("team_say " + players.getPlayer(botId).getName() + " defend the bomb!" );
            }    
        }
    }
    
    public static class TakeCoverAIShortcut extends AIShortcut {

        /**
         * @param shortcutKey
         * @param description
         */
        public TakeCoverAIShortcut(int shortcutKey) {
            super(shortcutKey, "Take Cover");
        }
        
        /* (non-Javadoc)
         * @see seventh.client.AIShortcut#execute(seventh.shared.Console, seventh.client.ClientGame)
         */
        @Override
        public void execute(Console console, ClientGame game) {
        	int botId = getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                Vector2f worldPosition = getMouseWorldPosition(game);
                console.execute("ai " + botId + " takeCover " + (int)worldPosition.x + " " + (int)worldPosition.y);
                console.execute("team_say " + players.getPlayer(botId).getName() + " take cover!" );
                //console.execute("speech " + 5);
            }    
        }
    }
}
