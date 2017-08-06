/*
 * see license.txt 
 */
package seventh.ai.basic.teamstrategy;

import seventh.ai.basic.Brain;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.group.AIGroup;
import seventh.ai.basic.group.AIGroupAttackAction;
import seventh.ai.basic.group.AIGroupDefendAction;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.math.Vector2f;
import seventh.shared.Command;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * The Team Death Match {@link TeamStrategy}.  
 * 
 * @author Tony
 *
 */
public class TDMTeamStrategy implements TeamStrategy {

    private Team team;
    AIGroup aIGroup;
    private DefaultAISystem aiSystem;
    private Timer time;
    /**
     * 
     */
    public TDMTeamStrategy(DefaultAISystem aiSystem, Team team) {
        this.team = team;
        this.aiSystem = aiSystem;
        this.aIGroup = new AIGroup(aiSystem);
        this.time = new Timer(false, 5_000);
        Cons.getImpl().addCommand(new Command("squadDefend") {
            
            @Override
            public void execute(Console console, String... args) {
                aIGroup.doAction(new AIGroupDefendAction(new Vector2f(275, 215)));
            }
        });
        
        Cons.getImpl().addCommand(new Command("squadAttack") {
            
            @Override
            public void execute(Console console, String... args) {
                aIGroup.doAction(new AIGroupAttackAction(new Vector2f(275, 215)));
            }
        });
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.teamstrategy.TeamStrategy#getDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double getDesirability(Brain brain) {    
        return 0;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.teamstrategy.TeamStrategy#getGoal(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {    
        return brain.getWorld().getGoals().wander();
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.teamstrategy.TeamStrategy#getTeam()
     */
    @Override
    public Team getTeam() {
        return this.team;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.AIGameTypeStrategy#playerKilled(seventh.game.PlayerInfo)
     */
    @Override
    public void playerKilled(PlayerInfo player) {        
    }
    
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.AIGameTypeStrategy#playerSpawned(seventh.game.PlayerInfo)
     */
    @Override
    public void playerSpawned(PlayerInfo player) {    
        if(player.getTeam().getId() == Team.AXIS_TEAM_ID) {
            if(player.isBot()) {
                Brain brain = aiSystem.getBrain(player);
                if(brain!=null) {
                    this.aIGroup.addMember(brain);
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.AIGameTypeStrategy#startOfRound(seventh.game.Game)
     */
    @Override
    public void startOfRound(GameInfo game) {
        //squad.doAction(new SquadDefendAction(new Vector2f(275, 215)));
    }

    /* (non-Javadoc)
     * @see seventh.ai.AIGameTypeStrategy#endOfRound(seventh.game.Game)
     */
    @Override
    public void endOfRound(GameInfo game) {
    }

    /* (non-Javadoc)
     * @see seventh.ai.AIGameTypeStrategy#update(seventh.shared.TimeStep, seventh.game.Game)
     */
    @Override
    public void update(TimeStep timeStep, GameInfo game) {
        time.update(timeStep);
//        if(time.isTime()) {
//            squad.doAction(new SquadDefendAction(new Vector2f(275, 215)));
//        }
    }

    /* (non-Javadoc)
     * @see seventh.shared.Debugable#getDebugInformation()
     */
    @Override
    public DebugInformation getDebugInformation() {
        DebugInformation me = new DebugInformation();
        // TODO
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
