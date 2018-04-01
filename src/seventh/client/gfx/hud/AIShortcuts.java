/*
 * see license.txt
 */
package seventh.client.gfx.hud;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input.Keys;

import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientPlayers;
import seventh.client.inputs.Inputs;
import seventh.client.inputs.KeyMap;
import seventh.math.Vector2f;
import seventh.shared.Console;

/**
 * @author Tony
 *
 */
public class AIShortcuts {

    private static final int FOLLOW_ME=0,
                             //SUPRESS_FIRE=1,
                             //MOVE_TO=2,
                             //TAKE_COVER=3,
                             DEFEND_LEADER=4
                             ;
    
    private int[] shortcuts;
    private boolean[] isDown;
    private boolean[] isGroupDown;
    
    private List<AIShortcut> commands;
    private List<AIGroupCommand> groupCommands;
    
    private KeyMap keyMap;
    /**
     * 
     */
    public AIShortcuts(KeyMap keyMap) {
        this.keyMap = keyMap;   // TODO: Allow shortcuts to be configurable     
        this.commands = new ArrayList<AIShortcut>();
        commands.add(new FollowMeAIShortcut(Keys.NUM_1));
        commands.add(new SurpressFireAIShortcut(Keys.NUM_2));
        commands.add(new MoveToAIShortcut(Keys.NUM_3));
        commands.add(new TakeCoverAIShortcut(Keys.NUM_4));
        commands.add(new DefendLeaderAIShortcut(Keys.NUM_5));
        commands.add(new PlantBombAIShortcut(Keys.NUM_6));
        commands.add(new DefuseBombAIShortcut(Keys.NUM_7));
        commands.add(new DefendPlantedBombAIShortcut(Keys.NUM_8));
        
        this.shortcuts = new int[commands.size()];
        this.isDown = new boolean[this.shortcuts.length];
        
        for(int i = 0; i < this.shortcuts.length; i++) {
            this.shortcuts[i] = commands.get(i).getShortcutKey();
        }
        
        this.groupCommands = new ArrayList<>();
        this.groupCommands.add(new RegroupAIGroupCommand(Keys.F1));
        this.groupCommands.add(new DefendAIGroupCommand(Keys.F2));
        
        this.isGroupDown = new boolean[this.groupCommands.size()];
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
    public boolean checkShortcuts(Inputs inputs, ClientGame game, int memberIndex) {
        ClientPlayer aiPlayer = null; // TODO: 
                // game.getPlayerByFireTeamId(memberIndex);
        
        boolean result = false;
        for(int i = 0; i < this.shortcuts.length; i++) {
            boolean isKeyDown = inputs.isKeyDown(this.shortcuts[i]);
            if(isKeyDown) {
                this.isDown[i] = true;
            }
            if(this.isDown[i] && !isKeyDown) {
                this.commands.get(i).execute(game.getApp().getConsole(), game, aiPlayer);
                result = true;
            }
            
            if(!isKeyDown) {
                this.isDown[i] = false;
            }
        }
                
        return result;
    }
    
    public boolean checkGroupCommands(Inputs inputs, ClientGame game) {
        boolean result = false;
        for(int i = 0; i < this.groupCommands.size(); i++) {
            AIGroupCommand cmd = this.groupCommands.get(i);
            boolean isKeyDown = inputs.isKeyDown(cmd.getShortcutKey());
            if(isKeyDown) {
                this.isGroupDown[i] = true;
            }
            
            if(this.isGroupDown[i] && !isKeyDown) {
                cmd.execute(game);
                result = true;
            }
            
            if(!isKeyDown) {
                this.isGroupDown[i] = false;
            }
        }
        
        return result;
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
        public void execute(Console console, ClientGame game, ClientPlayer aiPlayer) {
            int botId = aiPlayer!=null ? aiPlayer.getId() : getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                ClientPlayer bot = players.getPlayer(botId);                
                int playerId = game.isLocalPlayerCommander() ? findClosestBot(game, bot) : game.getLocalPlayer().getId();
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
        public void execute(Console console, ClientGame game, ClientPlayer aiPlayer) {
            int botId = aiPlayer!=null ? aiPlayer.getId() : getBotId(game);
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
        public void execute(Console console, ClientGame game, ClientPlayer aiPlayer) {
            int botId = aiPlayer!=null ? aiPlayer.getId() : getBotId(game);
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
        public void execute(Console console, ClientGame game, ClientPlayer aiPlayer) {
            int botId = //getBotId(game);
                    aiPlayer!=null ? aiPlayer.getId() : -1;
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
        public void execute(Console console, ClientGame game, ClientPlayer aiPlayer) {
            int botId = aiPlayer!=null ? aiPlayer.getId() : getBotId(game);
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
        public void execute(Console console, ClientGame game, ClientPlayer aiPlayer) {
            int botId = aiPlayer!=null ? aiPlayer.getId() : getBotId(game);
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
        public void execute(Console console, ClientGame game, ClientPlayer aiPlayer) {
            int botId = aiPlayer!=null ? aiPlayer.getId() : getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                Vector2f worldPosition = getMouseWorldPosition(game);
                console.execute("ai " + botId + " takeCover " + (int)worldPosition.x + " " + (int)worldPosition.y);
                console.execute("team_say " + players.getPlayer(botId).getName() + " take cover!" );
                //console.execute("speech " + 5);
            }    
        }
    }
    
    public static class DefendLeaderAIShortcut extends AIShortcut {

        /**
         * @param shortcutKey
         * @param description
         */
        public DefendLeaderAIShortcut(int shortcutKey) {
            super(shortcutKey, "Defend Me");
        }
        
        /* (non-Javadoc)
         * @see seventh.client.AIShortcut#execute(seventh.shared.Console, seventh.client.ClientGame)
         */
        @Override
        public void execute(Console console, ClientGame game, ClientPlayer aiPlayer) {
            int botId = aiPlayer!=null ? aiPlayer.getId() : getBotId(game);
            if(botId > -1) {
                ClientPlayers players = game.getPlayers();
                ClientPlayer bot = players.getPlayer(botId);                
                int playerId = game.isLocalPlayerCommander() ? findClosestBot(game, bot) : game.getLocalPlayer().getId();
                console.execute("ai " + botId + " defendLeader " + playerId);
                console.execute("team_say " + players.getPlayer(botId).getName() + " help!" );
                //console.execute("speech " + 5);
            }    
        }
    }
    
    private class RegroupAIGroupCommand extends AIGroupCommand {
        
        public RegroupAIGroupCommand(int shortcutKey) {
            super(shortcutKey, "Regroup", commands.get(FOLLOW_ME));
        }        
    }
    
    private class DefendAIGroupCommand extends AIGroupCommand {
        
        public DefendAIGroupCommand(int shortcutKey) {
            super(shortcutKey, "Defend", commands.get(DEFEND_LEADER));
        }        
    }
}
