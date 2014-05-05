/*
 * see license.txt 
 */
package seventh.server;

import harenet.api.Server;
import harenet.api.impl.HareNetServer;
import harenet.messages.NetMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import leola.vm.Leola;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoObject;
import seventh.game.Game;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.PlayerInfos;
import seventh.game.PlayerInfos.PlayerInfoIterator;
import seventh.game.Players;
import seventh.game.Team;
import seventh.game.net.NetGameStats;
import seventh.game.type.GameType;
import seventh.shared.Command;
import seventh.shared.CommonCommands;
import seventh.shared.Config;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.RconHash;
import seventh.shared.State;
import seventh.shared.StateMachine;
import seventh.shared.StateMachine.StateMachineListener;
import seventh.shared.TimeStep;



/**
 * @author Tony
 *
 */
public class GameServer {
	
	public static final String VERSION = "0.1.0.1-Beta";
	
	/**
	 * Invalid rcon token
	 */
	public static final long INVALID_TOKEN = -1;
	
	private Random random;
	
	/**
	 * Is the server running?
	 */
	private boolean isRunning;
	
	private Server server;
	private int port;
	private StateMachine<State> sm;
	private Leola runtime;
	
	private ServerProtocolListener protocolListener;
	
	private MapCycle mapCycle;
	private String serverName;
	
	private Config config;
	private String currentMap;
	
	private Console console;
	private final boolean isLocal;
	
	private String rconpassword;
	
	private MasterServerRegistration registration;	
	
	/**
	 * A callback for when the server is loaded and is about to start
	 * the game.
	 * 
	 * @author Tony
	 *
	 */
	public static interface OnServerReadyListener {
		
		public void onServerReady(GameServer server);
	}
	
	/**
	 * Server Game Settings
	 * @author Tony
	 *
	 */
	public static class GameServerSettings {
		public String currentMap;
		public int maxScore;
		public int matchTime;
		public int maxPlayers;
		public GameType.Type gameType;
		
		public List<String> alliedTeam;
		public List<String> axisTeam;
	}
	
	private OnServerReadyListener serverListener;
	
	/**
	 * @param console
	 * @param runtime
	 * @param isLocal
	 * @param settings
	 * @throws Exception
	 */
	public GameServer(final Console console, 
					 final Leola runtime, 
					 final boolean isLocal, 
					 final GameServerSettings settings) throws Exception {
		
		this(console, runtime, isLocal);
		
		final AtomicBoolean isReady = new AtomicBoolean(false);
		sm.setListener(new StateMachineListener<State>() {
			/* (non-Javadoc)
			 * @see seventh.shared.StateMachine.StateMachineListener#onEnterState(java.lang.Object)
			 */
			@Override
			public void onEnterState(State state) {

				if(settings!=null && (state instanceof InGameState)) {										
					if(settings.alliedTeam != null) {
						for(String name : settings.alliedTeam) {
							console.execute("add_bot " + name + " allies");
						}
					}
					
					if(settings.axisTeam != null) {
						for(String name : settings.axisTeam) {
							console.execute("add_bot " + name + " axis");
						}
					}
					
					
					/* clear out this listener because we only want
					 * to do this once for a game load!
					 */
					sm.setListener(null);
					isReady.set(true);
				}
			}
			
			@Override
			public void onExitState(State state) {				
			}
		});
		
		
		setGameType(settings.gameType);
		setMatchTime(settings.matchTime);
		setMaxScore(settings.maxScore);
		
		if(settings.currentMap == null) {
			settings.currentMap = mapCycle.getCurrentMap();
		}
		else {
			mapCycle.setCurrentMap(settings.currentMap);
		}
		
		
		changeMap(settings.currentMap);
		
		/* little hack to wait until we are done loading the 
		 * game
		 */
		long timeout = 10_000;
		while(!isReady.get() && timeout > 0) {
			Thread.sleep(500);
			timeout -= 500;
		}		
	}
	
