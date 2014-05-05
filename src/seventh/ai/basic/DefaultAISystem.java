/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.io.File;
import java.util.Random;

import leola.vm.Args;
import leola.vm.Leola;
import seventh.ai.AISystem;
import seventh.game.Game;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.PlayerInfos;
import seventh.game.PlayerInfos.PlayerInfoIterator;
import seventh.game.Team;
import seventh.game.type.GameType;
import seventh.math.Rectangle;
import seventh.shared.Cons;
import seventh.shared.DebugDraw;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class DefaultAISystem implements AISystem {

	private GameInfo game;
	private TeamStrategy alliedAIStrategy;
	private TeamStrategy axisAIStrategy;
	
	private Brain[] brains;
	
	private Zones zones;
	private Stats stats;
	
	private Random random;
	private Leola runtime;
	
	/**
	 * 
	 */
	public DefaultAISystem() {
		this.brains = new Brain[Game.MAX_PLAYERS];
		
		try {
			Args args = new Args();
			args.enableVMThreadLocal(false);
			this.runtime = new Leola(args);
			this.runtime.eval(new File("./seventh/ai/goals.leola"));
		}
		catch(Exception e) {
			Cons.println("Unable to load the Leola runtime : " + e);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#init(seventh.game.Game)
	 */
	@Override
	public void init(final GameInfo game) {
		this.game = game;
		this.random = game.getRandom();
		
		this.zones = new Zones(game);
		this.stats = new Stats(game, this.zones);
		
		GameType gameType = game.getGameType();
		
		if(gameType.getType().equals(GameType.Type.OBJ)) {
			this.alliedAIStrategy = new ObjectiveTeamStrategy(this, gameType.getAlliedTeam());
			this.axisAIStrategy = new ObjectiveTeamStrategy(this, gameType.getAxisTeam());
		}
		else {
			this.alliedAIStrategy = new TDMTeamStrategy();
			this.axisAIStrategy = new TDMTeamStrategy();
		}
		
		PlayerInfos players = game.getPlayerInfos();
		players.forEachPlayerInfo(new PlayerInfoIterator() {
			
			@Override
			public void onPlayerInfo(PlayerInfo player) {
				if(player.isBot()) {					
					brains[player.getId()] = new Brain(getStrategyFor(player), new World(game, zones), player);
				}	
			}
		});
				
	}
	
	private TeamStrategy getStrategyFor(PlayerInfo player) {
		if(Team.ALLIED_TEAM == player.getTeamId()) {
			return this.alliedAIStrategy;
		}
		else if(Team.AXIS_TEAM == player.getTeamId()) {
			return this.axisAIStrategy;
		}
		
		return null;
	}
	
	/**
	 * @return the runtime
	 */
	public Leola getRuntime() {
		return runtime;
	}
	
	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}
	
	/**
	 * @return the zones
	 */
	public Zones getZones() {
		return zones;
	}
	
	/**
	 * @return the stats
	 */
	public Stats getStats() {
		return stats;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#destroy()
	 */
	@Override
	public void destroy() {	
	}

	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#playerJoined(seventh.game.Player)
	 */
	@Override
	public void playerJoined(PlayerInfo player) {
		if(player.isBot()) {
			this.brains[player.getId()] = new Brain(getStrategyFor(player), new World(game, zones), player);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#playerLeft(seventh.game.Player)
	 */
	@Override
	public void playerLeft(PlayerInfo player) {
		this.brains[player.getId()] = null;		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#playerSpawned(seventh.game.Player)
	 */
	@Override
	public void playerSpawned(PlayerInfo player) {
		Brain brain = getBrain(player);
		if(brain != null) {			
			brain.spawned();
		}
		
		this.alliedAIStrategy.playerSpawned(player);
		this.axisAIStrategy.playerSpawned(player);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#playerKilled(seventh.game.Player)
	 */
	@Override
	public void playerKilled(PlayerInfo player) {
		Brain brain = getBrain(player);
		if(brain != null) {			
			brain.killed();
		}
		
		this.alliedAIStrategy.playerKilled(player);
		this.axisAIStrategy.playerKilled(player);
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		for(int i = 0; i < brains.length; i++) {
			Brain brain = brains[i];
			if(brain != null) {
				brain.update(timeStep);
			}
		}
		
		
		this.alliedAIStrategy.update(timeStep, game);
		this.axisAIStrategy.update(timeStep, game);
				
		//debugDraw();
		//debugDrawZones();
	}

	private void debugDrawZones() {
		Zone[][] zs = zones.getZones();
		for(int y = 0; y < zs.length; y++) {
			for(int x = 0; x < zs[y].length; x++) {
				Zone z = zs[y][x];
				Rectangle bounds = z.getBounds();
				DebugDraw.drawStringRelative(z.getId()+"", bounds.x, bounds.y, 0xff00ffff);
				DebugDraw.drawRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0xffff00ff);
			}
		}
	}
	
	/**
	 * Helpful debug for AI
	 */
	private void debugDraw() {
		
		int y = 100;
		int x = 20;
		final int yOffset = 20;
		int color = 0xff00ff00;
		
		final String message = "%-16s %-3s %-10s";
		DebugDraw.drawString(String.format(message, "Name", "ID", "State"), x, y, color);
		DebugDraw.drawString("====================================", x, y += yOffset, color);
		for(int i = 0; i < brains.length; i++) {
			Brain brain = brains[i];
			if(brain != null) {	
				String state = "DEAD";
				if(brain.getPlayer().isAlive()) {
					state = brain.getEntityOwner().getCurrentState().name();					
				}
				String text = String.format(message, brain.getPlayer().getName(), brain.getPlayer().getId(), state);				
				DebugDraw.drawString(text, x, y += yOffset, color);
			}
		}
	}
	

	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#startOfRound(seventh.game.Game)
	 */
	@Override
	public void startOfRound(GameInfo game) {
		zones.calculateBombTargets();
		
		alliedAIStrategy.startOfRound(game);
		axisAIStrategy.startOfRound(game);
	}

	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#endOfRound(seventh.game.Game)
	 */
	@Override
	public void endOfRound(GameInfo game) {

		alliedAIStrategy.endOfRound(game);
		axisAIStrategy.endOfRound(game);
	}
	/**
	 * @param playerId
	 * @return the {@link Brain} from the supplied player Id.
	 */	
	public Brain getBrain(int playerId) {
		if(playerId >= 0 && playerId < brains.length) {
			return brains[playerId];
		}
		return null;
	}
	
	/**
	 * @param player
	 * @return the {@link Brain} from the supplied player
	 */
	public Brain getBrain(PlayerInfo player) {
		if(player != null) {
			return getBrain(player.getId());
		}
		return null;
	}
}
