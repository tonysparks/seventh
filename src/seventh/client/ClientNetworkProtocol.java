/*
 * see license.txt 
 */
package seventh.client;

import harenet.api.Client;
import harenet.api.Connection;
import harenet.api.Endpoint;
import harenet.messages.NetMessage;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import seventh.client.screens.InGameScreen;
import seventh.game.net.NetGameState;
import seventh.game.net.NetMap;
import seventh.map.Map;
import seventh.map.MapLoaderUtil;
import seventh.network.messages.AICommandMessage;
import seventh.network.messages.BombDisarmedMessage;
import seventh.network.messages.BombExplodedMessage;
import seventh.network.messages.BombPlantedMessage;
import seventh.network.messages.ClientReadyMessage;
import seventh.network.messages.ConnectAcceptedMessage;
import seventh.network.messages.ConnectRequestMessage;
import seventh.network.messages.FlagCapturedMessage;
import seventh.network.messages.FlagReturnedMessage;
import seventh.network.messages.FlagStolenMessage;
import seventh.network.messages.GameEndedMessage;
import seventh.network.messages.GamePartialStatsMessage;
import seventh.network.messages.GameReadyMessage;
import seventh.network.messages.GameStatsMessage;
import seventh.network.messages.GameUpdateMessage;
import seventh.network.messages.PlayerConnectedMessage;
import seventh.network.messages.PlayerDisconnectedMessage;
import seventh.network.messages.PlayerInputMessage;
import seventh.network.messages.PlayerKilledMessage;
import seventh.network.messages.PlayerNameChangeMessage;
import seventh.network.messages.PlayerSpawnedMessage;
import seventh.network.messages.PlayerSpeechMessage;
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.PlayerSwitchWeaponClassMessage;
import seventh.network.messages.RconMessage;
import seventh.network.messages.RconTokenMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.network.messages.TileRemovedMessage;
import seventh.network.messages.TilesRemovedMessage;
import seventh.shared.Cons;
import seventh.shared.NetworkProtocol;
import seventh.shared.Scripting;
import seventh.shared.SeventhConstants;


/**
 * Implements the {@link ClientProtocol} and is responsible for buffering the messaging traffic
 * 
 * @author Tony
 *
 */
public class ClientNetworkProtocol extends NetworkProtocol implements ClientProtocol {
    
    /**
     * A Queued network Message
     * 
     * @author Tony
     *
     */
	private static class QueuedMessage {
		int flags;
		NetMessage msg;
		
		public QueuedMessage(int flags, NetMessage msg) {
			this.flags = flags;
			this.msg = msg;
		}
	}
	
    
    private SeventhGame app;
    private ClientGame game;
    private GameCreationListener gameCreationListener;

    private Client client;
    private ClientConnection connection;
    
    private LocalSession localPlayer;
    private ClientPlayers players;
    
