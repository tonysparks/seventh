/*
 * see license.txt 
 */
package seventh.game.game_types.cmd;

import java.util.List;

import leola.vm.Leola;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoObject;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.PlayerClass;
import seventh.game.Team;
import seventh.game.entities.Base;
import seventh.game.events.RoundStartedEvent;
import seventh.game.game_types.AbstractTeamGameType;
import seventh.game.net.NetCommanderGameTypeInfo;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.net.NetSquad;
import seventh.math.Vector2f;
import seventh.shared.EventDispatcher;
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
    
    private Base alliedBase;
    private Base axisBase;
    
    private Vector2f alliedBasePos, axisBasePos;
    
    private Squad alliedSquad, 
                  axisSquad;
    
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
                             long matchTime,
                             LeoObject config) {
        
        super(Type.CMD, runtime, alliedSpawnPoints, axisSpawnPoints, maxScore, matchTime);
        
        this.alliedBasePos = getVector2f(config, "alliedBasePos");
        this.axisBasePos   = getVector2f(config, "axisBasePos");
        
        this.alliedSquad = new Squad(getAlliedTeam(), 4, 4, 4, 4);
        this.axisSquad   = new Squad(getAxisTeam(), 4, 4, 4, 4);
    }
    
    private Vector2f getVector2f(LeoObject config, String name) {
        LeoObject v = config.getObject(name);
        Vector2f result = new Vector2f();
        if(!LeoObject.isTrue(v)) {
            return result;
        }

        if(v.isMap() || v.isClass() || v.isNativeClass()) {
            result.x = v.getObject("x").asFloat();
            result.y = v.getObject("y").asFloat();
        }
        else if(v.isArray()) {
            LeoArray array = v.as();
            result.x = array.get(0).asFloat();
            result.y = array.get(1).asFloat();
        }
        
        return result;
    }
    
    private Squad getSquad(Player player) {
        if(getAlliedTeam().onTeam(player)) {
            return alliedSquad;
        }
        
        if(getAxisTeam().onTeam(player)) {
            return axisSquad;
        }
        
        return null;
    }
    
    private Squad getSquad(Team team) {
        if(getAlliedTeam().getId() == team.getId()) {
            return alliedSquad;
        }
        
        if(getAxisTeam().getId() == team.getId()) {
            return axisSquad;
        }
        
        return null;
    }

    @Override
    protected NetGameTypeInfo createNetGameTypeInfo() {
        this.netGameTypeInfo = new NetCommanderGameTypeInfo();
        this.netGameTypeInfo.alliedSquad = new NetSquad();
        this.netGameTypeInfo.axisSquad = new NetSquad();
        
        return this.netGameTypeInfo;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.game_types.AbstractTeamGameType#getNetGameTypeInfo()
     */
    @Override
    public NetGameTypeInfo getNetGameTypeInfo() {        
        List<Player> allies = this.getAlliedTeam().getPlayers();
        for(int i = 0; i < allies.size(); i++) {
            Player p = allies.get(i);
            if(p != null) {
                this.netGameTypeInfo.alliedSquad.playerClasses[p.getId()] = p.getPlayerClass();
            }
        }
        
        List<Player> axis = this.getAxisTeam().getPlayers();
        for(int i = 0; i < axis.size(); i++) {
            Player p = axis.get(i);
            if(p != null) {
                this.netGameTypeInfo.axisSquad.playerClasses[p.getId()] = p.getPlayerClass();
            }
        }
        
        return super.getNetGameTypeInfo();
    }
    
    @Override
    protected void doRegisterListeners(Game game, EventDispatcher dispatcher) {
        if(this.alliedBase != null) {
            this.alliedBase.softKill();
        }
        if(this.axisBase != null) {
            this.axisBase.softKill();
        }
        
        this.alliedBase = game.newAlliedBase(alliedBasePos);
        this.axisBase   = game.newAxisBase(axisBasePos);
    }
    
    @Override
    protected boolean joinTeam(Team team, Player player) {
        Squad squad = getSquad(team);
        if(squad != null) {
            if(squad.assignPlayer(player, squad.getAvailableClass())) {
                return super.joinTeam(team, player);
            }            
        }
        
        return false;
    }
    
    @Override
    protected boolean leaveTeam(Team team, Player player) {
        Squad squad = getSquad(team);
        if(squad != null) {
            squad.unassignPlayer(player);
        }
        
        return super.leaveTeam(team, player);
    }     
    
    
    @Override
    public boolean switchPlayerClass(Player player, PlayerClass playerClass) {
        Squad squad = getSquad(player);
                
        if(squad == null) {
            return false;
        }
        
        squad.unassignPlayer(player);
        
        if(squad.assignPlayer(player, playerClass)) {
            return true;
        }
        
        playerClass = squad.getAvailableClass();
        if(playerClass != null) {
            if(squad.assignPlayer(player, playerClass)) {
                return true;
            }
        }
        
        return false;        
    }
    
    @Override
    public void start(Game game) {
        setGameState(GameState.IN_PROGRESS);
        getDispatcher().queueEvent(new RoundStartedEvent(this));
    }

    @Override
    protected GameState doUpdate(Game game, TimeStep timeStep) {
        
        GameState gameState = getGameState();
        if(gameState == GameState.IN_PROGRESS) {
            checkRespawns(timeStep, game);
            
            // first check for a tie
            if(!this.alliedBase.isAlive() && !this.axisBase.isAlive()) {
                setGameState(GameState.TIE);
            }
            else {
                if(!this.alliedBase.isAlive()) {
                    getAxisTeam().setScore(1);
                    setGameState(GameState.WINNER);
                }
                if(!this.axisBase.isAlive()) {
                    getAlliedTeam().setScore(1);
                    setGameState(GameState.WINNER);
                }
            }
            
        }
        
        return getGameState();
    }

}
