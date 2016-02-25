/*
 * see license.txt 
 */
package seventh.server;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import harenet.api.Server;
import leola.vm.Leola;
import leola.vm.util.Classpath;
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
import seventh.shared.Debugable.DebugableListener;
import seventh.shared.LANServerRegistration;
import seventh.shared.State;
import seventh.shared.StateMachine;
import seventh.shared.StateMachine.StateMachineListener;
import seventh.shared.TimeStep;



/**
 * The {@link GameServer} handles running the game and listening for clients.
 * 
 * @author Tony
 *
 */
public class GameServer {
	
	public static final String VERSION = "0.1.0.1-Beta";
	
	
	/**
	 * Is the server running?
	 */
	private boolean isRunning;		
	private int port;
	
	private Console console;
	private final boolean isLocal;
		
	private ServerContext serverContext;
	
	private MasterServerRegistration registration;
	private LANServerRegistration lanRegistration;
	private OnServerReadyListener serverListener;
	private DebugableListener debugListener;
	
	/**
	 * A callback for when the server is loaded and is about to start
	 * the game.
	 * 
	 * @author Tony
	 *
	 */
	public static interface OnServerReadyListener {
		
		/**
		 * The server is ready for remote clients to connect.
		 * 
		 * @param server
		 */
		public void onServerReady(GameServer server);
	}
	
	/**
	 * Server Game Settings
	 * 
	 * @author Tony
	 *
	 */
	public static class GameServerSettings {
		public String serverName;
		public String currentMap;
		public String startupScript;
		public String password;
		public int maxScore;		
		public int maxPlayers;
		public long matchTime;
		public GameType.Type gameType;
		
		public List<String> alliedTeam;
		public List<String> axisTeam;
		public boolean isDedicatedServer;		
		public boolean isLAN;
		public boolean isPrivate;
		public int port;
	}
	
	
	/**
	 * @param config
	 * @param console
	 * @param runtime
	 * @param isLocal
	 * @param settings
	 * 
	 * @throws Exception
	 */
	public GameServer(ServerSeventhConfig config,
					  Console console, 
					  Leola runtime, 
					  boolean isLocal, 
					  GameServerSettings settings) throws Exception {
		
		this.console = console;
		this.isLocal = isLocal;
		
		/* if no settings are supplied, use
		 * the default configured settings
		 */
		if(settings == null) {
			settings = new GameServerSettings();
			settings.startupScript = config.getStartupScript();
			settings.serverName = config.getServerName();
			settings.gameType = config.getGameType();
			settings.matchTime = config.getMatchTime();
			settings.maxScore = config.getMaxScore();
			settings.maxPlayers = config.getMaxPlayers();
			settings.port = config.getPort();
			
			settings.isDedicatedServer = true;
			settings.isLAN = false;
		}
		
		
		init(config, runtime, settings);
				
	}
	

	/**
	 * @param console
	 * @param runtime
	 * @param isLocal
	 * @param settings
	 * @throws Exception
	 */
	public GameServer(Console console, 
					  Leola runtime, 
					  boolean isLocal, 
					  GameServerSettings settings) throws Exception  {
		
		this(new ServerSeventhConfig(new Config("./seventh/server_config.leola", "server_config", runtime)), 
				console, 
				runtime, 
				isLocal, 
				settings);		
	}
	
	/**
	 * Defaults to a dedicated server in which uses the configured
	 * default settings
	 * 
	 * @param config
	 * @param console
	 * @param runtime
	 * @throws Exception
	 */
	public GameServer(ServerSeventhConfig config, Console console, Leola runtime) throws Exception  {
		this(config, console, runtime, false, null);		
	}
	

