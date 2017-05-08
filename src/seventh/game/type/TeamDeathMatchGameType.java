/*
 * see license.txt 
 */
package seventh.game.type;

import java.util.List;

import leola.frontend.listener.EventDispatcher;
import leola.frontend.listener.EventMethod;
import leola.vm.Leola;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundStartedEvent;
import seventh.math.Vector2f;
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
                        if(killed != null && (killer.getId() == killed.getId())) {
                                killed.getTeam().score(-1);
                                return;
                            
                        }
                        
                        killer.getTeam().score(1);
                    }            
                }
            }
        });
    }
        
    /* (non-Javadoc)
     * @see seventh.game.type.GameType#start(seventh.game.Game)
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
    	/*
		 * Refactoring target : if and else if statements
		 * Refactoring name : Introduce explaining variable, erase nested if statements 
		 * Bad smell(reason) : Put the result of the expression, 
		 * or parts of the expression, 
		 * in a temporary variable with a name that explains the purpose,
		 * exist needless nested statements 
		 * 
		 */
    	List<Team> leaders = getTeamsWithHighScore();
    	boolean leadersSize = leaders.size()>1;
    	boolean remainTime = this.getRemainingTime()<=0;
    	boolean isUnlimitedScore = getMaxScore() <= 0;
    	boolean whatScore = leaders.get(0).getScore() >= getMaxScore();
    	boolean inProgress = GameState.IN_PROGRESS ==getGameState();
    	if(inProgress && remainTime || (whatScore && !isUnlimitedScore)) {
                if(leadersSize) {
                    setGameState(GameState.TIE);
                    getDispatcher().queueEvent(new RoundEndedEvent(this, null, game.getNetGameStats()));
                }
                else {
                    setGameState(GameState.WINNER);
                    getDispatcher().queueEvent(new RoundEndedEvent(this, leaders.get(0), game.getNetGameStats()));
                }
        }
        
        checkRespawns(timeStep, game);
        
        return getGameState();
    }
}
