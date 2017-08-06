/*
 * see license.txt 
 */
package seventh.ai.basic.teamstrategy;

import seventh.ai.basic.Brain;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.group.AIGroup;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.game.type.cmd.CommanderGameType;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class CommanderTeamStrategy implements TeamStrategy {

    private CommanderGameType gameType;
    private DefaultAISystem aiSystem;
    private Team team;
    
    private AIGroup able, baker, charlie;
    
    /**
     * 
     */
    public CommanderTeamStrategy(CommanderGameType gameType, DefaultAISystem aiSystem, Team team) {
        this.gameType = gameType;
        this.aiSystem = aiSystem;
        this.team = team;
        
        this.able = new AIGroup(aiSystem);
        this.baker = new AIGroup(aiSystem);
        this.charlie = new AIGroup(aiSystem);
    }

    @Override
    public DebugInformation getDebugInformation() {
        return new DebugInformation();
    }

    @Override
    public Team getTeam() {
        return this.team;
    }

    @Override
    public Action getAction(Brain brain) {
        return brain.getWorld().getGoals().waitAction(999999999);
    }

    @Override
    public double getDesirability(Brain brain) {
        return 0.5;
    }

    @Override
    public void startOfRound(GameInfo game) {        
    }

    @Override
    public void endOfRound(GameInfo game) {        
    }

    @Override
    public void playerSpawned(PlayerInfo player) {        
    }

    @Override
    public void playerKilled(PlayerInfo player) {
        
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.teamstrategy.TeamStrategy#update(seventh.shared.TimeStep, seventh.game.GameInfo)
     */
    @Override
    public void update(TimeStep timeStep, GameInfo game) {
        
    }

    
}
