/*
 * The Seventh
 * see license.txt 
 */
package seventh.client;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.Stack;

import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import seventh.client.gfx.Art;
import seventh.client.gfx.BlurEffectShader;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.FireEffectShader;
import seventh.client.gfx.GdxCanvas;
import seventh.client.gfx.LightEffectShader;
import seventh.client.gfx.RippleEffectShader;
import seventh.client.gfx.Terminal;
import seventh.client.gfx.Theme;
import seventh.client.screens.AnimationEditorScreen;
import seventh.client.screens.LoadingScreen;
import seventh.client.screens.MenuScreen;
import seventh.client.sfx.Sounds;
import seventh.network.messages.PlayerNameChangeMessage;
import seventh.shared.Command;
import seventh.shared.CommonCommands;
import seventh.shared.Config;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.StateMachine;
import seventh.shared.TimeStep;
import seventh.ui.UserInterfaceManager;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL10;

/**
 * @author Tony
 *
 */
public class SeventhGame implements ApplicationListener {

	private static final String HEADER = 
			"=============================================================================\n" +
			"                                                                             \n" +
			"                             === The Seventh ===                             \n" +
			"                                5d Studios (c)                               \n" +
			"                                                                             \n" +
			"                                                                             \n" +
			"=============================================================================\n" +
			"                                                                             \n" 
			;
	
	private static final String VERSION = "v0.1.0.21-BETA";
	public static final int DEFAULT_MINIMIZED_SCREEN_WIDTH = 1024;
	public static final int DEFAULT_MINIMIZED_SCREEN_HEIGHT = 768;
	
	private StateMachine<Screen> sm;
	
	private Config config;
	
	private Terminal terminal;
	private Console console;
	private Theme theme;	
	
	private Canvas canvas;
	
	private float accum;
	private TimeStep timeStep;
	private long gameClock;
	
	private InputMultiplexer inputs;
	private KeyMap keyMap;
	
	private Network network;
	
	private boolean isVSync;
	private Stack<Screen> screenStack;
	
	private UserInterfaceManager uiManager;
	
	private static final float TICK_RATE = 1.0f / 30.0f;
	private static final long DELTA_TIME = 1000 / 30;
	/**
	 */
	public SeventhGame(Config config) throws Exception {
		this.console = Cons.getImpl();
		this.timeStep = new TimeStep();
		this.terminal = new Terminal(console);
						
		this.inputs = new InputMultiplexer();
		
		Cons.println(HEADER);
		Cons.println("*** Initializing " + VERSION + " ***");
		Cons.println("Start Stamp: " + new Date());
		

		this.config = config;
		LeoObject controls = this.config.get("controls");
		this.keyMap = new KeyMap( (controls.isMap()) ? (LeoMap)controls : new LeoMap() );
						
		this.network = new Network(this.config, console);
		
		this.screenStack = new Stack<Screen>();
		this.sm = new StateMachine<Screen>();										
		this.theme = new Theme();
				
		setupCommand(Cons.getImpl());		
	}
	