    private Queue<QueuedMessage> outboundQ;
	
		
	/**
	 * @param connection
	 * @param app
	 */
	public ClientNetworkProtocol(ClientConnection connection, SeventhGame app) {
		super(connection.getClient());
		this.client = connection.getClient();				
		this.outboundQ = new ConcurrentLinkedQueue<QueuedMessage>();
		
	    this.app = app;
	    this.connection = connection;	        
	        
	    this.localPlayer = new LocalSession();
	    this.players = new ClientPlayers(SeventhConstants.MAX_PLAYERS);
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.shared.NetworkProtocol#postQueuedMessages()
	 */
	@Override
	public void postQueuedMessages() {
		while(!this.outboundQ.isEmpty()) {

		    QueuedMessage msg = this.outboundQ.poll();
			try {
				this.client.send(msg.flags, msg.msg);
			} catch (IOException e) {
				Cons.println("*** Failed to send msg - " + e);
			}
		}
	}
	

    /**
     * Queues a reliable {@link NetMessage} for the next available network update.
     * @param msg
     */
	private void queueSendReliableMessage(NetMessage msg) {
	    queueSendMessage(Endpoint.FLAG_RELIABLE, msg);
    }	
    
    /**
     * Sends the reliable {@link NetMessage} immediately
     * 
     * @param message
     * @throws IOException
     */
    private void sendReliableMessage(NetMessage message) {
        
        try {
            sendMessage(Endpoint.FLAG_RELIABLE, message);
        } 
        catch (IOException e) {
            Cons.println("*** Error sending reliable packet - " + e);
        }
    }
	
    /**
     * Sends the unreliable {@link NetMessage} immediately.
     * 
     * @param message
     * @throws IOException
     */
    private void sendUnReliableMessage(NetMessage message) {
        try {
            sendMessage(Endpoint.FLAG_UNRELIABLE, message);
        }
        catch(IOException e) {
            Cons.println("*** Error sending unreliable packet - " + e);
        }
    }
	
	/**
	 * @param msg
	 */
	private void queueSendMessage(int flags, NetMessage msg) {
		this.outboundQ.add(new QueuedMessage(flags, msg));
	}
	
	public void sendMessage(int flags, NetMessage msg) throws IOException {
		this.client.send(flags, msg);
	}
		
	/* (non-Javadoc)
	 * @see net.ConnectionListener#onConnected(net.Connection)
	 */
	@Override
	public void onConnected(Connection conn) {
		Cons.println("Client connected");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ConnectionListener#onDisconnected(net.Connection)
	 */
	@Override
	public void onDisconnected(Connection conn) {			
		Cons.println("Client disconnected");
		this.players.clear();
		this.outboundQ.clear();
		
		this.localPlayer.invalidate();
		
		if(game!=null) {
            game.destroy();
            game=null;
        }
        
        app.goToMenuScreen();
	}
	
	/*
	 * (non-Javadoc)
	 * @see palisma.shared.NetworkProtocol#processMessage(com.esotericsoftware.kryonet.Connection, palisma.network.messages.Message)
	 */
	@Override
	protected void processMessage(Connection conn, NetMessage message) throws IOException {
		if(!this.connection.isConnected()) {
			return;
		}
		
		/* game messages first, from most frequent to least */
		
		if(message instanceof GameUpdateMessage) {
			receiveGameUpdateMessage(conn, (GameUpdateMessage)message);
		}
		else if(message instanceof GamePartialStatsMessage) {
			receiveGamePartialStatsMessage(conn, (GamePartialStatsMessage)message);
		}
		else if(message instanceof GameStatsMessage) {
			receiveGameStatsMessage(conn, (GameStatsMessage)message);
		}		
		else if(message instanceof PlayerKilledMessage) {
			receivePlayerKilledMessage(conn, (PlayerKilledMessage)message);
		}
		else if(message instanceof PlayerSpawnedMessage) {
			receivePlayerSpawnedMessage(conn, (PlayerSpawnedMessage)message);
		}
		else if(message instanceof GameEndedMessage) {
			receiveGameEndedMessage(conn, (GameEndedMessage)message);
		}
		else if(message instanceof GameReadyMessage) {
			receiveGameReadyMessage(conn, (GameReadyMessage)message);
		}
		else if(message instanceof PlayerSwitchTeamMessage) {
			receivePlayerSwitchedTeamMessage(conn, (PlayerSwitchTeamMessage)message);
		}
		else if(message instanceof RoundStartedMessage) {
			receiveRoundStartedMessage(conn, (RoundStartedMessage)message);
		}
		else if(message instanceof RoundEndedMessage) {
			receiveRoundEndedMessage(conn, (RoundEndedMessage)message);
		}
		else if(message instanceof BombPlantedMessage) {
			receiveBombPlantedMessage(conn, (BombPlantedMessage)message);
		}
		else if(message instanceof BombDisarmedMessage) {
			receiveBombDisarmedMessage(conn, (BombDisarmedMessage)message);
		}
		else if(message instanceof TileRemovedMessage) {
		    receiveTileRemovedMessage(conn, (TileRemovedMessage)message);
		}
		else if(message instanceof TilesRemovedMessage) {
		    receiveTilesRemovedMessage(conn, (TilesRemovedMessage)message);
		}
		/* None game messages */
		
		else if(message instanceof ConnectAcceptedMessage) {
			receiveConnectAcceptedMessage(conn, (ConnectAcceptedMessage)message);
		}
		else if(message instanceof PlayerConnectedMessage) {
			receivePlayerConnectedMessage(conn, (PlayerConnectedMessage)message);
		}
		else if(message instanceof PlayerDisconnectedMessage) {
			receivePlayerDisconnectedMessage(conn, (PlayerDisconnectedMessage)message);
		}
		else if(message instanceof TextMessage) {
			receiveTextMessage(conn, (TextMessage)message);
		}		
		else if(message instanceof TeamTextMessage) {
			receiveTeamTextMessage(conn, (TeamTextMessage)message);
		}	
		else if(message instanceof PlayerSpeechMessage) {
			receivePlayerSpeechMessage(conn, (PlayerSpeechMessage)message);
		}
		else if(message instanceof RconMessage) {
			receiveRconMessage(conn, (RconMessage)message);
		}
		else if(message instanceof RconTokenMessage) {
			receiveRconTokenMessage(conn, (RconTokenMessage)message);
		}
		else if(message instanceof FlagCapturedMessage) {
			receiveFlagCapturedMessage(conn, (FlagCapturedMessage)message);
		}
		else if(message instanceof FlagReturnedMessage) {
			receiveFlagReturnedMessage(conn, (FlagReturnedMessage)message);
		}
		else if(message instanceof FlagStolenMessage) {
			receiveFlagStolenMessage(conn, (FlagStolenMessage)message);
		}
		else {
			Cons.println("Unknown message: " + message);
		}
	}
			
	
	private void applyGameState(NetGameState gameState, boolean sendNotification) {
        if(!localPlayer.isValid()) {
            Cons.println("*** Received gameReady message before connectionAccepted message, skipping!");
        }
        else {
        
            NetMap serverMap = gameState.map;
            try {
                Map map = MapLoaderUtil.loadMap(Scripting.newSandboxedRuntime(), serverMap.path, true);
                
                if(game != null) {
                    game.destroy();
                }
                
                game = new ClientGame(app, players, map, localPlayer);
                game.prepareGame(serverMap.path, gameState);
                
                if(gameCreationListener != null && sendNotification) {
                    gameCreationListener.onGameCreated(game);
                }
                
            } catch (Exception e) {
                Cons.println("*** Unable load the game state: " + e);
                app.goToMenuScreen();
            }
        }
    }
          
    /**
     * @param listener the listener to set
     */
    public void setGameCreationListener(GameCreationListener listener) {
        this.gameCreationListener = listener;
    }
    
    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#connectAccepted(net.Connection, palisma.network.messages.ConnectAcceptedMessage)
     */
    @Override
    public void receiveConnectAcceptedMessage(Connection conn, ConnectAcceptedMessage msg) {                
        this.localPlayer.newSessionPlayerId(msg.playerId);
        this.players.addPlayer(new ClientPlayer(app.getConfig().getPlayerName(), this.localPlayer.getSessionPlayerId()));
        
        applyGameState(msg.gameState, true);
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#gameUpdate(net.Connection, palisma.network.messages.GameUpdateMessage)
     */
    @Override
    public void receiveGameUpdateMessage(Connection conn, GameUpdateMessage msg) {
        if(game != null) {
            game.applyGameUpdate(msg);
        }
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#gameStats(net.Connection, palisma.network.messages.GameStatsMessage)
     */
    @Override
    public void receiveGameStatsMessage(Connection conn, GameStatsMessage msg) {
        if(game != null) {
            game.applyGameStats(msg.stats);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#gamePartialStats(harenet.api.Connection, seventh.network.messages.GamePartialStatsMessage)
     */
    @Override
    public void receiveGamePartialStatsMessage(Connection conn, GamePartialStatsMessage msg) {
        if(game != null) {
            game.applyGamePartialStats(msg.stats);
        }
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#gameEnded(net.Connection, palisma.network.messages.GameEndedMessage)
     */
    @Override
    public void receiveGameEndedMessage(Connection conn, GameEndedMessage msg) {
        if(game != null) {
            game.gameEnded(msg);
        }
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#gameReady(net.Connection, palisma.network.messages.GameReadyMessage)
     */
    @Override
    public void receiveGameReadyMessage(Connection conn, GameReadyMessage msg) {                        
        applyGameState(msg.gameState, false);
        
        app.setScreen(new InGameScreen(app, game));
        game.gameReady(msg);
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#playerConnectedMessage(net.Connection, palisma.network.messages.PlayerConnectedMessage)
     */
    @Override
    public void receivePlayerConnectedMessage(Connection conn, PlayerConnectedMessage msg) {
        if(this.game != null) {
            this.game.playerConnected(msg);
        }
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#playerDisconnectedMessage(net.Connection, palisma.network.messages.PlayerDisconnectedMessage)
     */
    @Override
    public void receivePlayerDisconnectedMessage(Connection conn, PlayerDisconnectedMessage msg) {
        if(this.game != null) {
            this.game.playerDisconnected(msg);
        }
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#textMessage(net.Connection, palisma.network.messages.TextMessage)
     */
    @Override
    public void receiveTextMessage(Connection conn, TextMessage msg) {
        if(game != null) {
            game.textMessage(msg);
        }
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#teamTextMessage(net.Connection, palisma.network.messages.TeamTextMessage)
     */
    @Override
    public void receiveTeamTextMessage(Connection conn, TeamTextMessage msg) {
        if(game!=null) {
            game.teamTextMessage(msg);
        }
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#playerSpawned(net.Connection, palisma.network.messages.PlayerSpawnedMessage)
     */
    @Override
    public void receivePlayerSpawnedMessage(Connection conn, PlayerSpawnedMessage msg) {
        if(game != null) {
            game.playerSpawned(msg);
        }
    }

    /* (non-Javadoc)
     * @see palisma.client.ClientProtocol#playerKilled(net.Connection, palisma.network.messages.PlayerKilledMessage)
     */
    @Override
    public void receivePlayerKilledMessage(Connection conn, PlayerKilledMessage msg) {
        if(game != null) {
            game.playerKilled(msg);
        }
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#playerSwitchedTeamMessage(net.Connection, seventh.network.messages.PlayerSwitchTeamMessage)
     */
    @Override
    public void receivePlayerSwitchedTeamMessage(Connection conn, PlayerSwitchTeamMessage msg) {
        if(game!=null) {
            game.playerSwitchedTeam(msg);
        }
    }
    
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#playerSpeech(harenet.api.Connection, seventh.network.messages.PlayerSpeechMessage)
     */
    @Override
    public void receivePlayerSpeechMessage(Connection conn, PlayerSpeechMessage msg) {
        if(game!=null) {
            game.playerSpeech(msg);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#roundEnded(net.Connection, seventh.network.messages.RoundEndedMessage)
     */
    @Override
    public void receiveRoundEndedMessage(Connection conn, RoundEndedMessage msg) {
        if(game!=null) {
            game.roundEnded(msg);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#roundStarted(net.Connection, seventh.network.messages.RoundStartedMessage)
     */
    @Override
    public void receiveRoundStartedMessage(Connection conn, RoundStartedMessage msg) {
        if(game!=null) {
            game.roundStarted(msg);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#bombDefused(net.Connection, seventh.network.messages.BombDefusedMessage)
     */
    @Override
    public void receiveBombDisarmedMessage(Connection conn, BombDisarmedMessage msg) {
        if(game!=null) {
            game.bombDisarmed(msg);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#bombPlanted(net.Connection, seventh.network.messages.BombPlantedMessage)
     */
    @Override
    public void receiveBombPlantedMessage(Connection conn, BombPlantedMessage msg) {
        if(game!=null) {
            game.bombPlanted(msg);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#bombExploded(net.Connection, seventh.network.messages.BombExplodedMessage)
     */
    @Override
    public void receiveBombExplodedMessage(Connection conn, BombExplodedMessage msg) {
        if(game!=null) {
            game.bombExploded(msg);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#tileRemoved(harenet.api.Connection, seventh.network.messages.TileRemovedMessage)
     */
    @Override
    public void receiveTileRemovedMessage(Connection conn, TileRemovedMessage msg) {
        if(game!=null) {
            game.removeTile(msg);
        }
        
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#tilesRemoved(harenet.api.Connection, seventh.network.messages.TilesRemovedMessage)
     */
    @Override
    public void receiveTilesRemovedMessage(Connection conn, TilesRemovedMessage msg) {
        if(game!=null) {
            game.removeTiles(msg);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#rconMessage(net.Connection, seventh.network.messages.RconMessage)
     */
    @Override
    public void receiveRconMessage(Connection conn, RconMessage msg) {
        app.getConsole().println(msg.getCommand());
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#rconTokenMessage(net.Connection, seventh.network.messages.RconTokenMessage)
     */
    @Override
    public void receiveRconTokenMessage(Connection conn, RconTokenMessage msg) {
        if(game!=null) {
            game.getLocalSession().setRconToken(msg.getToken());
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#receiveFlagCapturedMessage(harenet.api.Connection, seventh.network.messages.FlagCapturedMessage)
     */
    @Override
    public void receiveFlagCapturedMessage(Connection conn, FlagCapturedMessage msg) {
    	if(game!=null) {
    		game.flagCaptured(msg);
    	}
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#receiveFlagReturnedMessage(harenet.api.Connection, seventh.network.messages.FlagReturnedMessage)
     */
    @Override
    public void receiveFlagReturnedMessage(Connection conn, FlagReturnedMessage msg) {
    	if(game!=null) {
    		game.flagReturned(msg);
    	}
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#receiveFlagStolenMessage(harenet.api.Connection, seventh.network.messages.FlagStolenMessage)
     */
    @Override
    public void receiveFlagStolenMessage(Connection conn, FlagStolenMessage msg) {
    	if(game!=null) {
    		game.flagStolen(msg);
    	}
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendClientReadyMessage(seventh.network.messages.ClientReadyMessage)
     */
    @Override
    public void sendClientReadyMessage(ClientReadyMessage msg) {
        sendReliableMessage(msg);        
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendConnectRequestMessage(seventh.network.messages.ConnectRequestMessage)
     */
    @Override
    public void sendConnectRequestMessage(ConnectRequestMessage msg) {
        sendReliableMessage(msg);   
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendPlayerNameChangedMessage(seventh.network.messages.PlayerNameChangeMessage)
     */
    @Override
    public void sendPlayerNameChangedMessage(PlayerNameChangeMessage msg) {
        queueSendReliableMessage(msg);
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendPlayerSwitchWeaponClassMessage(seventh.network.messages.PlayerSwitchWeaponClassMessage)
     */
    @Override
    public void sendPlayerSwitchWeaponClassMessage(PlayerSwitchWeaponClassMessage msg) {
        queueSendReliableMessage(msg);
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendAICommandMessage(seventh.network.messages.AICommandMessage)
     */
    @Override
    public void sendAICommandMessage(AICommandMessage msg) {
        queueSendReliableMessage(msg);
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendTextMessage(seventh.network.messages.TextMessage)
     */
    @Override
    public void sendTextMessage(TextMessage msg) {
        queueSendReliableMessage(msg);
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendTeamTextMessage(seventh.network.messages.TeamTextMessage)
     */
    @Override
    public void sendTeamTextMessage(TeamTextMessage msg) {
        queueSendReliableMessage(msg);
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendPlayerSpeechMessage(seventh.network.messages.PlayerSpeechMessage)
     */
    @Override
    public void sendPlayerSpeechMessage(PlayerSpeechMessage msg) {
        queueSendReliableMessage(msg);
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendPlayerSwitchTeamMessage(seventh.network.messages.PlayerSwitchTeamMessage)
     */
    @Override
    public void sendPlayerSwitchTeamMessage(PlayerSwitchTeamMessage msg) {
        queueSendReliableMessage(msg);
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendRconMessage(seventh.network.messages.RconMessage)
     */
    @Override
    public void sendRconMessage(RconMessage msg) {
        queueSendReliableMessage(msg);
    }

    /* (non-Javadoc)
     * @see seventh.client.ClientProtocol#sendPlayerInputMessage(seventh.network.messages.PlayerInputMessage)
     */
    @Override
    public void sendPlayerInputMessage(PlayerInputMessage msg) {
        sendUnReliableMessage(msg);
    }
}
