/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.type.obj;

import seventh.game.Game;
import seventh.game.GameInfo;

/**
 * @author Tony
 *
 */
public interface Objective {
    
    /**
     * Reset the objective
     * @param game
     */
    public void reset(Game game);
    
    /**
     * Initializes the objective
     * @param game
     */
    public void init(Game game);
    
    /**
     * Determines if the objective is completed
     * @param game
     * @return true if completed, false otherwise
     */
    public boolean isCompleted(GameInfo game);    
    
    
    /**
     * Determines if the objective is in progress, i.e., the
     * defenders must take action to destroy/defend it. (such as
     * a bomb is planted and the defenders must disarm it).
     * @param game
     * @return true if the objective is in progress which requires
     * the defenders to take action.
     */
    public boolean isInProgress(GameInfo game);
    
    
    /**
     * The {@link Objective}'s name.  This text is displayed
     * on the players HUD.
     * 
     * @return the name of the objective.
     */
    public String getName();
}