	private void setupCommand(Console console) {
		CommonCommands.addCommonCommands(console);
			
		Command exit = new Command("exit") {			
			@Override
			public void execute(Console console, String... args) {
				console.println("Exiting the game...");
				shutdown();	
			}
		};
		
		console.addCommand(exit);
		console.addCommand("quit", exit);
		
		console.addCommand(new Command("clear") {			
			@Override
			public void execute(Console console, String... args) {
				terminal.clear();
			}
		});
		
		console.addCommand(new Command("name") {
			@Override
			public void execute(Console console, String... args) {
				String newName = this.mergeArgsDelim(" ", args);
				config.set(newName, "name");
				if(network != null) {
					PlayerNameChangeMessage msg = new PlayerNameChangeMessage();
					msg.name = newName;
					network.queueSendReliableMessage(msg);
				}
			}
		});
		
		
		console.addCommand(new Command("connect") {			
			@Override
			public void execute(Console console, String... args) {
				if(args.length<1) {
					console.println("<usage> connect [IP/DNS] \n   Ex. connect 68.2.44.100:9844");
					
				}
				else {
					try {
						String host = args[0];
						String p[] = host.split(":");
						host = p[0];
						int port= Integer.parseInt(p[1]);
						
						setScreen(new LoadingScreen(SeventhGame.this, host, port, true));
					}
					catch(Exception e) {
						console.println("*** Error attempting to connect: \n" + e);
					}
				}
			}
		});
		
		console.addCommand(new Command("help") {
			@Override
			public void execute(Console console, String... args) {
				console.println("\n");
				console.println("The console is a means for executing commands.  The set of available commands ");
				console.println("differ given the current game context (i.e., in-game, main-menu, etc.).  To ");
				console.println("get a list of available commands type: 'cmdlist'.  The console supports TAB completion. ");
			}
		});

		console.addCommand(new Command("v_reload_shaders") {			
			@Override
			public void execute(Console console, String... args) {
				BlurEffectShader.getInstance().reload();
				FireEffectShader.getInstance().reload();
				RippleEffectShader.getInstance().reload();
				LightEffectShader.getInstance().reload();
			}
		});
		
		console.addCommand(new Command("v_reload_gfx") {			
			@Override
			public void execute(Console console, String... args) {
				Art.reload();
			}
		});
		
//		console.addCommand(new Command("v_reload_animation") {			
//			@Override
//			public void execute(Console console, String... args) {
//				try {
//					Art.debugAnimations(new Leola());
//				} catch (Exception e) {
//					console.println("*** Error loading animations: " + e);
//				}
//			}
//		});
		
		console.addCommand(new Command("animation_editor"){			
			@Override
			public void execute(Console console, String... args) {
				pushScreen(new AnimationEditorScreen(SeventhGame.this));
			}
		});
		
		console.addCommand(new Command("mouse_sensitivity") {
			
			@Override
			public void execute(Console console, String... args) {
				seventh.client.gfx.Cursor cursor = uiManager.getCursor();
				if(args.length==0) {
					console.println(cursor.getMouseSensitivity());
				}
				else {
					try {
						float value = Float.parseFloat(args[0]);
						cursor.setMouseSensitivity(value);
					}
					catch(NumberFormatException e) {
						console.println("Must be a number between 0 and 1");
					}
				}
				
			}
		});
	}

	/**
	 * Hide the Mouse cursor
	 * 
	 * @param visible
	 */
	private void setHWCursorVisible(boolean visible) {
		if (Gdx.app.getType() != ApplicationType.Desktop && Gdx.app instanceof LwjglApplication) {
			return;
		}
	
		try {
			/* make sure the mouse doesn't move off the screen */
			Gdx.input.setCursorCatched(true);
			
			Cursor emptyCursor = null;
			if (Mouse.isCreated()) {
				int min = org.lwjgl.input.Cursor.getMinCursorSize();
				IntBuffer tmp = BufferUtils.createIntBuffer(min * min);
				emptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
			} else {
				Cons.println("Could not create empty cursor before Mouse object is created");
			}
		
			if (/*Mouse.isInsideWindow() &&*/ emptyCursor != null) {
				Mouse.setNativeCursor(visible ? null : emptyCursor);
			}
		}
		catch(LWJGLException e) {
			Cons.println("*** Unable to hide cursor: " + e);
		}
	}
	