	/**
	 * Initializes the {@link GameServer}
	 * 
	 * @param settings
	 * @throws Exception
	 */
	private void init(final ServerSeventhConfig config, 
					  final Leola runtime, 
					  final GameServerSettings settings) throws Exception {
		
		this.serverContext = new ServerContext(this, config, runtime, this.console);
		
						
		/* load some helper functions for objective scripts */
		runtime.loadStatics(SeventhScriptingCommonLibrary.class);
		runtime.put("console", this.console);
			
		
		/* if this is a dedicated server, we'll contact the 
		 * master server so that users know about this server
		 */
		this.registration = new MasterServerRegistration(this.serverContext);
		if(settings.isDedicatedServer) {
			this.registration.start();
		}
		
		this.lanRegistration = new LANServerRegistration(this.serverContext);
		if(settings.isLAN) {
			this.lanRegistration.start();
		}

		/* attempt to attach a debugger */
		if(config.isDebuggerEnabled()) {
			DebugableListener debugableListener = createDebugListener(config);
			if(debugableListener != null) {
				setDebugListener(debugableListener);
			}
		}
		
		/*
		 * Load up the bots
		 */
		final AtomicBoolean isReady = new AtomicBoolean(false);
		this.serverContext.getStateMachine().setListener(new StateMachineListener<State>() {			
			@Override
			public void onEnterState(State state) {
				
				/* only listen for the first in game state
				 * transition because we only need to 'wait'
				 * for the server to be up the first time in.
				 * 
				 * This addresses the Single Player bug of the
				 * Bots not spawning
				 */
				if( (state instanceof InGameState) ) {
					if(settings.startupScript != null) {
						console.execute("run", settings.startupScript);
					}
					
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
					serverContext.getStateMachine().setListener(null);
					isReady.set(true);					
				}
			}
			
			@Override
			public void onExitState(State state) {				
			}
		});
		
				
		setupServerCommands(console);		
		
		config.setServerName(settings.serverName);
		config.setGameType(settings.gameType);
		config.setMatchTime(settings.matchTime);
		config.setMaxScore(settings.maxScore);
		
		MapCycle mapCycle = serverContext.getMapCycle();
		if(settings.currentMap == null) {
			settings.currentMap = mapCycle.getCurrentMap();
		}		
		mapCycle.setCurrentMap(settings.currentMap);
		
		/* load up the map */
		serverContext.spawnGameSession(settings.currentMap);	
	}
	
	
	/**
	 * Attempt to create a {@link DebugableListener} from the supplied configuration
	 * 
	 * @param config
	 * @return the {@link DebugableListener} if one is available, or null
	 */
	private DebugableListener createDebugListener(ServerSeventhConfig config) {
		try {
			String className = config.getDebuggerClassName();
			if(className != null && !"".equals(className)) {
				
				/* add jars to the class path if needed */
				String classpath = config.getDebuggerClasspath();
				if(classpath != null && !"".equals(classpath)) {
					Classpath.loadJars(classpath);
				}
				
				Class<?> aClass = Class.forName(className);
				DebugableListener listener = (DebugableListener)aClass.newInstance();
				if(listener != null) {
					listener.init(config);
				}
				
				return listener;
			}
		}
		catch(Throwable t) {
			Cons.println("Unable to load the debugger: " + t);
		}
		
		return null;
	}
	
