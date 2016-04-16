/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import leola.vm.Leola;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import leola.vm.types.LeoUserFunction;
import seventh.ai.AICommand;
import seventh.ai.AISystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.commands.AICommands;
import seventh.ai.basic.teamstrategy.CaptureTheFlagTeamStrategy;
import seventh.ai.basic.teamstrategy.ObjectiveTeamStrategy;
import seventh.ai.basic.teamstrategy.TDMTeamStrategy;
import seventh.ai.basic.teamstrategy.TeamStrategy;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.PlayerInfos;
import seventh.game.PlayerInfos.PlayerInfoIterator;
import seventh.game.Team;
import seventh.game.type.GameType;
import seventh.math.Rectangle;
import seventh.shared.AssetLoader;
import seventh.shared.AssetWatcher;
import seventh.shared.Cons;
import seventh.shared.DebugDraw;
import seventh.shared.FileSystemAssetWatcher;
import seventh.shared.Randomizer;
import seventh.shared.Scripting;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;

/**
 * The Default AI System implementation.  This implementation is based
 * off of making Reactive Decisions based off of Sensory inputs.  If no
 * immediate decision can be made, a general goal (objective) is worked
 * towards.
 * 
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
	
	private Randomizer random;
	private Leola runtime;
	
	private Actions goals;
	private AICommands aiCommands;
	
	private AssetWatcher watcher;
	
	private AIConfig config;
	private World world;
	
	private final Map<String, PersonalityTraits> personalities;
	private static final PersonalityTraits defaultPersonality = new PersonalityTraits();
	static {
		defaultPersonality.accuracy = 0.6;
		defaultPersonality.aggressiveness = 0.7;
		defaultPersonality.curiosity = 0.65;
		defaultPersonality.obedience = 1.0;
	}
	
	/**
	 * 
	 */
	public DefaultAISystem() {
		this.brains = new Brain[SeventhConstants.MAX_PLAYERS];	
		this.personalities = new HashMap<>();
				
		try {						
			this.runtime = Scripting.newRuntime();
			this.watcher = new FileSystemAssetWatcher(new File("./assets/ai"));
			this.watcher.loadAsset("goals.leola", new AssetLoader<File>() {				
				@Override
				public File loadAsset(String filename) throws IOException {
					try {
						Cons.println("Evaluating: " + filename);
						runtime.eval(new File(filename));
						Cons.println("Successfully evaluated: " + filename);
					} 
					catch (Exception e) {
						Cons.println("*** Error evaluating: " + filename);
						Cons.println("*** " + e);
					}
					return null;
				}
			});
			
			
			this.watcher.loadAsset("personalities.leola", new AssetLoader<File>() {				
				@Override
				public File loadAsset(String filename) throws IOException {
					try {
						Cons.println("Evaluating: " + filename);
						runtime.eval(new File(filename));
						
						LeoObject config = runtime.get("personalities");
						if(LeoObject.isTrue(config) && config.isMap()) {
							LeoMap a = config.as();
							a.foreach(new LeoUserFunction() {
								public LeoObject call(LeoObject key, LeoObject value) {
									PersonalityTraits traits = new PersonalityTraits();
				
									traits.accuracy = value.getObject("accuracy").asDouble();
									traits.aggressiveness = value.getObject("aggressiveness").asDouble();
									traits.curiosity = value.getObject("curiosity").asDouble();
									traits.obedience = value.getObject("obedience").asDouble();
									
									personalities.put(key.toString(), traits);
									
									return LeoObject.NULL;
								}
							});
							
						}
						
						Cons.println("Successfully evaluated: " + filename);
					} 
					catch (Exception e) {
						Cons.println("*** Error evaluating: " + filename);
						Cons.println("*** " + e);
					}
					return null;
				}
			});
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
		this.random = new Randomizer(game.getRandom());
		this.config = new AIConfig(game.getConfig().getConfig());
		
		this.zones = new Zones(game);
		this.stats = new Stats(game, this.zones);
		
		initScriptingEngine();
		
		this.aiCommands = new AICommands(this);
		this.world = new World(config, game, zones, goals, random);
		
		GameType gameType = game.getGameType();
		
		switch(gameType.getType()) {
			case CTF:
				this.alliedAIStrategy = new CaptureTheFlagTeamStrategy(this, gameType.getAlliedTeam());
				this.axisAIStrategy = new CaptureTheFlagTeamStrategy(this, gameType.getAxisTeam());
				break;
			case OBJ:
				this.alliedAIStrategy = new ObjectiveTeamStrategy(this, gameType.getAlliedTeam());
				this.axisAIStrategy = new ObjectiveTeamStrategy(this, gameType.getAxisTeam());
				break;
			case TDM:				
			default:
				this.alliedAIStrategy = new TDMTeamStrategy(this, gameType.getAlliedTeam());
				this.axisAIStrategy = new TDMTeamStrategy(this, gameType.getAxisTeam());
				break;
		
		}
        
		
		PlayerInfos players = game.getPlayerInfos();
		players.forEachPlayerInfo(new PlayerInfoIterator() {
			
			@Override
			public void onPlayerInfo(PlayerInfo player) {
				if(player.isBot()) {					
					brains[player.getId()] = new Brain(getPersonalityTraitsFor(player), getStrategyFor(player), world, player);
				}	
			}
		});
		
		
		this.watcher.startWatching();
	}
	
	
	/**
	 * Initialize the scripting engine
	 */
	private void initScriptingEngine() {
		try {									
			AILeolaLibrary aiLib = new AILeolaLibrary(this);
			this.runtime.loadLibrary(aiLib, "ai");			

			this.runtime.eval(new File("./assets/ai/goals.leola"));
			
			this.goals = aiLib.getActionFactory();			
		}
		catch(Exception e) {
			Cons.println("Unable to load the Leola runtime : " + e);
		}
	}
	
	/**
	 * @param player
	 * @return the team strategy for a given player 
	 */
	private TeamStrategy getStrategyFor(PlayerInfo player) {
		if(Team.ALLIED_TEAM_ID == player.getTeamId()) {
			return this.alliedAIStrategy;
		}
		else if(Team.AXIS_TEAM_ID == player.getTeamId()) {
			return this.axisAIStrategy;
		}
		
		return null;
	}
	
	private PersonalityTraits getPersonalityTraitsFor(PlayerInfo player) {
		PersonalityTraits traits = null;
		if(player!=null) {
			traits = this.personalities.get(player.getName().toLowerCase());
			
			if(traits==null) {
				traits = this.personalities.get("default");			
			}
		}
		
		return traits==null ? defaultPersonality : traits;
	}
	
	/**
	 * @return the config
	 */
	@Override
	public AIConfig getConfig() {
		return config;
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
	@Override
	public Randomizer getRandomizer() {
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
	
	/**
	 * @return the game
	 */
	public GameInfo getGame() {
		return game;
	}
	
	/**
	 * @return the goals
	 */
	public Actions getGoals() {
		return goals;
	}
	
	/**
     * @return the world
     */
    public World getWorld() {
        return world;
    }
	
	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#destroy()
	 */
	@Override
	public void destroy() {
		this.watcher.stopWatching();
		
		for(int i = 0; i < this.brains.length; i++) {
			this.brains[i] = null;
		}				
	}

	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#playerJoined(seventh.game.Player)
	 */
	@Override
	public void playerJoined(PlayerInfo player) {
		if(player.isBot()) {
			this.brains[player.getId()] = new Brain(getPersonalityTraitsFor(player), getStrategyFor(player), world, player);
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
		TeamStrategy teamStrategy = getStrategyFor(player);
		
		Brain brain = getBrain(player);
		if(brain != null) {			
			brain.spawned(teamStrategy);
		}
		
		
		if(teamStrategy != null) {
			teamStrategy.playerSpawned(player);
		}		
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
		TeamStrategy teamStrategy = getStrategyFor(player);
		if(teamStrategy != null) {
			teamStrategy.playerKilled(player);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.stats.update(timeStep);
		
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

	
	/**
	 * Draws the {@link Zones}
	 */
	@SuppressWarnings("unused")
	private void debugDrawZones() {
		Zone[] zones = this.stats.getTop5DeadliesZones();
		for(int i = 0; i < zones.length; i++) {
			Zone z = zones[i];
			if(z!=null) {
				Rectangle bounds = z.getBounds();
				DebugDraw.drawStringRelative(z.getId()+"", bounds.x, bounds.y, 0xff00ffff);
				DebugDraw.fillRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0x1fff00ff);
				
				
				DebugDraw.drawString(i + ":" + z.getId()+"", 10, 40 + (i*20), 0xff00ffff);
			}
		}
		/*
		Zone[][] zs = zones.getZones();
		for(int y = 0; y < zs.length; y++) {
			for(int x = 0; x < zs[y].length; x++) {
				Zone z = zs[y][x];
				Rectangle bounds = z.getBounds();
				DebugDraw.drawStringRelative(z.getId()+"", bounds.x, bounds.y, 0xff00ffff);
				DebugDraw.drawRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0xffff00ff);
			}
		}
		
		
		for(int i = 0; i < brains.length; i++) {
			Brain brain = brains[i];
			if(brain != null) {
				if(brain.getPlayer().isAlive()) {
					World world = brain.getWorld();
					
					Zone currentZone = zones.getZone(brain.getEntityOwner().getCenterPos());
					final int range = 88;
					Zone[] adjacent = world.findAdjacentZones(currentZone, range);
					
					int cx = currentZone.getBounds().x + currentZone.getBounds().width / 2;
					int cy = currentZone.getBounds().y + currentZone.getBounds().height / 2;
					
					DebugDraw.drawLineRelative(new Vector2f(cx, cy), new Vector2f(cx      , cy+range), 0x5ffff0ff);
					DebugDraw.drawLineRelative(new Vector2f(cx, cy), new Vector2f(cx      , cy-range), 0x5ffff0ff);
					DebugDraw.drawLineRelative(new Vector2f(cx, cy), new Vector2f(cx+range, cy   ), 0x5ffff0ff);
					DebugDraw.drawLineRelative(new Vector2f(cx, cy), new Vector2f(cx-range, cy   ), 0x5ffff0ff);
					
					//Zone[] adjacent = zones.getAdjacentZones(zones.getZone(brain.getEntityOwner().getCenterPos()));
					for(Zone zone : adjacent) {
						if(zone != null) {
							Rectangle bounds = zone.getBounds();
							DebugDraw.fillRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0x5ffff0ff);
						}
					}
				}
			}
		}*/
	}
	
	/**
	 * Helpful debug for AI
	 */
	@SuppressWarnings("unused")
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
	
	/* (non-Javadoc)
	 * @see seventh.ai.AISystem#receiveAICommand(seventh.game.PlayerInfo, seventh.ai.basic.commands.AICommand)
	 */
	@Override
	public void receiveAICommand(PlayerInfo forBot, AICommand command) {
		if(forBot.isBot() && forBot.isAlive()) {
			Brain brain = getBrain(forBot);
			if(brain != null) {
				Action action = this.aiCommands.compile(brain, command);
				if(action != null) {
					brain.getCommunicator().makeTopPriority(action);
				}
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("brains", this.brains)
		  .add("stats", this.stats)
		  .add("allied_strategy", this.alliedAIStrategy)
		  .add("axis_strategy", this.axisAIStrategy);
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