	/**
	 * @param console
	 * @param runtime
	 * @param isLocal
	 * @throws Exception
	 */
	public GameServer(Console console, Leola runtime, boolean isLocal) throws Exception  {
		this.console = console;
		this.runtime = runtime;		
		this.isLocal = isLocal;
				
		this.random = new Random();
		this.config = new Config("./seventh/server_config.leola", "server_config", runtime);
		try {
			this.serverName = this.config.setIfNull("name", "Seventh Server").toString();
			if(this.config.has("map_list")) {
				LeoArray mapList = this.config.get("map_list").as();
				List<String> maps = new ArrayList<String>(mapList.size());
				for(LeoObject m : mapList) {
					maps.add(m.toString());
				}
				this.mapCycle = new MapCycle(maps);
			}
		}
		catch(Exception e) {
			console.println("*** Unable to parse configuration file: " + config + " because of: " + e);
			
		}
						
		this.config.setIfNull("sv_maxscore", Leola.toLeoObject(50));
		this.config.setIfNull("sv_matchtime", Leola.toLeoObject(20));
		this.config.setIfNull("sv_gametype", Leola.toLeoObject("tdm"));
		
		this.rconpassword = this.config.getString("rcon_password");
		
		if(this.mapCycle==null) {
			this.mapCycle = new MapCycle(Arrays.asList("./seventh/maps/tdm_0.json"));
		}
		
		this.server = new HareNetServer(this.config.getNetConfig());

		this.protocolListener = new ServerProtocolListener(this);
		this.server.addConnectionListener(this.protocolListener);
//		this.server.addConnectionListener(new LagConnectionListener(125, 205, protocolListener));
		
		this.serverName = "The Seventh Server";
		
		// load some helper functions for objective scripts
		this.runtime.loadStatics(LeolaScriptLibrary.class);
		
		setupServerCommands(console);
		
		this.sm = new StateMachine<State>();
		changeMap(this.mapCycle.getCurrentMap());
		
		this.registration = new MasterServerRegistration(this);
		this.registration.start();
	}


	private void changeMap(String map) {
		if(!map.toLowerCase().endsWith(".json")) {
			map += ".json";
		}
		
		this.currentMap = map;
		this.sm.changeState(new LoadingState(this, map));
	}
	