	/**
	 * Shuts down the game
	 */
	public void shutdown() {
		Gdx.app.exit();
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	@Override
	public void create() {			
		Art.load();
		Sounds.init(config);
				

		this.uiManager = new UserInterfaceManager();
		seventh.client.gfx.Cursor cursor = this.uiManager.getCursor();
		float sensitivity = config.getFloat("mouse_sensitivity");
		if(sensitivity > 0) {
			cursor.setMouseSensitivity(sensitivity);
		}
		
		Gdx.input.setInputProcessor(this.inputs);
//		Gdx.input.setCursorCatched(true);
		
		initControllers();		
		videoReload();		
		setScreen(new MenuScreen(this));
	}

	private void initControllers() {
		Cons.println("Detecting controllers...");
		for(Controller control : Controllers.getControllers()) {
			Cons.println("Found: " + control.getName());			
		}	
		Cons.println("Completed checking for controllers");
	}
	
	private void videoReload() {
		Gdx.input.setCursorPosition(getScreenWidth()/2, getScreenHeight()/2);		
		setHWCursorVisible(false);
		
		this.inputs.addProcessor(new Inputs() {
			@Override
			public boolean keyUp(int key) {			
				if(key == Keys.GRAVE) {
					terminal.toggle();
					return true;
				}
				return false;
			}
		});
		
		this.inputs.addProcessor(this.terminal.getInputs());
		
		setVSync(config.getBool("video", "vsync"));
		
		
		this.canvas = new GdxCanvas();
		try {
			this.canvas.loadFont("./seventh/gfx/fonts/Courier New.ttf", "Courier New");
			this.canvas.loadFont("./seventh/gfx/fonts/Consola.ttf", "Consola");
			this.canvas.loadFont("./seventh/gfx/fonts/Army.ttf", "Army");
			this.canvas.loadFont("./seventh/gfx/fonts/Napalm Vertigo.ttf", "Napalm Vertigo");
//			this.canvas.loadFont("./seventh/gfx/fonts/Bebas.ttf", "Bebas");
			this.canvas.loadFont("./seventh/gfx/fonts/future.ttf", "Futurist Fixed-width");
			
			this.canvas.setDefaultFont("Courier New", 14);			
		}
		catch (IOException e) {
			Cons.println("*** Unable to load font: " + e);
		}

//		if(!reloadCurrentScreen()) {
//			setScreen(new MenuScreen(this));	
//		}
		
//		if(this.screenStack.isEmpty())  {
//			setScreen(new MenuScreen(this));
//		}
//		else {
//			popScreen();
//		}
		setHWCursorVisible(false);
	}
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#dispose()
	 */
	@Override
	public void dispose() {		
		Sounds.destroy();
		Art.destroy();
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#pause()
	 */
	@Override
	public void pause() {		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#render()
	 */
	@Override
	public void render() {
		//Gdx.gl.glClearColor(0, 0, 0, 0); 
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		float dt = Gdx.graphics.getDeltaTime(); 
		accum += dt;
		
		while (accum > TICK_RATE) {
			timeStep.setDeltaTime(DELTA_TIME);
			timeStep.setGameClock(gameClock);
						
			updateScreen(timeStep);
			
			accum -= TICK_RATE;
			gameClock += DELTA_TIME;
		}
		
		renderScreen(canvas);		
	}
	
	/**
	 * Pushes a {@link Screen} onto the stack.
	 * 
	 * @param newScreen
	 */
	public void pushScreen(Screen newScreen) {
		this.screenStack.add(newScreen);
		preserveStackSetScreen(newScreen);
	}
	
	/**
	 * Pops a {@link Screen} off the stack.
	 */
	public void popScreen() {
		if(!this.screenStack.isEmpty()) {
			this.screenStack.pop();
			if(!this.screenStack.isEmpty()) {
				preserveStackSetScreen(this.screenStack.peek());
			}
		}
	}
	
	/**
	 * Pops and pushes the the current {@link Screen}, effectively
	 * reloading it
	 * @return true if actually reloaded a screen, false if no-op
	 */
	public boolean reloadCurrentScreen() {
		if(!this.screenStack.isEmpty()) {
			Screen screen = this.screenStack.pop();
			preserveStackSetScreen(screen);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Preserves the stack state
	 * @param newScreen
	 */
	private void preserveStackSetScreen(Screen newScreen) {
		Screen previousScreen = this.sm.getCurrentState();
		this.sm.changeState(newScreen);
		updateInputs(previousScreen);
	}
	
	/**
	 * @param newScreen
	 */
	public void setScreen(Screen newScreen) {
		this.screenStack.clear();
		pushScreen(newScreen);
	}
	
	/**
	 * @return the uiManager
	 */
	public UserInterfaceManager getUiManager() {
		return uiManager;
	}
	
	/**
	 * @return the network
	 */
	public Network getNetwork() {
		return network;
	}
		
	
	/**
	 * @return the keyMap
	 */
	public KeyMap getKeyMap() {
		return keyMap;
	}
	
	/**
	 * @return the theme
	 */
	public Theme getTheme() {
		return theme;
	}
	
	/**
	 * @return the terminal
	 */
	public Terminal getTerminal() {
		return terminal;
	}
	
	/**
	 * @return the config
	 */
	public Config getConfig() {
		return config;
	}

	public int getFps() {
		return Gdx.graphics.getFramesPerSecond();
	}
	
	/**
	 * @return the gameClock
	 */
	public long getGameClock() {
		return gameClock;
	}
	
	/**
	 * @return the screenHeight
	 */
	public int getScreenHeight() {
		return Gdx.graphics.getHeight();
	}
	
	/**
	 * @return the screenWidth
	 */
	public int getScreenWidth() {
		return Gdx.graphics.getWidth();
	}
	
	/**
	 * @return true if full screen
	 */
	public boolean isFullscreen() {
		return Gdx.graphics.isFullscreen();
	}
	
	/**
	 * @return if VSync is enabled
	 */
	public boolean isVSync() {
		return this.isVSync;
	}
	
	/**
	 * Enable/disable vsync
	 * @param vsync
	 */
	public void setVSync(boolean vsync) {
		this.isVSync = vsync;
		Gdx.graphics.setVSync(vsync);
	}
	
	/**
	 * @return the version
	 */
	public static String getVersion() {
		return VERSION;
	}
	
	private void updateScreen(TimeStep timeStep) {
		this.console.update(timeStep);
		this.sm.update(timeStep);
		
		if(this.terminal.isActive()) {
			this.terminal.update(timeStep);
		}		
	}
	

	private void renderScreen(Canvas canvas) {
		Screen screen = this.sm.getCurrentState();
		canvas.preRender();
		if(screen != null) {
			screen.render(canvas);
		}		
		
		if(this.terminal.isActive()) {			
			this.terminal.render(canvas);			
		}
		canvas.postRender();
	}
	
	private void updateInputs(Screen previousScreen) {
	
		if(previousScreen != null) {			
			this.inputs.removeProcessor(previousScreen.getInputs());
		}
		
		Screen screen = this.sm.getCurrentState();
		if(screen != null) {
			Inputs inputs = screen.getInputs();
			//Gdx.input.setInputProcessor(inputs);
			this.inputs.addProcessor(inputs);
		}	
	}
	
	/**
	 * Adds an input listener
	 * @param inputs
	 */
	public void addInput(Inputs inputs) {
		this.inputs.addProcessor(inputs);
	}
	
	/**
	 * Adds an input listener to the front
	 * @param inputs
	 */
	public void addInputToFront(Inputs inputs) {
		this.inputs.addProcessor(0, inputs);
	}
	
	/**
	 * Removes an input listener
	 * @param inputs
	 */
	public void removeInput(Inputs inputs) {
		this.inputs.removeProcessor(inputs);
	}
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
//		setHWCursorVisible(false);
//		videoReload();
	}

	/**
	 * Restarts the video 
	 */
	public void restartVideo() {
		setHWCursorVisible(false);
		videoReload();
	}
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#resume()
	 */
	@Override
	public void resume() {
		setHWCursorVisible(false);
		/* the mouse gets captured, so when we 
		 * gain focus back, center the mouse so the 
		 * user can find it
		 */
		getUiManager().getCursor().centerMouse();
	}

	/**
	 * @return
	 */
	public Console getConsole() {
		return Cons.getImpl();
	}


}
