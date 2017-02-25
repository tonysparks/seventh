/*
 * see license.txt 
 */
package seventh.server;

import leola.vm.Leola;
import leola.vm.exceptions.LeolaRuntimeException;
import leola.vm.lib.LeolaLibrary;
import leola.vm.types.LeoNamespace;
import seventh.ai.AICommand;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.actions.Action;
import seventh.game.Game;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.math.Vector2f;
import seventh.shared.Cons;

/**
 * The server-side (game side) script API.
 * 
 * @author Tony
 *
 */
public class GameServerLeolaLibrary implements LeolaLibrary {

    private Game game;
    
    /**
     * @param game
     */
    public GameServerLeolaLibrary(Game game) {
        this.game = game;
    }

    /* (non-Javadoc)
     * @see leola.vm.lib.LeolaLibrary#init(leola.vm.Leola, leola.vm.types.LeoNamespace)
     */
    @Override
    public void init(Leola runtime, LeoNamespace namespace) throws LeolaRuntimeException {
        runtime.putIntoNamespace(this, namespace);
    }
    
    
    /**
     * Adds a bot
     * 
     * @param id
     * @param name
     * @param team
     */
    public void addBot(int id, String name, String team) {
        game.addBot(id, name);
        if(team != null) {
            game.playerSwitchedTeam(id, team.toLowerCase().startsWith("axis") ? Team.AXIS_TEAM_ID : Team.ALLIED_TEAM_ID);
        }
    }
    
    public PlayerInfo getBot(int id) {
        PlayerInfo info = game.getPlayerById(id);
        if(info == null) {
            Cons.println("*** ERROR: No player with the id: " + id);
        }
        else {
            if(!info.isBot()) {
                Cons.println("*** ERROR: Player with the id: " + id + " is not a bot.");
                info = null;
            }
        }
        
        return info;
    }
    
    public void botPlace(int id, float x, float y) {
        PlayerInfo info = getBot(id);
        if(info != null && info.isAlive()) {
            info.getEntity().moveTo(new Vector2f(x, y));
        }
    }
    
    public void botCommand(int id, String command) {
        DefaultAISystem system = (DefaultAISystem)game.getAISystem();
        PlayerInfo info = getBot(id);
        if(info != null) {
            system.receiveAICommand(info, new AICommand(command));
        }        
    }

    public void botAction(int id, Action action) {
        DefaultAISystem system = (DefaultAISystem)game.getAISystem();
        PlayerInfo info = getBot(id);
        if(info != null) {
            system.getBrain(id).getCommunicator().makeTopPriority(action);
        }        
    }
}