	private void setupServerCommands(Console console) {
		CommonCommands.addCommonCommands(console);
		
		console.addCommand(this.mapCycle.getMapListCommand());
		console.addCommand(this.mapCycle.getMapAddCommand()); 
		console.addCommand(this.mapCycle.getMapRemoveCommand());
				
		console.addCommand(new Command("map") {
			
			@Override
			public void execute(Console console, String... args) {				
				changeMap(mergeArgsDelim(" ", args));
			}
		});
		
		console.addCommand(new Command("map_next") {
			
			@Override
			public void execute(Console console, String... args) {				
				changeMap(mapCycle.getNextMap());
			}
		});
				
		console.addCommand(new Command("map_restart") {
			
			@Override
			public void execute(Console console, String... args) {				
				changeMap(currentMap);
			}
		});		
		
		console.addCommand(new Command("add_bot"){			
			@Override
			public void execute(Console console, String... args) {
				Game game = protocolListener.getGame();
				if(game != null) {
					if( args.length < 1) {
						console.println("<usage> add_bot [bot name] [optional team]");
					}
					else {
						int id = server.reserveId();
						if(id >= 0) {
							game.addBot(id, args[0]);
						}
						
						if(args.length > 1) {							
							String team = args[1].trim().toLowerCase();
							if(team.startsWith("allies")) {
								game.playerSwitchedTeam(id, Team.ALLIED_TEAM);
							}
							else if(team.startsWith("axis")) {
								game.playerSwitchedTeam(id, Team.AXIS_TEAM);
							}
						}
						console.println("Added Bot...ID: " + id);
					}
				}
				else {
					console.println("The game has not properly been setup yet!");
				}
			}
		});
		
		
		console.addCommand(new Command("add_dummy_bot"){			
			@Override
			public void execute(Console console, String... args) {
				Game game = protocolListener.getGame();
				if(game != null) {
					int id = server.reserveId();
					if(id >= 0) {
						game.addDummyBot(id);
					}
					
					console.println("Added Dummy Bot...ID: " + id);
				}
			}
		});
		
		console.addCommand(new Command("kick"){			
			@Override
			public void execute(Console console, String... args) {
				Game game = protocolListener.getGame();
				if(game != null) {
					switch(args.length) {
						case 0 : console.println("*** You must supply a playerId");
							break;
						default: {
							int id = Integer.parseInt(args[0]);
							console.println("Kicking playerId: " + id);
							game.kickPlayer(id);
						}
					}
					
				}
			}
		});
		
		console.addCommand(new Command("kill"){			
			@Override
			public void execute(Console console, String... args) {
				GameInfo game = protocolListener.getGame();
				if(game != null) {
					switch(args.length) {
						case 0 : console.println("*** You must supply a playerId");
							break;
						default: {
							int id = Integer.parseInt(args[0]);
							PlayerInfo player = game.getPlayerById(id);
							if(player!=null&&!player.isDead()) {
								player.getEntity().kill(player.getEntity());
							}
						}
					}
					
				}
			}
		});
		
		console.addCommand(new Command("players"){			
			@Override
			public void execute(final Console console, String... args) {
				GameInfo game = protocolListener.getGame();
				if(game != null) {
					PlayerInfos players = game.getPlayerInfos();
					console.println("Name                       ID           Ping");
					console.println("============================================");
					players.forEachPlayerInfo(new PlayerInfoIterator() {
						
						@Override
						public void onPlayerInfo(PlayerInfo p) {
							console.printf("%-25s %-16d %-4d\n", p.getName(), p.getId(), p.getPing());	
						}
					});
					
					console.println("\n");		
				}
			}
		});
		
		console.addCommand(new Command("stats"){			
			@Override
			public void execute(final Console console, String... args) {
				Game game = protocolListener.getGame();
				if(game != null) {
					final DateFormat format = new SimpleDateFormat("HH:mm:ss");
					format.setTimeZone(TimeZone.getTimeZone("GMT"));
					
					console.println("\n");
					
					Players players = game.getPlayers();
					console.println("Name                       Kills      Deaths      Joined          Team");
					console.println("======================================================================");
					players.forEachPlayerInfo(new PlayerInfoIterator() {
						
						@Override
						public void onPlayerInfo(PlayerInfo p) {
							console.printf("%-25s %6d %11d %12s %12s\n", p.getName(), p.getKills(), p.getDeaths()
											, format.format(new Date(p.getJoinTime())), p.getTeam().getName());	
						}
					});
					
					
					console.println("\n");
					
					console.println("\tTeam            ID      Score");
					console.println("\t=============================");
					
					NetGameStats stats = game.getNetGameStats();
					if(stats.teamStats != null) {
						for(int i = 0; i < stats.teamStats.length; i++) {
							console.printf("\t%-15s %2d %10d\n", Team.getName(stats.teamStats[i].id)
															 , stats.teamStats[i].id
															 , stats.teamStats[i].score );	
						}
					}
					console.println("\n");
				}
			}
		});
		
		console.addCommand(new Command("sv_exit"){			
			@Override
			public void execute(Console console, String... args) {
				console.println("Shutting down the system...");
				shutdown();
				console.println("Shutdown complete!");
				System.exit(0);
			}
		});
		
		if(console.getCommand("exit") == null) {
			console.addCommand("exit", console.getCommand("sv_exit"));
			console.addCommand("quit", console.getCommand("sv_exit"));
		}
				
		console.addCommand(new Command("set"){			
			@Override
			public void execute(Console console, String... args) {
				switch(args.length) {
					case 0: console.println("<Usage> set [variable name] [value]");
						break;
					case 1: console.println(args[0] + " = " + config.get(args[0]));
						break;
					default: {
						config.set( mergeArgsDelimAt(" ", 1, args), args[0]);
					}
					
				}
			}
		});
		
		console.addCommand(new Command("seti"){			
			@Override
			public void execute(Console console, String... args) {
				switch(args.length) {
					case 0: console.println("<Usage> seti [variable name] [value]");
						break;
					case 1: console.println(args[0] + " = " + config.get(args[0]));
						break;
					default: {
						try {
							config.set(Integer.parseInt(args[1]), args[0]);						
						}
						catch(Exception e) {
							console.println("Illegal input, must be an integer value");
						}
					}
					
				}
			}
		});
	}
	
	/**
	 * @return the config
	 */
	public Config getConfig() {
		return config;
	}
	
	/**
	 * @return the console
	 */
	public Console getConsole() {
		return Cons.getImpl();
	}
	
	/**
	 * @return the serverListener
	 */
	public OnServerReadyListener getServerListener() {
		return serverListener;
	}
	
	/**
	 * @param serverListener the serverListener to set
	 */
	public void setServerListener(OnServerReadyListener serverListener) {
		this.serverListener = serverListener;
	}
	
