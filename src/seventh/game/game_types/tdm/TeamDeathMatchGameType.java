/*
 * see license.txt 
 */
package seventh.game.game_types.tdm;

import java.util.List;

import leola.vm.Leola;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundStartedEvent;
import seventh.game.game_types.AbstractTeamGameType;
import seventh.math.Vector2f;
import seventh.shared.EventDispatcher;
import seventh.shared.EventMethod;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class TeamDeathMatchGameType extends AbstractTeamGameType {
        
    /**
     * @param maxKills
     * @param matchTime
     */
    public TeamDeathMatchGameType(Leola runtime, List<Vector2f> alliedSpawns, List<Vector2f> axisSpawns, int maxKills, long matchTime) {
        super(Type.TDM, runtime, alliedSpawns, axisSpawns, maxKills, matchTime);        
    }
    
    /* (non-Javadoc)
     * @see palisma.game.type.GameType#registerListeners(leola.frontend.listener.EventDispatcher)
     */
    @Override
    protected void doRegisterListeners(final Game game, EventDispatcher dispatcher) {
        dispatcher.addEventListener(PlayerKilledEvent.class, new PlayerKilledListener() {
            
            @Override
            @EventMethod
            public void onPlayerKilled(PlayerKilledEvent event) {        
                if(isInProgress()) {
                    PlayerInfo killer = game.getPlayerById(Integer.valueOf((int)(event.getKillerId())));
                    if(killer!=null) {
                        Player killed = event.getPlayer();
                        if(killed != null) {
                            // killing yourself or a teammate causes
                            // a negative score
                            if(killer.getId() == killed.getId() ||
                               killer.getTeam() == killed.getTeam()) {
                                killed.getTeam().score(-1);
                                return;
                            }
                        }
                        
                        killer.getTeam().score(1);
                    }            
                }
            }
        });
    }
        
    /* (non-Javadoc)
     * @see seventh.game.game_types.GameType#start(seventh.game.Game)
     */
    @Override
    public void start(Game game) {
        setGameState(GameState.IN_PROGRESS);
        getDispatcher().queueEvent(new RoundStartedEvent(this));
    }
    
    /*
     * (non-Javadoc)
     * @see palisma.game.type.GameType#update(leola.live.TimeStep)
     */
    @Override
    protected GameState doUpdate(Game game, TimeStep timeStep) {                
        if(GameState.IN_PROGRESS == getGameState()) {
            List<Team> leaders = getTeamsWithHighScore();
            
            boolean isUnlimitedScore = getMaxScore() <= 0;
            
            if(this.getRemainingTime() <= 0 || (leaders.get(0).getScore() >= getMaxScore() && !isUnlimitedScore) ) {
                
                if(leaders.size() > 1) {
                    setGameState(GameState.TIE);
                    getDispatcher().queueEvent(new RoundEndedEvent(this, null, game.getNetGameStats()));
                }
                else {
                    setGameState(GameState.WINNER);
                    getDispatcher().queueEvent(new RoundEndedEvent(this, leaders.get(0), game.getNetGameStats()));
                }
            }
        }
        
        
        checkRespawns(timeStep, game);
        
        return getGameState();
    }
}
