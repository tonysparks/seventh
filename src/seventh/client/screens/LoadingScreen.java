/*
 * see license.txt 
 */
package seventh.client.screens;

import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import seventh.client.ClientGame;
import seventh.client.SeventhGame;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Theme;
import seventh.client.inputs.Inputs;
import seventh.client.network.ClientConnection;
import seventh.client.network.ClientProtocol;
import seventh.client.network.ClientProtocol.GameCreationListener;
import seventh.network.messages.ClientReadyMessage;
import seventh.network.messages.ConnectRequestMessage;
import seventh.shared.Cons;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class LoadingScreen implements Screen {

	private SeventhGame app;
	private Theme theme;
	private String host;
	private int port;
	
	private String message;
	
	private long timer;
	private int index = 0;
	private String[] spinner = {"-","\\", "|", "/" };
	
	private AtomicBoolean isDone;
	
	private Thread thread;
	private AtomicReference<ClientGame> game;
	private ClientConnection connection;
	
	private boolean connectNow;
	private boolean isConnecting;
	
	private Inputs inputs = new Inputs() {
		/* (non-Javadoc)
		* @see seventh.client.Inputs#keyUp(int)
		*/
		@Override
		public boolean keyUp(int key) {			
			
			if(key == KeyEvent.VK_ESCAPE) {
				if(thread != null) {
					Cons.println("Cancelling connection");
					thread.interrupt();
				}					
			}	
			return true;
		}
	};
	
	/**
	 * 
	 */
	public LoadingScreen(SeventhGame app, String host, int port, boolean connectNow) {
		this.app = app;		
		this.host = host;
		this.port = port;
		
		this.connectNow = connectNow;
		this.isConnecting = false;
		
		this.connection = app.getClientConnection();
		this.game = new AtomicReference<ClientGame>();
		this.isDone = new AtomicBoolean();
		this.theme = app.getTheme();
		
		this.message = "Attempting to connect to: ";
	}

	/**
	 * Attempts to connect to the server
	 */
	public void connectToServer() {
		synchronized (this) {	
			if(!isConnecting) {
				isConnecting = true;
				thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						message += host + ":" + port + "...";
						try {			
							Cons.println("Connecting to: " + host + ":" + port);
							connection.disconnect();
														
							connection.connect(host, port);
							message += "Success!";
							
							final ClientProtocol protocol = connection.getClientProtocol();
							
							Cons.println("Successfully connected!");
							connection.setGameCreationListener(new GameCreationListener() {
                                
                                @Override
                                public void onGameCreated(ClientGame game) {                                   
                                    LoadingScreen.this.game.set(game);                          
                                    protocol.sendClientReadyMessage(new ClientReadyMessage());
                                    isDone.set(true);                                                           
                                }
                            });
							
							ConnectRequestMessage msg = new ConnectRequestMessage();
							msg.name = app.getConfig().getPlayerName();
							
							protocol.sendConnectRequestMessage(msg);
						}
						catch(Exception e) {
							Cons.println("*** Unable to connect to the server :: \n" + e);
							Cons.getImpl().execute("kill_local_server");
							app.goToMenuScreen();
						}						
					}
				});
				
				thread.start();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see palisma.shared.State#enter()
	 */
	@Override
	public void enter() {
		
		if(connectNow) {
			connectToServer();
		}
	}

	/* (non-Javadoc)
	 * @see palisma.shared.State#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		timer -= timeStep.getDeltaTime();
		if (timer <= 0) {
			index = (index+1) % spinner.length;
			timer = 100;
		}
		
		
		connection.updateNetwork(timeStep);
		
		
		if(isDone.get()) {
			app.setScreen(new InGameScreen(app, game.get()));
		}
	}

	/* (non-Javadoc)
	 * @see palisma.shared.State#exit()
	 */
	@Override
	public void exit() {
	}

	/* (non-Javadoc)
	 * @see palisma.client.Screen#destroy()
	 */
	@Override
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see palisma.client.Screen#render(leola.live.gfx.Canvas)
	 */
	@Override
	public void render(Canvas canvas, float alpha) {
		
		canvas.fillRect(0,0
					  , canvas.getWidth()+100, canvas.getHeight()+100, theme.getBackgroundColor());
				
		canvas.setFont(theme.getSecondaryFontName(), 18);
		RenderFont.drawShadedString(canvas, spinner[index] + " " + message
									, 20, canvas.getHeight()-20, theme.getForegroundColor());		
	}

	/* (non-Javadoc)
	 * @see palisma.client.Screen#getInputs()
	 */
	@Override
	public Inputs getInputs() {
		return this.inputs;
	}

}
