/*
 * see license.txt 
 */
package seventh.ai;

import seventh.ai.basic.AIConfig;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.shared.Debugable;
import seventh.shared.Randomizer;
import seventh.shared.Updatable;

/**
 * The AI Subsystem
 * 
 * @author Tony
 *
 */
public interface AISystem extends Updatable, Debugable {
    
    /**
     * Initialize the AI system
     * @param game
     */
    public void init(GameInfo game);
    
    public AIConfig getConfig();
    public Randomizer getRandomizer();
    
    /**
     * Cleans up any allocated resources
     */
    public void destroy();
    
    /**
     * A player has joined
     * @param player
     */
    public void playerJoined(PlayerInfo player);
    
    /**
     * A player has left the game
     * @param player
     */
    public void playerLeft(PlayerInfo player);
    
    /**
     * A player has just spawned
     * @param player
     */
    public void playerSpawned(PlayerInfo player);
    
    /**
     * A player has been killed
     * @param player
     */
    public void playerKilled(PlayerInfo player);
    
    /**
     * The start of a match
     * @param game
     */
    public void startOfRound(GameInfo game);
    
    /**
     * The end of a match
     * @param game
     */
    public void endOfRound(GameInfo game);
    
    /**
     * Receives an {@link AICommand}
     * @param forBot
     * @param command
     */
    public void receiveAICommand(PlayerInfo forBot, AICommand command);
}
