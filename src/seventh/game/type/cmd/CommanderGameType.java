/*
 * see license.txt 
 */
package seventh.game.type.cmd;

import java.util.List;

import leola.frontend.listener.EventDispatcher;
import leola.vm.Leola;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.Team;
import seventh.game.events.RoundStartedEvent;
import seventh.game.net.NetCommanderGameTypeInfo;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.type.AbstractTeamGameType;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Commander Game Type.
 * 
 * Concept:
 * 
 * 2 - AIGroup leaders for each team (they don't control any characters, but manage resources and
 * command FireTeams
 * 
 * Resources:
 *   Ammo
 *   Tanks
 *   Respawns
 *   Location of Spawns
 *   Material (for building walls, ?)
 * 
 * @author Tony
 *
 */
public class CommanderGameType extends AbstractTeamGameType {

    private NetCommanderGameTypeInfo netGameTypeInfo;
    
    private Squad alliedSquad;
    private Squad axisSquad;
    
    /**
     * @param type
     * @param runtime
     * @param alliedSpawnPoints
     * @param axisSpawnPoints
     * @param maxScore
     * @param matchTime
     */
    public CommanderGameType(Leola runtime, 
                             List<Vector2f> alliedSpawnPoints, 
                             List<Vector2f> axisSpawnPoints, 
                             int maxScore,
                             long matchTime) {
        
        super(Type.CMD, runtime, alliedSpawnPoints, axisSpawnPoints, maxScore, matchTime);
        
        this.alliedSquad = new Squad(getAlliedTeam());
        this.axisSquad = new Squad(getAxisTeam());     
        
        this.netGameTypeInfo.alliedSquad = this.alliedSquad.getNetSquad();
        this.netGameTypeInfo.axisSquad = this.axisSquad.getNetSquad();
    }
    
    private Squad getSquad(Team team) {
        if(team.isAlliedTeam()) {
            return this.alliedSquad;
        }
        if(team.isAxisTeam()) {
            return this.axisSquad;
        }
        return null;
    }

    @Override
    protected NetGameTypeInfo createNetGameTypeInfo() {
        this.netGameTypeInfo = new NetCommanderGameTypeInfo();
        return this.netGameTypeInfo;
    }
    
    @Override
    protected void doRegisterListeners(Game game, EventDispatcher dispatcher) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected boolean joinTeam(Team team, Player player) {
        Squad squad = getSquad(team);
        if(squad!=null) {
            if(!squad.addPlayer(player)) {
                return false;        
            }
        }
        
        return super.joinTeam(team, player);
    }
    
    @Override
    protected boolean leaveTeam(Team team, Player player) {
        Squad squad = getSquad(team);
        if(squad!=null) {
            squad.removePlayer(player);
        }
        
        return super.leaveTeam(team, player);
    }     
    
    @Override
    public void start(Game game) {
        setGameState(GameState.IN_PROGRESS);
        getDispatcher().queueEvent(new RoundStartedEvent(this));
    }

    @Override
    protected GameState doUpdate(Game game, TimeStep timeStep) {
        this.alliedSquad.update(timeStep);
        this.axisSquad.update(timeStep);
        
        checkRespawns(timeStep, game);
        
        return getGameState();
    }

}
