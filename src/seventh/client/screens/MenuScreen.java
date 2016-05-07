/*
 * see license.txt 
 */
package seventh.client.screens;

import leola.vm.Leola;
import seventh.client.Inputs;
import seventh.client.Screen;
import seventh.client.SeventhGame;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Colors;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Renderable;
import seventh.client.gfx.Theme;
import seventh.client.sfx.Sounds;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.server.GameServer;
import seventh.server.GameServer.GameServerSettings;
import seventh.server.GameServer.OnServerReadyListener;
import seventh.shared.Command;
import seventh.shared.Console;
import seventh.shared.Scripting;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Panel;
import seventh.ui.UserInterfaceManager;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.view.ButtonView;
import seventh.ui.view.PanelView;

/**
 * The main menu screen
 * 
 * @author Tony
 *
 */
public class MenuScreen implements Screen {
	
	private SeventhGame app;	
	private Theme theme;
	
	private UserInterfaceManager uiManager;
	
	private Button singlePlyBtn, multiPlyBtn
				, optionsBtn, creditsBtn, exitBtn;	
	
	private Panel menuPanel;
	private PanelView<Renderable> panelView;

	/**
	 * 
	 */
	public MenuScreen(final SeventhGame app) {
		this.app = app;
		this.theme = app.getTheme();
						
		this.uiManager = app.getUiManager();
		this.panelView = new PanelView<>();
		this.menuPanel = new Panel();
				
		Vector2f uiPos = new Vector2f(app.getScreenWidth()/2, 300);
		
		this.singlePlyBtn = setupButton(uiPos, "Single Player");
		this.singlePlyBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.setScreen(new ServerSetupScreen(MenuScreen.this));
				
			}
		});
		
		uiPos.y += 80;
		
		this.multiPlyBtn = setupButton(uiPos, "Multiplayer");
		this.multiPlyBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.setScreen(new ServerListingsScreen(MenuScreen.this));
			}
		});
		
		uiPos.y += 80;
		
		this.optionsBtn = setupButton(uiPos, "Options");
		this.optionsBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.pushScreen(new OptionsScreen(app));
			}
		});
		
		uiPos.y += 80;
		
		this.creditsBtn = setupButton(uiPos, "Credits");
		this.creditsBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.pushScreen(new AnimationEditorScreen(app));
			}
		});
		
		uiPos.y += 80;
		
		this.exitBtn = setupButton(uiPos, "Quit");
		this.exitBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.shutdown();
			}
		});
		
		this.menuPanel.addWidget(singlePlyBtn);
		this.menuPanel.addWidget(multiPlyBtn);
		this.menuPanel.addWidget(optionsBtn);
		this.menuPanel.addWidget(creditsBtn);
		this.menuPanel.addWidget(exitBtn);
		
		this.panelView.addElement(new ButtonView(singlePlyBtn));
		this.panelView.addElement(new ButtonView(multiPlyBtn));
		this.panelView.addElement(new ButtonView(optionsBtn));
		this.panelView.addElement(new ButtonView(creditsBtn));
		this.panelView.addElement(new ButtonView(exitBtn));
		
		
		Console console = app.getConsole();
		console.execute("help");
		
		
		console.addCommand(new Command("start_local") {			
			@Override
			public void execute(Console console, String... args) {
				startLocalServer(null);				
			}
		});
	}
	
	private Button setupButton(Vector2f uiPos, String text) {
		Button btn = new Button();
		btn.setText(text);
		btn.setBounds(new Rectangle(280, 80));
		btn.getBounds().centerAround(uiPos);
		btn.setForegroundColor(theme.getForegroundColor());
		btn.getTextLabel().setForegroundColor(theme.getForegroundColor());
		btn.setTheme(theme);
		btn.setEnableGradiant(false);
		return btn;
	}

	/**
	 * Starts a local server.  This method will spawn a new daemon
	 * thread running the game server
	 */
	public void startLocalServer(final GameServerSettings settings) {
		final Console console = app.getConsole();
		final int port = settings.port;
		final LoadingScreen loadingScreen = new LoadingScreen(app, "localhost", port, false);
		
		final Thread gameThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Leola runtime = Scripting.newRuntime();
					final GameServer server = new GameServer(console, runtime, true, settings );
					server.setServerListener(new OnServerReadyListener() {
						
						@Override
						public void onServerReady(GameServer server) {
							loadingScreen.connectToServer();					
						}
					});
					
					console.addCommand(new Command("kill_local_server") {						
						@Override
						public void execute(Console console, String... args) {
							server.shutdown();
						}
					});
					
					server.start(port);
				} 
				catch (Exception e) {
					console.println("An error occured with the server: " + e);
				}
				finally {
					console.removeCommand("kill_local_server");
				}
			}
		}, "local-game-server");
		
		gameThread.setDaemon(true);
		gameThread.start();		
		
		app.setScreen(loadingScreen);
	}
		
	/* (non-Javadoc)
	 * @see palisma.shared.State#enter()
	 */
	@Override
	public void enter() {	
		menuPanel.show();
		Sounds.playGlobalSound(Sounds.uiNavigate);
	}
	
	/* (non-Javadoc)
	 * @see palisma.shared.State#exit()
	 */
	@Override
	public void exit() {
		menuPanel.hide();
		Sounds.playGlobalSound(Sounds.uiNavigate);
	}


	
	/* (non-Javadoc)
	 * @see palisma.shared.State#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
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
		canvas.fillRect(0, 0, canvas.getWidth() + 100,  canvas.getHeight() + 100, theme.getBackgroundColor());
		
		canvas.setFont(theme.getPrimaryFontName(), 18);	
		this.panelView.render(canvas, null, 0);
		
		canvas.begin();
		canvas.setFont(theme.getPrimaryFontName(), 94);
				
		int fontColor = theme.getForegroundColor();
		String message = "The Seventh";
		int center = (canvas.getWidth() - canvas.getWidth(message)) / 2;
		RenderFont.drawShadedString(canvas, message, center, canvas.getHeight()/6, fontColor);
		
		canvas.setFont(theme.getSecondaryFontName(), 12);
		RenderFont.drawShadedString(canvas, SeventhGame.getVersion(), 5, canvas.getHeight()-5, Colors.setAlpha(fontColor, 150));
		
		this.uiManager.render(canvas);
				
		canvas.end();		
	}
	
	/**
	 * @return the theme
	 */
	public Theme getTheme() {
		return theme;
	}
	
	/**
	 * @return the app
	 */
	public SeventhGame getApp() {
		return app;
	}
	
	/**
	 * @return the uiManager
	 */
	public UserInterfaceManager getUiManager() {
		return uiManager;
	}

	/* (non-Javadoc)
	 * @see palisma.client.Screen#getInputs()
	 */
	@Override
	public Inputs getInputs() {
		return this.uiManager;
	}

}
