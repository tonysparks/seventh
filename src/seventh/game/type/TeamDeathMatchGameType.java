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
	
	private List<Vector2f> axisSpawns;
	private List<Vector2f> alliedSpawns;
	
	/**
	 * @param maxKills
	 * @param matchTime
	 */
	public TeamDeathMatchGameType(Leola runtime, List<Vector2f> alliedSpawns, List<Vector2f> axisSpawns, int maxKills, long matchTime) {
		super(Type.TDM, runtime, maxKills, matchTime);
		this.axisSpawns = axisSpawns;
		this.alliedSpawns = alliedSpawns;
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
							if(killer.getId() == killed.getId()) {
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
	 * @see seventh.game.type.GameType#getAlliedSpawnPoints()
	 */
	@Override
	public List<Vector2f> getAlliedSpawnPoints() {
		return this.alliedSpawns;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#getAxisSpawnPoints()
	 */
	@Override
	public List<Vector2f> getAxisSpawnPoints() {	
		return this.axisSpawns;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#start(seventh.game.Game)
	 */
	@Override
	public void start(Game game) {
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
		
		
		if (GameState.IN_PROGRESS == getGameState()) {
			Player[] players = game.getPlayers().getPlayers();
			for (int i = 0; i < players.length; i++) {
				Player player = players[i];
				if (player != null) {
					if (player.isDead() && !player.isSpectating()) {
						player.updateSpawnTime(timeStep);
						if (player.readyToSpawn()) {
							game.spawnPlayerEntity(player.getId());
						}
					}
				}
			}
		}
		
		return getGameState();
	}
}
