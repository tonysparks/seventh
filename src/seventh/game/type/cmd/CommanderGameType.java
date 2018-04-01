/*
 * see license.txt 
 */
package seventh.game.type.cmd;

import java.util.List;

import leola.vm.Leola;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoObject;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.Team;
import seventh.game.entities.Base;
import seventh.game.events.RoundStartedEvent;
import seventh.game.net.NetCommanderGameTypeInfo;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.type.AbstractTeamGameType;
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

    @Override
    protected NetGameTypeInfo createNetGameTypeInfo() {
        this.netGameTypeInfo = new NetCommanderGameTypeInfo();
        return this.netGameTypeInfo;
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
        
        return super.joinTeam(team, player);
    }
    
    @Override
    protected boolean leaveTeam(Team team, Player player) {
        
        return super.leaveTeam(team, player);
    }     
    
    @Override
    public void start(Game game) {
        setGameState(GameState.IN_PROGRESS);
        getDispatcher().queueEvent(new RoundStartedEvent(this));
    }

    @Override
    protected GameState doUpdate(Game game, TimeStep timeStep) {
        
        checkRespawns(timeStep, game);
        
        GameState gameState = getGameState();
        if(gameState == GameState.IN_PROGRESS) {
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