	/**
	 * Setups server side console commands
	 * 
	 * @param console
	 */
	private void setupServerCommands(Console console) {
		CommonCommands.addCommonCommands(console);
		
		final ServerSeventhConfig config = serverContext.getConfig();
		final MapCycle mapCycle = serverContext.getMapCycle();
		console.addCommand(mapCycle.getMapListCommand());
		console.addCommand(mapCycle.getMapAddCommand()); 
		console.addCommand(mapCycle.getMapRemoveCommand());
				
		console.addCommand(new Command("map") {
			
			@Override
			public void execute(Console console, String... args) {				
				serverContext.spawnGameSession(mergeArgsDelim(" ", args));
			}
		});
		
		console.addCommand(new Command("map_next") {
			
			@Override
			public void execute(Console console, String... args) {				
				serverContext.spawnGameSession(mapCycle.getNextMap());
			}
		});
				
		console.addCommand(new Command("map_restart") {
			
			@Override
			public void execute(Console console, String... args) {				
				serverContext.spawnGameSession(serverContext.getMapCycle().getCurrentMap());
			}
		});		
		
		console.addCommand(new Command("run") {
			
			@Override
			public void execute(Console console, String... args) {
				
				try { 
					serverContext.getRuntime().eval(new File(args[0]));
				}
				catch(Exception e) {
					Cons.println("*** ERROR: Error running server script '" + mergeArgsDelim(" ", args) + "': " + e);
				}
				
			}
		});
		
		console.addCommand(new Command("sv_privatePassword") {
            
            @Override
            public void execute(Console console, String... args) {
                ServerSeventhConfig config = serverContext.getConfig();
                if(args==null||args.length < 1) {
                    console.println("sv_privatePassword: " + config.getPrivatePassword());
                }
                else {
                    config.setPrivatePassword(args[0]);
                }
            }
        });
		
		console.addCommand(new Command("add_bot"){			
			@Override
			public void execute(Console console, String... args) {
				Game game = serverContext.getGameSession().getGame();
				if(game != null) {
					if( args.length < 1) {
						console.println("<usage> add_bot [bot name] [optional team]");
					}
					else {
						int id = serverContext.getServer().reserveId();
						if(id >= 0) {
							game.addBot(id, args[0]);
						}
						
						if(args.length > 1) {							
							String team = args[1].trim().toLowerCase();
							if(team.startsWith(Team.ALLIED_TEAM_NAME.toLowerCase())) {
								game.playerSwitchedTeam(id, Team.ALLIED_TEAM_ID);
							}
							else if(team.startsWith(Team.AXIS_TEAM_NAME.toLowerCase())) {
								game.playerSwitchedTeam(id, Team.AXIS_TEAM_ID);
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
				Game game = serverContext.getGameSession().getGame();
				if(game != null) {
					int id = serverContext.getServer().reserveId();
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
				Game game = serverContext.getGameSession().getGame();
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
				GameInfo game = serverContext.getGameSession().getGame();
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
				GameInfo game = serverContext.getGameSession().getGame();
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
				Game game = serverContext.getGameSession().getGame();
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
		
		console.addCommand(new Command("get"){			
			@Override
			public void execute(Console console, String... args) {
				switch(args.length) {
					case 0: console.println("<Usage> get [variable name]");
						break;
					default: 
						console.println(args[0] + " = " + config.getConfig().get(args[0]));
						break;					
				}
			}
		});
				
		console.addCommand(new Command("set"){			
			@Override
			public void execute(Console console, String... args) {
				switch(args.length) {
					case 0: console.println("<Usage> set [variable name] [value]");
						break;
					case 1: console.println(args[0] + " = " + config.getConfig().get(args[0]));
						break;
					default: {
						config.getConfig().set( mergeArgsDelimAt(" ", 1, args), args[0]);
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
					case 1: console.println(args[0] + " = " + config.getConfig().get(args[0]));
						break;
					default: {
						try {
							config.getConfig().set(Integer.parseInt(args[1]), args[0]);						
						}
						catch(Exception e) {
							console.println("Illegal input, must be an integer value");
						}
					}
					
				}
			}
		});
		
		console.addCommand(new Command("new_tank") {			
			@Override
			public void execute(Console console, String... args) {
				if(getServerContext().hasGameSession()) {
					GameSession session = getServerContext().getGameSession();
					Game game = session.getGame();
					if (game != null) {
						switch(args.length) {						
							case 2:
								try {
									int x = Integer.parseInt(args[0]);
									int y = Integer.parseInt(args[1]);
									game.newShermanTank(x, y);
								}
								catch(Exception e) {
									console.println("Unable to create tank: " + e);
								}
								break;
							default: {
								console.println("<usage> new_tank [x coordinate] [y coordinate]");
							}
						}
						
					}
				}
			}
		});
		
		console.addCommand(new Command("gametype") {
			
			@Override
			public void execute(Console console, String... args) {
				if(getServerContext().hasGameSession()) {
					GameSession session = getServerContext().getGameSession();
					
					Game game = session.getGame();
					if (game != null) {
						switch(args.length) {	
							case 0:
								console.println("<usage> gametype [obj|tdm] [max score] [time]");
								break;
							case 1:
								console.execute("set sv_gametype " + args[0]);
								break;
							case 2:
								console.execute("set sv_gametype " + args[0]);
								console.execute("set sv_maxscore " + args[1]);
								break;
							case 3:
								console.execute("set sv_gametype " + args[0]);
								console.execute("seti sv_maxscore " + args[1]);
								console.execute("seti sv_matchtime " + args[2]);
								break;								
							default: {
								console.println("<usage> new_tank [x coordinate] [y coordinate]");
							}
						}
						
					}
				}				
			}
		});
	}
	
	/**
	 * @return the serverContext
	 */
	public ServerContext getServerContext() {
		return serverContext;
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
	
	/**
	 * @param debugListener the debugListener to set
	 */
	public void setDebugListener(DebugableListener debugListener) {
		this.debugListener = debugListener;
	}
	
	/**
	 * @return the debugListener
	 */
	public DebugableListener getDebugListener() {
		return debugListener;
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
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Starts the server listening on the supplied port
	 * 
	 * @param port
	 * @throws Exception
	 */
	public void start(int port) throws Exception {
		if(this.isRunning) {
			throw new IllegalStateException("The server is already running");
		}
		
		this.port = port;
		this.isRunning = true;
		
		Cons.println("*** Launching GameServer v" + VERSION + " ***");			
		
		Server server = this.serverContext.getServer();
		StateMachine<State> sm = this.serverContext.getStateMachine();
		
		/* start listening on the supplied port */		
		server.bind(port);
		server.start();
		
		Cons.println("*** Listening on port: " + port + " ***");
		
		try {
			long currentTime = System.currentTimeMillis();
			long accumalator = 0;

			long gameClock = 0;
			
			final int maxIterations = 5;
			final long maxDelta = 250;
			final long dt = 1000 / serverContext.getConfig().getServerFrameRate();

			final TimeStep timeStep = new TimeStep();
			
			if(this.serverListener != null) {
				this.serverListener.onServerReady(this);
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
						serverFrame(sm, timeStep);						

						gameClock += dt;
						accumalator -= dt;
						iteration++;
					}
				}						
			}
		}
		catch(Exception e) {
			Cons.println("*** An error occured in the main server game loop: " + e);
			Cons.println("*** Stack trace: " + Arrays.toString(e.getStackTrace()));
		}
		finally {
			Cons.println("Shutting down the server...");
			server.stop();
			server.close();
			this.registration.shutdown();
			this.lanRegistration.shutdown();
			
			if(this.debugListener != null) {
				this.debugListener.shutdown();
			}
			
			Cons.println("Server shutdown completed!");
		}
	}
	
	
	/**
	 * Executes a server frame
	 * 
	 * @param timeStep
	 */
	private void serverFrame(StateMachine<State> sm, TimeStep timeStep) {	
		if(!this.isLocal) {
			this.console.update(timeStep);
		}
		
		sm.update(timeStep);
	}
	
	/**
	 * Shutdown the server
	 */
	public void shutdown() {
		this.isRunning = false;
	}
	
}
