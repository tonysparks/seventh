/*
 * see license.txt 
 */
package seventh.game.type;

import java.util.List;

import leola.frontend.listener.EventDispatcher;
import leola.frontend.listener.EventMethod;
import leola.vm.Leola;
import seventh.game.Game;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.game.entities.Entity;
import seventh.game.entities.Flag;
import seventh.game.entities.PlayerEntity;
import seventh.game.entities.Entity.OnTouchListener;
import seventh.game.events.FlagCapturedEvent;
import seventh.game.events.FlagCapturedListener;
import seventh.game.events.FlagReturnedEvent;
import seventh.game.events.FlagReturnedListener;
import seventh.game.events.FlagStolenEvent;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundStartedEvent;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Capture the Flag game type.
 * 
 * @author Tony
 *
 */
public class CaptureTheFlagGameType extends AbstractTeamGameType {
	
	class FlagOnTouchListener implements OnTouchListener {
		Flag flag;
		
		FlagOnTouchListener(Flag flag) {
			this.flag = flag;
		}
		
		@Override
		public void onTouch(Entity me, Entity other) {
			if(!flag.isBeingCarried()) {
				if(other.getType()==seventh.game.entities.Entity.Type.PLAYER) {
					PlayerEntity otherPlayer = (PlayerEntity)other;
					if(otherPlayer.isAlive()) {
						/*
						 * Now, based on which team the player is on,
						 * we determine what to do with the flag.
						 * 
						 * If the player is on an opposing team, they
						 * force the flag back to their home base
						 */
						int teamId = otherPlayer.getTeam().getId(); 
						
						if( (teamId==Team.ALLIED_TEAM_ID && flag.getType()==seventh.game.entities.Entity.Type.ALLIED_FLAG) || 
							(teamId==Team.AXIS_TEAM_ID && flag.getType()==seventh.game.entities.Entity.Type.AXIS_FLAG) ) {
							flag.carriedBy(otherPlayer);
							getDispatcher().queueEvent(new FlagStolenEvent(this, flag, otherPlayer.getId()));
						}
						else {
							if(!flag.isAtHomeBase()) {
								flag.returnHome();
								getDispatcher().queueEvent(new FlagReturnedEvent(this, flag, otherPlayer.getId()));
							}
						}				
					}
				}
			}
		}	
	}
	
	
	private Flag axisFlag, alliedFlag;
	
	private Vector2f alliedFlagSpawn,
					 axisFlagSpawn;
	
	private Rectangle alliedHomeBase, axisHomeBase;
	
	private long spawnDelay;
	
	/**
	 * @param runtime
	 * @param maxScore
	 * @param matchTime
	 */
	public CaptureTheFlagGameType(Leola runtime, 
			 List<Vector2f> alliedSpawnPoints,
			 List<Vector2f> axisSpawnPoints, 
			 int maxScore, 
			 long matchTime,
			 Vector2f alliedFlagSpawn,
			 Vector2f axisFlagSpawn,
			 Rectangle alliedHomeBase,
			 Rectangle axisHomeBase,
			 long spawnDelay) {
		
		super(Type.CTF, runtime, alliedSpawnPoints, axisSpawnPoints, maxScore, matchTime);
		
		this.alliedFlagSpawn = alliedFlagSpawn;
		this.axisFlagSpawn = axisFlagSpawn;
		
		this.alliedHomeBase = alliedHomeBase;		
		this.axisHomeBase = axisHomeBase;
		
		this.spawnDelay = spawnDelay;
	}

	/**
	 * @return the alliedHomeBase
	 */
	public Rectangle getAlliedHomeBase() {
		return alliedHomeBase;
	}
	
	/**
	 * @return the axisHomeBase
	 */
	public Rectangle getAxisHomeBase() {
		return axisHomeBase;
	}
	
	/**
	 * @return the alliedFlag
	 */
	public Flag getAlliedFlag() {
		return alliedFlag;
	}
	
	/**
	 * @return the axisFlag
	 */
	public Flag getAxisFlag() {
		return axisFlag;
	}
	
	
	
	/* (non-Javadoc)
	 * @see palisma.game.type.GameType#registerListeners(leola.frontend.listener.EventDispatcher)
	 */
	@Override
	protected void doRegisterListeners(final Game game, EventDispatcher dispatcher) {
		
		this.alliedFlag = game.newAlliedFlag(this.alliedFlagSpawn);
		this.alliedFlag.onTouch = new FlagOnTouchListener(this.alliedFlag);
		
		this.axisFlag = game.newAxisFlag(this.axisFlagSpawn);
		this.axisFlag.onTouch = new FlagOnTouchListener(this.axisFlag);		
		
		
		dispatcher.addEventListener(FlagCapturedEvent.class, new FlagCapturedListener() {
			
			@Override
			@EventMethod
			public void onFlagCapturedEvent(FlagCapturedEvent event) {				
				if(isInProgress()) {
					PlayerInfo carrier = game.getPlayerById(event.getPlayerId());
					if(carrier !=null) {												
						carrier.getTeam().score(1);
					}												
				}
			}
		});
		
		dispatcher.addEventListener(FlagReturnedEvent.class, new FlagReturnedListener() {
			
			@Override
			@EventMethod
			public void onFlagReturnedEvent(FlagReturnedEvent event) {						
			}
		});	
		
		dispatcher.addEventListener(PlayerKilledEvent.class, new PlayerKilledListener() {
			
			@Override
			public void onPlayerKilled(PlayerKilledEvent event) {
				event.getPlayer().applySpawnDelay(spawnDelay);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#start(seventh.game.Game)
	 */
	@Override
	public void start(Game game) {
		getDispatcher().queueEvent(new RoundStartedEvent(this));
	}

	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#update(leola.live.TimeStep)
	 */
	@Override
	protected GameState doUpdate(Game game, TimeStep timeStep) {
		
		if(GameState.IN_PROGRESS == getGameState()) {
			List<Team> leaders = getTeamsWithHighScore();
						
			if(this.getRemainingTime() <= 0 || (leaders.get(0).getScore() >= getMaxScore()) ) {
				
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
		checkSpectating(timeStep, game);
		
		if (GameState.IN_PROGRESS == getGameState()) {
			
			/* Check and see if this flag has been
			 * captured
			 */
			if(this.axisFlag.isBeingCarried()) {
				if(this.axisFlag.getBounds().intersects(axisHomeBase)) {
					getDispatcher().queueEvent(new FlagCapturedEvent(this, this.axisFlag, this.axisFlag.getCarriedBy().getId()));
					this.axisFlag.returnHome();
				}
			}
			
			
			if(this.alliedFlag.isBeingCarried()) {
				if(this.alliedFlag.getBounds().intersects(alliedHomeBase)) {
					getDispatcher().queueEvent(new FlagCapturedEvent(this, this.alliedFlag, this.alliedFlag.getCarriedBy().getId()));
					this.alliedFlag.returnHome();
				}
			}
			
		}
		
		return getGameState();
		
	}	
}
