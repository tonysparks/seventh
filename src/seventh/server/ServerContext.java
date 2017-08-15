/*
 * see license.txt 
 */
package seventh.server;

import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import harenet.NetConfig;
import harenet.api.Server;
import harenet.api.impl.HareNetServer;
import leola.vm.Leola;
import seventh.shared.Console;
import seventh.shared.Debugable.DebugableListener;
import seventh.shared.MapList.MapEntry;
import seventh.shared.RconHash;
import seventh.shared.Scripting;
import seventh.shared.State;
import seventh.shared.StateMachine;

/**
 * Context information for a {@link GameServer}
 * 
 * @author Tony
 *
 */
public class ServerContext {

    /**
     * Invalid rcon token
     */
    public static final long INVALID_RCON_TOKEN = -1;
    
    
    private Server server;
    private GameServer gameServer;
    private RemoteClients clients;
    private ServerNetworkProtocol protocol;

    private Leola runtime;
    private Console console;
    private String rconPassword;

    private MapCycle mapCycle;
    private ServerSeventhConfig config;
    
    private Random random;
    
    private StateMachine<State> stateMachine;
    private AtomicReference<GameSession> gameSession;
    private List<GameSessionListener> gameSessionListeners;
    
    
    private GameSessionListener sessionListener = new GameSessionListener() {
        
        @Override
        public void onGameSessionDestroyed(GameSession session) {
            gameSession.set(null);
            
            for(GameSessionListener l : gameSessionListeners) {
                l.onGameSessionDestroyed(session);
            }
        }
        
        @Override
        public void onGameSessionCreated(GameSession session) {
            gameSession.set(session);
            
            for(GameSessionListener l : gameSessionListeners) {
                l.onGameSessionCreated(session);
            }
        }
    };
    
    /**
     * @param gameServer
     * @param config
     * @param runtime
     */
    public ServerContext(GameServer gameServer, ServerSeventhConfig config, Leola runtime, Console console) {
        this.gameServer = gameServer;
        this.config = config;
        this.runtime = runtime;
        this.console = console;
                                
        this.rconPassword = config.getRconPassword();
        
        this.random = new Random();
        this.clients = new RemoteClients(config.getMaxPlayers());
        this.stateMachine = new StateMachine<State>();

        NetConfig netConfig = config.getNetConfig();
        netConfig.setMaxConnections(config.getMaxPlayers());
        
        this.server = new HareNetServer(netConfig);
        
        this.protocol = new ServerNetworkProtocol(this);
        this.server.addConnectionListener(this.protocol);
        
        this.gameSessionListeners = new Vector<>();        
        addGameSessionListener(this.protocol);
        
        this.gameSession = new AtomicReference<>();
        this.mapCycle = new MapCycle(config.getMapListings());
    }
    
    /**
     * @return true if there is a debug listener
     */
    public boolean hasDebugListener() {
        return this.gameServer.getDebugListener() != null;
    }
    
    /**
     * @return the {@link DebugableListener}
     */
    public DebugableListener getDebugableListener() {
        return this.gameServer.getDebugListener();
    }
    
    /**
     * Spawns a new GameSession
     * 
     * @param map - the map to load
     */
    public void spawnGameSession(MapEntry map) {                    
        this.mapCycle.setCurrentMap(map);
        
        this.stateMachine.changeState(new LoadingState(this, this.sessionListener, map));
    }
    
    /**
     * Spawns a new Game Session
     */
    public void spawnGameSession() {
        spawnGameSession(mapCycle.getNextMap());
    }
    
    /**
     * @return true if there is a {@link GameSession} loaded
     */
    public boolean hasGameSession() {
        return gameSession.get() != null;
    }
    
    /**
     * @return the gameSession
     */
    public GameSession getGameSession() {
        return gameSession.get();
    }
        
    /**
     * Adds a {@link GameSessionListener} 
     * 
     * @param l
     */
    public void addGameSessionListener(GameSessionListener l) {
        this.gameSessionListeners.add(l);
    }
    
    /**
     * Removes a {@link GameSessionListener}
     * 
     * @param l
     */
    public void removeGameSessionListener(GameSessionListener l) {
        this.gameSessionListeners.remove(l);
    }
    
    
    /**
     * Creates a security token for RCON sessions
     * 
     * @return a security token
     */
    public long createToken() {
        
        long token = INVALID_RCON_TOKEN;
        while(token == INVALID_RCON_TOKEN) {
            token =    this.random.nextLong();
        }
        return token;
    }
    
    /**
     * @return the port in which this server is listening on
     */
    public int getPort() {
        return this.gameServer.getPort();
    }
    
    /**
     * @param token
     * @return the hashed rcon password
     */
    public String getRconPassword(long token) {
        RconHash hash = new RconHash(token);
        return hash.hash(this.rconPassword);
    }
    
    /**
     * @return the mapCycle
     */
    public MapCycle getMapCycle() {
        return mapCycle;
    }
    
    /**
     * @return the random
     */
    public Random getRandom() {
        return random;
    }
    
    /**
     * @return the server
     */
    public Server getServer() {
        return server;
    }
    
    /**
     * @return the serverProtocolListener
     */
    public ServerNetworkProtocol getServerProtocol() {
        return protocol;
    }
    
    /**
     * @return the stateMachine
     */
    public StateMachine<State> getStateMachine() {
        return stateMachine;
    }
    
    /**
     * @return the gameServer
     */
    public GameServer getGameServer() {
        return gameServer;
    }


    /**
     * @return the console
     */
    public Console getConsole() {
        return console;
    }


    /**
     * @return the clients
     */
    public RemoteClients getClients() {
        return clients;
    }


    /**
     * @return the runtime
     */
    public Leola getRuntime() {
        return runtime;
    }
    
    /**
     * @return a new instance of the {@link Leola} runtime
     */
    public Leola newRuntime() {
        Leola runtime = Scripting.newRuntime();
        return runtime;
    }


    /**
     * @return the config
     */
    public ServerSeventhConfig getConfig() {
        return config;
    }        
    
    

}