	public void queueSendToAll(int flags, NetMessage message) {
		this.protocolListener.queueSendToAll(flags,message);
	}
	public void queueSendToAllExcept(int flags, NetMessage message, int id) {
		this.protocolListener.queueSendToAllExcept(flags, message, id);
	}
	public void queueSendToClient(int flags, NetMessage message, int id) {
		this.protocolListener.queueSendToClient(flags, message, id);
	}
	
	/**
	 * @return a security token
	 */
	public long createToken() {
		
		long token = INVALID_TOKEN;
		while(token == INVALID_TOKEN) {
			token =	this.random.nextLong();
		}
		return token;
	}
	
	/**
	 * @return the match time -- the max amount of time a match should last
	 */
	public long getMatchTime() {
		return this.config.getInt("sv_matchtime") * 60 * 1000L;
	}
	
	public void setMatchTime(int matchTime) {
		this.config.set(matchTime, "sv_matchtime");
	}
	
	/**
	 * @return the max score needed to win
	 */
	public int getMaxScore() {
		return this.config.getInt("sv_maxscore");
	}
	
	public void setMaxScore(int maxscore) {
		this.config.set(maxscore, "sv_maxscore");
	}
	
	/**
	 * @return the game type
	 */
	public GameType.Type getGameType() {
		return GameType.Type.toType(this.config.getString("sv_gametype"));
	}
	
	public void setGameType(GameType.Type gameType) {
		this.config.set(gameType.name(), "sv_gametype");
	}
	
	/**
	 * @return the mapCycle
	 */
	public MapCycle getMapCycle() {
		return mapCycle;
	}
	
	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}
	
	/**
	 * @return the runtime
	 */
	public Leola getRuntime() {
		return runtime;
	}
	
	/**
	 * @return the server
	 */
	public Server getServer() {
		return server;
	}
	
	/**
	 * @return the sm
	 */
	public StateMachine<State> getSm() {
		return sm;
	}
	
	/**
	 * @return the clients
	 */
	public java.util.Map<Integer, RemoteClient> getClients() {
		return this.protocolListener.getClients();
	}
	
	/**
	 * @return the protocolListener
	 */
	public ServerProtocolListener getProtocolListener() {
		return protocolListener;
	}
	
	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * @return the isLocal
	 */
	public boolean isLocal() {
		return isLocal;
	}
	
	/**
	 * @param token
	 * @return the hashed rcon password
	 */
	public String getRconPassword(long token) {
		RconHash hash = new RconHash(token);
		return hash.hash(this.rconpassword);
	}
	
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Starts the server listening on the supplied port
	 * @param port
	 * @throws Exception
	 */
	public void start(int port) throws Exception {
		if(this.isRunning) {
			throw new IllegalStateException("The server is already running");
		}
		this.port = port;
		isRunning = true;
		
		Cons.println("*** Listening on port: " + port + " ***");			
		
		/* start listening on the supplied port */		
		server.bind(port);
		server.start();
					
		try {
			long currentTime = System.currentTimeMillis();
			long accumalator = 0;

			long gameClock = 0;
			
			final int maxIterations = 5;
			final long maxDelta = 250;
			final long dt = 1000 / config.getInt(20, "sv_framerate");

			final TimeStep timeStep = new TimeStep();
			
			if(serverListener != null) {
				serverListener.onServerReady(this);
			}
			
			while(this.isRunning) {							
				long newTime = System.currentTimeMillis();
				long deltaTime = newTime - currentTime;

				if(deltaTime > maxDelta) {
					deltaTime = maxDelta;
				}
								
				if ( deltaTime >= dt ) {
					currentTime = newTime;

					accumalator += deltaTime;
					int iteration = 0;
					while( accumalator >= dt && iteration < maxIterations) {
						timeStep.setDeltaTime(dt);
						timeStep.setGameClock(gameClock);						
						serverFrame(timeStep);						

						gameClock += dt;
						accumalator -= dt;
						iteration++;
					}
				}						
			}
		}
		catch(Exception e) {
			Cons.println("*** An error occured in the main server game loop: " + e);
		}
		finally {
			Cons.println("Shutting down the server...");
			server.stop();
			server.close();
			registration.shutdown();
			Cons.println("Server shutdown completed!");
		}
	}
	
	private void serverFrame(TimeStep timeStep) {	
		if(!this.isLocal) {
			this.console.update(timeStep);
		}
		this.sm.update(timeStep);
	}
	
	/**
	 * Shutdown the server
	 */
	public void shutdown() {
		this.isRunning = false;
	}
	
}
