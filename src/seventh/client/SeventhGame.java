/*
 * The Seventh
 * see license.txt 
 */
package seventh.client;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.Stack;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;

import leola.vm.types.LeoObject;
import seventh.ClientMain;
import seventh.client.gfx.Art;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.GdxCanvas;
import seventh.client.gfx.Terminal;
import seventh.client.gfx.Theme;
import seventh.client.gfx.effects.BlurEffectShader;
import seventh.client.gfx.effects.FireEffectShader;
import seventh.client.gfx.effects.LightEffectShader;
import seventh.client.gfx.effects.RippleEffectShader;
import seventh.client.inputs.Inputs;
import seventh.client.inputs.KeyMap;
import seventh.client.network.ClientConnection;
import seventh.client.screens.AnimationEditorScreen;
import seventh.client.screens.LoadingScreen;
import seventh.client.screens.MenuScreen;
import seventh.client.screens.Screen;
import seventh.client.screens.ShaderEditorScreen;
import seventh.client.sfx.Sounds;
import seventh.network.messages.PlayerNameChangeMessage;
import seventh.shared.Command;
import seventh.shared.CommonCommands;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.StateMachine;
import seventh.shared.TimeStep;
import seventh.ui.UserInterfaceManager;

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
    
    private static final String VERSION = "v0.3.0-BETA";
    public static final int DEFAULT_MINIMIZED_SCREEN_WIDTH  = 840; // 640//960;//1024;
    public static final int DEFAULT_MINIMIZED_SCREEN_HEIGHT = 480;//400//600;//768;
    
    private StateMachine<Screen> sm;
    
    private ClientSeventhConfig config;
    
    private Terminal terminal;
    private Console console;
    private Theme theme;    
    
    private Canvas canvas;
    
    private InputMultiplexer inputs;
    private KeyMap keyMap;
    
    private final ClientConnection connection;
        
    private boolean isVSync;
    private Stack<Screen> screenStack;
    
    private UserInterfaceManager uiManager;
    private MenuScreen menuScreen;
    
    private TimeStep timeStep;
    private long gameClock;
    
    private double currentTime;
    private double accumulator;
    private static final double step = 1.0/30.0;    
    private static final long DELTA_TIME = 1000 / 30;
    
    /**
     * @param config
     */
    public SeventhGame(ClientSeventhConfig config) throws Exception {
        this.console = Cons.getImpl();
        this.timeStep = new TimeStep();
        this.terminal = new Terminal(console, config);
                        
        this.inputs = new InputMultiplexer();
        
        Cons.println(HEADER);
        Cons.println("*** Initializing " + VERSION + " ***");
        Cons.println("Start Stamp: " + new Date());
        

        this.config = config;        
        this.keyMap = config.getKeyMap();
                        
        this.connection = new ClientConnection(this, config, console);
        
        this.screenStack = new Stack<Screen>();
        this.sm = new StateMachine<Screen>();                                        
        this.theme = new Theme();
                
        setupCommand(Cons.getImpl());
        
        // Can't set this up here because the 
        // Gdx context hasn't been created yet
//        this.menuScreen = new MenuScreen(this);
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
                config.setPlayerName(newName);
                if(connection.isConnected()) {
                    PlayerNameChangeMessage msg = new PlayerNameChangeMessage();
                    msg.name = newName;
                    connection.getClientProtocol().sendPlayerNameChangedMessage(msg);
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
                
        console.addCommand(new Command("animation_editor"){            
            @Override
            public void execute(Console console, String... args) {
                pushScreen(new AnimationEditorScreen(SeventhGame.this));
            }
        });
        
        console.addCommand(new Command("shader_editor"){            
            @Override
            public void execute(Console console, String... args) {
                pushScreen(new ShaderEditorScreen(SeventhGame.this));
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
        
        console.addCommand(new Command("client_config") {

            @Override
            public void execute(Console console, String... args) {
                if(args.length==0) {
                    console.println("<usage> client_config [config property name] [(optional) value to set]");
                    console.println("Ex. ");
                    console.println("\tclient_config show_debug_info");
                    console.println("\tshow_debug_info = false");
                    return;
                }

                String[] keys = args[0].split("\\.");
                if(args.length == 1) {
                    LeoObject value = config.getConfig().get(keys);
                    console.println(args[0] + " = " + value);
                }
                else {
                    String newValue = mergeArgsDelimAt(" ", 1, args);
                    
                    try {
                        config.getConfig().set(Double.parseDouble(newValue), keys);
                        return;
                    }
                    catch(NumberFormatException e) {}
                    
                    try {
                        config.getConfig().set(Boolean.parseBoolean(newValue), keys);
                        return;
                    }
                    catch(NumberFormatException e) {}
                    
                    config.getConfig().set(newValue, keys);
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
            seventh.client.gfx.Cursor cursor = this.uiManager.getCursor();
            cursor.setClampEnabled(config.getVideo().isFullscreen());
            Gdx.input.setCursorCatched(config.getVideo().isFullscreen());
            
            //Gdx.input.setCursorCatched(true);
            //Gdx.input.setCursorPosition(getScreenWidth()/2, getScreenHeight()/2);
            
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
        ClientMain.logSystemSpecs(console);
        ClientMain.logVideoSpecs(console);
        
        Art.load();
        Sounds.init(config);
                

        this.uiManager = new UserInterfaceManager();
        seventh.client.gfx.Cursor cursor = this.uiManager.getCursor();
        float sensitivity = config.getMouseSensitivity();
        if(sensitivity > 0) {
            cursor.setMouseSensitivity(sensitivity);
        }
        
        Gdx.input.setInputProcessor(this.inputs);
        
        initControllers();        
        videoReload();        
        
        this.menuScreen = new MenuScreen(this);
        goToMenuScreen();
    }

    /**
     * @return the menuScreen
     */
    public MenuScreen getMenuScreen() {
        return menuScreen;
    }
    
    
    /**
     * Navigates to the {@link MenuScreen}
     */
    public void goToMenuScreen() {
        setScreen(menuScreen);
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
                    Gdx.input.setCursorCatched(!terminal.toggle());                    
                    return true;
                }
                return false;
            }
        });
        
        this.inputs.addProcessor(this.terminal.getInputs());
        
        setVSync(config.getVideo().isVsync());
        
        
        
        this.canvas = new GdxCanvas();
        try {
            this.canvas.loadFont("./assets/gfx/fonts/Courier New.ttf", "Courier New");
            this.canvas.loadFont("./assets/gfx/fonts/Consola.ttf", "Consola");
            this.canvas.loadFont("./assets/gfx/fonts/Army.ttf", "Army");
            this.canvas.loadFont("./assets/gfx/fonts/Napalm Vertigo.ttf", "Napalm Vertigo");
//            this.canvas.loadFont("./assets/gfx/fonts/Bebas.ttf", "Bebas");
            this.canvas.loadFont("./assets/gfx/fonts/future.ttf", "Futurist Fixed-width");
            
            this.canvas.setDefaultFont("Courier New", 14);            
        }
        catch (IOException e) {
            Cons.println("*** Unable to load font: " + e);
        }
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        double newTime = TimeUtils.millis() / 1000.0;
        double frameTime = Math.min(newTime - currentTime, 0.25);
        
        currentTime = newTime;
        accumulator += frameTime;
        
        while(accumulator >= step) {
            timeStep.setDeltaTime(DELTA_TIME);
            timeStep.setGameClock(gameClock);
                        
            updateScreen(timeStep);
            
            accumulator -= step;
            gameClock += DELTA_TIME;
        }
        
        //avoid divided by zero
        float alpha = (float)(accumulator / step);
        
        renderScreen(canvas, alpha);                
    }
    
    /**
     * Returns the screen if the current screen was to be popped off
     * 
     * @return the previous screen if available, otherwise null
     */
    public Screen previousScreen() {
        if(this.screenStack.size()>1) {
            return this.screenStack.elementAt(this.screenStack.size() - 2);
        }
        return null;
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
     * @param screen
     * @return true if the current screen is of the supplied type
     */
    public boolean onScreen(Class<? extends Screen> screen) {
        if(!this.screenStack.empty()) {
            return this.screenStack.peek().getClass().equals(screen);
        }
        return false;
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
    public ClientConnection getClientConnection() {
        return connection;
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
    public ClientSeventhConfig getConfig() {
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
        //return Gdx.graphics.getHeight();
        return DEFAULT_MINIMIZED_SCREEN_HEIGHT;
    }
    
    /**
     * @return the screenWidth
     */
    public int getScreenWidth() {
        //return Gdx.graphics.getWidth();
        return DEFAULT_MINIMIZED_SCREEN_WIDTH;
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
    

    private void renderScreen(Canvas canvas, float alpha) {
        Screen screen = this.sm.getCurrentState();
        canvas.preRender();
        if(screen != null) {
            screen.render(canvas, alpha);
        }        
        
        if(this.terminal.isActive()) {            
            this.terminal.render(canvas, alpha);            
        }
        canvas.postRender();
    }
    
    private void updateInputs(Screen previousScreen) {
    
        if(previousScreen != null) {                        
            removeInput(previousScreen.getInputs());
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
        this.inputs.removeProcessor(inputs);
        this.inputs.addProcessor(inputs);       
    }
    
    /**
     * Adds an input listener to the front
     * @param inputs
     */
    public void addInputToFront(Inputs inputs) {
        this.inputs.removeProcessor(inputs);
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
//        setHWCursorVisible(false);
//        videoReload();
    }

    /**
     * Restarts the video 
     */
    public void restartVideo() {
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
