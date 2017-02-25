/*
 * see license.txt 
 */
package seventh.ai.basic.teamstrategy;

import seventh.ai.basic.Brain;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.WaitAction;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.game.type.ObjectiveGameType;
import seventh.shared.TimeStep;

/**
 * Handles the objective based game type.
 * 
 * @author Tony
 *
 */
public class ObjectiveTeamStrategy implements TeamStrategy {

    private Team team;    
    private TeamStrategy strategy;
    private DefaultAISystem aiSystem;
    
    /**
     * 
     */
    public ObjectiveTeamStrategy(DefaultAISystem aiSystem, Team team) {
        this.aiSystem = aiSystem;
        this.team = team;        
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.teamstrategy.TeamStrategy#getDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double getDesirability(Brain brain) {
        if(strategy!=null) {
            return strategy.getDesirability(brain);
        }
        return 0;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.teamstrategy.TeamStrategy#getGoal(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {
        if(strategy!=null) {
            return strategy.getAction(brain);
        }
        return new WaitAction(1000);
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.teamstrategy.TeamStrategy#getTeam()
     */
    @Override
    public Team getTeam() {
        return this.team;
    }

        
    /* (non-Javadoc)
     * @see seventh.ai.AIGameTypeStrategy#startOfRound(seventh.game.Game)
     */
    @Override
    public void startOfRound(GameInfo game) {
        ObjectiveGameType gameType = (ObjectiveGameType) game.getGameType();
        
        if( gameType.getAttacker().getId() == this.team.getId() ) {
            strategy = new OffenseObjectiveTeamStrategy(this.aiSystem, team);
        }
        else {
            strategy = new DefenseObjectiveTeamStrategy(this.aiSystem, team);
        }
        
        strategy.startOfRound(game);
    }

    /* (non-Javadoc)
     * @see seventh.ai.AIGameTypeStrategy#endOfRound(seventh.game.Game)
     */
    @Override
    public void endOfRound(GameInfo game) {
        if(strategy!=null) {
            strategy.endOfRound(game);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.AIGameTypeStrategy#playerKilled(seventh.game.PlayerInfo)
     */
    @Override
    public void playerKilled(PlayerInfo player) {
        if(strategy != null) {
            strategy.playerKilled(player);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.AIGameTypeStrategy#playerSpawned(seventh.game.PlayerInfo)
     */
    @Override
    public void playerSpawned(PlayerInfo player) {
        if(strategy != null) {
            strategy.playerSpawned(player);
        }    
    }

    /* (non-Javadoc)
     * @see seventh.ai.AIGameTypeStrategy#update(seventh.shared.TimeStep, seventh.game.Game)
     */
    @Override
    public void update(TimeStep timeStep, GameInfo game) {    
        if(strategy!=null) {
            strategy.update(timeStep, game);
        }
    }

    /* (non-Javadoc)
     * @see seventh.shared.Debugable#getDebugInformation()
     */
    @Override
    public DebugInformation getDebugInformation() {
        DebugInformation me = new DebugInformation();
        me.add("strategy", this.strategy);
        return me;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getDebugInformation().toString();
    }
}
