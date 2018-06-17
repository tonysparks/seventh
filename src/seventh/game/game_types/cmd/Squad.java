/*
 * see license.txt 
 */
package seventh.game.game_types.cmd;

import seventh.game.Player;
import seventh.game.PlayerClass;
import seventh.game.Team;

/**
 * @author Tony
 *
 */
public class Squad {

    private final int maxEngineers,
                      maxScouts,
                      maxInfantry,
                      maxDemolition;

    private Team team;
    
    private int numEngineers,
                numScouts,
                numInfantry,
                numDemolition;
    
    /**
     * @param maxEngineers
     * @param maxScouts
     * @param maxInfantry
     * @param maxDemolition
     */
    public Squad(Team team, int maxEngineers, int maxScouts, int maxInfantry, int maxDemolition) {
        this.team = team;
        this.maxEngineers = maxEngineers;
        this.maxScouts = maxScouts;
        this.maxInfantry = maxInfantry;
        this.maxDemolition = maxDemolition;
    }
    
    
    /**
     * Assign the Player to the team squad
     * 
     * @param player
     * @return true if successful, otherwise false
     */
    public boolean assignPlayer(Player player, PlayerClass playerClass) {                
        if(playerClass == PlayerClass.Engineer) {
            if(numEngineers + 1 < maxEngineers) {
                numEngineers++;
                player.setPlayerClass(playerClass);
                return true;
            }
        }
        if(playerClass == PlayerClass.Scout) {
            if(numScouts + 1 < maxScouts) {
                numScouts++;
                player.setPlayerClass(playerClass);
                return true;
            }
        }
        
        if(playerClass == PlayerClass.Infantry) {
            if(numInfantry + 1 < maxInfantry) {
                numInfantry++;
                player.setPlayerClass(playerClass);
                return true;
            }
        }
        
        if(playerClass == PlayerClass.Demolition) {
            if(numDemolition + 1 < maxDemolition) {
                numDemolition++;
                player.setPlayerClass(playerClass);
                return true;
            }
        }
        
        return false;
    }
    
    public void unassignPlayer(Player player) {
        PlayerClass playerClass = player.getPlayerClass();
        if(playerClass == PlayerClass.Engineer) {            
            numEngineers--;
        }
        else if(playerClass == PlayerClass.Scout) {
            numScouts--;
        }
        else if(playerClass == PlayerClass.Infantry) {            
            numInfantry--;            
        }
        else if(playerClass == PlayerClass.Demolition) {
            numDemolition--;
        }
    }

    public PlayerClass getAvailableClass() {
        if(numEngineers + 1 < maxEngineers) {
            return PlayerClass.Engineer;
        }
        if(numScouts + 1 < maxScouts) {
            return PlayerClass.Scout;
        }
        if(numInfantry + 1 < maxInfantry) {
            return PlayerClass.Infantry;
        }
        if(numDemolition + 1 < maxDemolition) {
            return PlayerClass.Demolition;
        }
        
        return null;
    }
}
