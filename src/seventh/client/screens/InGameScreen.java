/*
 * see license.txt 
 */
package seventh.client.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controllers;

import seventh.ai.AICommand;
import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientTeam;
import seventh.client.SeventhGame;
import seventh.client.entities.ClientPlayerEntity;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Cursor;
import seventh.client.gfx.InGameOptionsDialog;
import seventh.client.gfx.InGameOptionsDialog.OnHideListener;
import seventh.client.gfx.InGameOptionsDialogView;
import seventh.client.gfx.Theme;
import seventh.client.gfx.effects.Effects;
import seventh.client.gfx.hud.AIShortcut;
import seventh.client.gfx.hud.AIShortcuts;
import seventh.client.gfx.hud.AIShortcuts.DefendPlantedBombAIShortcut;
import seventh.client.gfx.hud.AIShortcuts.DefuseBombAIShortcut;
import seventh.client.gfx.hud.AIShortcuts.FollowMeAIShortcut;
import seventh.client.gfx.hud.AIShortcuts.MoveToAIShortcut;
import seventh.client.gfx.hud.AIShortcuts.PlantBombAIShortcut;
import seventh.client.gfx.hud.AIShortcuts.SurpressFireAIShortcut;
import seventh.client.gfx.hud.AIShortcuts.TakeCoverAIShortcut;
import seventh.client.gfx.hud.AIShortcutsMenu;
import seventh.client.inputs.ControllerInput.ControllerButtons;
import seventh.client.inputs.Inputs;
import seventh.client.inputs.JoystickGameController;
import seventh.client.inputs.KeyMap;
import seventh.client.inputs.KeyboardGameController;
import seventh.client.network.ClientConnection;
import seventh.client.network.ClientProtocol;
import seventh.client.sfx.Sounds;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.network.messages.AICommandMessage;
import seventh.network.messages.PlayerCommanderMessage;
import seventh.network.messages.PlayerInputMessage;
import seventh.network.messages.PlayerSpeechMessage;
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.RconMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.shared.Command;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.RconHash;
import seventh.shared.TimeStep;
import seventh.ui.TextBox;
import seventh.ui.UserInterfaceManager;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.view.TextBoxView;

/**
 * Represents when a player is actually playing the game.
 * 
 * @author Tony
 *
 */
public class InGameScreen implements Screen {

    public static enum Actions {
        UP(1<<0),
        DOWN(1<<1),
        LEFT(1<<2),
        RIGHT(1<<3),
        WALK(1<<4),
        FIRE(1<<5),
        RELOAD(1<<6),
        WEAPON_SWITCH_UP(1<<7),
        WEAPON_SWITCH_DOWN(1<<8),
        THROW_GRENADE(1<<9),
        
        SPRINT(1<<10),
        CROUCH(1<<11),
        
        USE(1<<12),
        DROP_WEAPON(1<<13),
        MELEE_ATTACK(1<<14),
        
        SAY(1<<15),
        TEAM_SAY(1<<16),
        
        ;
        
        private int mask;
        private Actions(int mask) {
            this.mask = mask;
        }
        
        /**
         * @return the mask
         */
        public int getMask() {
            return mask;
        }
    }
    
    private SeventhGame app;
    private ClientConnection connection;
    private ClientGame game;
    
    private Cursor cursor;
    
    private PlayerInputMessage inputMessage;
    private int inputKeys;
        
    private InGameOptionsDialog dialog;
    private InGameOptionsDialogView dialogView;
    
    private TextBox sayTxtBx;
    private TextBox teamSayTxtBx;
    
    private TextBoxView sayTxtBxView;
    private TextBoxView teamSayTxtBxView;
    
    private UserInterfaceManager uiManager;
    
    private KeyMap keyMap;
    private AIShortcuts aiShortcuts;
    private AIShortcutsMenu aiShortcutsMenu;
    
    private JoystickGameController controllerInput;    
    private KeyboardGameController inputs = new KeyboardGameController() {
        
        public boolean keyUp(int key) {
            
            if(key == Keys.ESCAPE) {
                dialogMenu();
                return true;
                
            }
            else if(key == Keys.Q) {
                if(!getDialog().isOpen()) {
                    aiShortcutsMenu.toggle();
                }
            }
            
            return super.keyUp(key);
        }
    
        
        @Override
        public boolean mouseMoved(int mx, int my) {
            
            cursor.moveTo(mx, my);
            if(!getDialog().isOpen()) {
                super.mouseMoved(mx,my);                                    
                return true;
            }
            
            return false;
        }
        
                
        @Override
        public boolean touchDragged(int x, int y, int pointer) {                        
            return mouseMoved(x,y);                
        }
        
        @Override
        public boolean scrolled(int notches) {        
            if(notches < 0) {
                inputKeys |= Actions.WEAPON_SWITCH_DOWN.getMask();
            }
            else {
                inputKeys |= Actions.WEAPON_SWITCH_UP.getMask();
            }
            return true;
        }
    };
    
    private boolean isDebugMode;
    private Effects debugEffects;
    
    
    
    /**
     * 
     */
    public InGameScreen(final SeventhGame app, final ClientGame game) {
        this.app = app;
        this.game = game;
        
        this.connection = app.getClientConnection();
        this.uiManager = app.getUiManager();
                
        this.inputMessage = new PlayerInputMessage();
        this.keyMap = app.getKeyMap();
        
        this.isDebugMode = false;
        this.debugEffects = new Effects();
        
        this.cursor = app.getUiManager().getCursor();
        
        List<AIShortcut> commands = new ArrayList<AIShortcut>();
        commands.add(new FollowMeAIShortcut(Keys.P));
        commands.add(new SurpressFireAIShortcut(Keys.O));
        commands.add(new MoveToAIShortcut(Keys.I));
        commands.add(new PlantBombAIShortcut(Keys.J));
        commands.add(new DefuseBombAIShortcut(Keys.K));
        commands.add(new DefendPlantedBombAIShortcut(Keys.L));
        commands.add(new TakeCoverAIShortcut(Keys.U));
        
        this.aiShortcuts = new AIShortcuts(this.keyMap, commands, commands.get(2), commands.get(1));
        this.aiShortcutsMenu = new AIShortcutsMenu(game, keyMap, aiShortcuts);
        
        createUI();
                                
        this.controllerInput = new JoystickGameController();
        
    }
    
    
    /**
     * @return the dialog
     */
    protected InGameOptionsDialog getDialog() {
        return dialog;
    }
    
    /**
     * @return the sayTxtBx
     */
    protected TextBox getSayTxtBx() {
        return sayTxtBx;
    }
    
    /**
     * @return the teamSayTxtBx
     */
    protected TextBox getTeamSayTxtBx() {
        return teamSayTxtBx;
    }
    
    private void createUI() {
        if(this.dialog!=null) {
            this.dialog.destroy();
        }
        
        this.aiShortcutsMenu.hide();
        
        this.dialog = new InGameOptionsDialog(app.getConsole(), connection, app.getTheme());
        this.dialog.setOnHide(new OnHideListener() {
            
            @Override
            public void onShow() {
                game.activateCamera(false);
            }
            
            @Override
            public void onHide() {
                game.activateCamera(true);
            }
        });                
        
        ClientPlayer player = game.getLocalPlayer();
        if(player!=null) {
            this.dialog.setTeam(player.getTeam());
        }
        
        this.dialog.show();
        
        this.dialog.setBounds(new Rectangle(0,0, 400, 380));
        this.dialog.getBounds().centerAround(new Vector2f(app.getScreenWidth()/2, app.getScreenHeight()/2));
        this.dialog.getLeaveGameBtn().addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {                
                connection.disconnect();
                app.getConsole().execute("kill_local_server");

                app.goToMenuScreen();
            }
        });
        this.dialog.getOptions().addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                app.pushScreen(new OptionsScreen(app));
            }
        });
        
        this.dialogView = new InGameOptionsDialogView(dialog);
        
        if(this.sayTxtBx != null) {
            this.sayTxtBx.destroy();
        }
        this.sayTxtBx = new TextBox();
        this.sayTxtBx.setLabelText("Say:");
        this.sayTxtBxView = new TextBoxView(sayTxtBx);
        setupTextbox(sayTxtBx);
        
        if(this.teamSayTxtBx!=null) {
            this.teamSayTxtBx.destroy();
        }
        this.teamSayTxtBx = new TextBox();
        this.teamSayTxtBx.setLabelText("Team Say:");
        this.teamSayTxtBxView = new TextBoxView(teamSayTxtBx);
        setupTextbox(teamSayTxtBx);
        
        this.app.addInputToFront(app.getUiManager());
    }
    
    private void dialogMenu() {
        if(getDialog().isOpen()) {                
            app.removeInput(app.getUiManager());
            getDialog().close();
            Sounds.playGlobalSound(Sounds.uiNavigate);
        }
        else if(!getSayTxtBx().isDisabled()) {
            hideTextBox(getSayTxtBx());
        }
        else if(!getTeamSayTxtBx().isDisabled()) {                    
            hideTextBox(getTeamSayTxtBx());
        }
        else {                    
            createUI();                    
            Sounds.playGlobalSound(Sounds.uiNavigate);
        }
    }
    
    private void showTextBox(TextBox box) {
        box.show();
        inputs.clearKeys();
        inputs.clearButtons();
        this.app.addInputToFront(app.getUiManager());
    }
    
    private void hideTextBox(TextBox box) {
        box.hide();
        
        app.removeInput(app.getUiManager());
    }
    
    private void setupTextbox(final TextBox box) {
        box.setBounds(new Rectangle(150, app.getScreenHeight() - 200, app.getScreenWidth() - 275, 35));
        box.setFont(Theme.DEFAULT_FONT);
        box.setTextSize(20f);
        box.setMaxSize(60);
        box.hide();
        
        box.addInputListenerToFront(new Inputs() {        
            
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {                
                return true;
            }
            
            @Override
            public boolean keyUp(int key) {
                if(key == Keys.ESCAPE) {
                    if(!getSayTxtBx().isDisabled()) {
                        hideTextBox(getSayTxtBx());
                    }
                    else if(!getTeamSayTxtBx().isDisabled()) {                    
                        hideTextBox(getTeamSayTxtBx());
                    }
                    
                    return true;
                    
                }
                else if(key == Keys.ENTER) {
                    
                    if(!getSayTxtBx().isDisabled()) {
                        hideTextBox(getSayTxtBx());
                        app.getConsole().execute("say " + box.getText());                        
                    }
                    else if(!getTeamSayTxtBx().isDisabled()) {                    
                        hideTextBox(getTeamSayTxtBx());                        
                        app.getConsole().execute("team_say " + box.getText());
                        
                    }
                    box.setText("");
                    return true;
                }
                return super.keyUp(key);
            }
        });
    }
    
    /* (non-Javadoc)
     * @see palisma.shared.State#enter()
     */
    @Override
    public void enter() {
        if(this.game != null) {
            this.game.onReloadVideo();
        }
        
        if(!this.dialog.isOpen()) {
            game.activateCamera(true);
        }
        
        Controllers.addListener(this.controllerInput);
        final ClientProtocol protocol = connection.getClientProtocol();
        
        Console console = app.getConsole();
        console.addCommand(new Command("ai") {
            
            @Override
            public void execute(Console console, String... args) {
                if(args.length > 1) {                                        
                    AICommandMessage msg = new AICommandMessage();
                    msg.botId = Integer.parseInt(args[0]);
                    msg.command = new AICommand(this.mergeArgsDelimAt(",", 1, args));
                    protocol.sendAICommandMessage(msg);    
                }
                else {
                    console.println("<usage> ai [botid] [command] [args]");
                }
                
            }
        });
        
        console.addCommand(new Command("disconnect") {            
            @Override
            public void execute(Console console, String... args) {
                connection.disconnect();
                app.goToMenuScreen();
            }
        });
        
        console.addCommand(new Command("say"){
            
            @Override
            public void execute(Console console, String... args) {
                TextMessage msg = new TextMessage();
                msg.message = mergeArgsDelim(" ", args);
                msg.playerId = game.getLocalPlayer().getId();
                
                protocol.sendTextMessage(msg);
            }
        });
        
        
        console.addCommand(new Command("team_say"){
            
            @Override
            public void execute(Console console, String... args) {
                TeamTextMessage msg = new TeamTextMessage();
                msg.message = mergeArgsDelim(" ", args);
                msg.playerId = game.getLocalPlayer().getId();
                                
                protocol.sendTeamTextMessage(msg);
            }
        });
        
        console.addCommand(new Command("speech") {
            
            @Override
            public void execute(Console console, String... args) {
                if(args.length < 1) {
                    console.println("<usage> speech [speech id]");
                }
                else {
                    PlayerSpeechMessage msg = new PlayerSpeechMessage();
                    ClientPlayer player = game.getLocalPlayer();
                    if(player.isAlive()) {
                        msg.playerId = player.getId();
                        ClientPlayerEntity entity = player.getEntity();
                        if(entity!=null) {
                            Vector2f pos = entity.getCenterPos();
                            msg.posX = (short)pos.x;
                            msg.posY = (short)pos.y;
                            msg.speechCommand = Byte.parseByte(args[0]);
                            
                            Sounds.playSpeechSound(player.getTeam().getId(), msg.speechCommand, pos.x, pos.y);
                            protocol.sendPlayerSpeechMessage(msg);
                        }
                    }
                }
            }
        });
        
        console.addCommand(new Command("change_team"){
            
            @Override
            public void execute(Console console, String... args) {
                
                if(args.length < 1) {
                    console.println("<usage> change_team [allies|axis|spectator]");
                }
                else {
                    PlayerSwitchTeamMessage msg = new PlayerSwitchTeamMessage();
                    msg.playerId = game.getLocalPlayer().getId();
                    String team = args[0];
                    if(team.toLowerCase().equals("allies")) {
                        msg.teamId = (byte)ClientTeam.ALLIES.getId();
                    }
                    else if(team.toLowerCase().equals("axis")) {
                        msg.teamId = (byte)ClientTeam.AXIS.getId();
                    }
                    else {                        
                        msg.teamId = (byte)ClientTeam.NONE.getId();                        
                    }
                    
                    protocol.sendPlayerSwitchTeamMessage(msg);
                }
            }
        });
        
        console.addCommand(new Command("rcon") {

            @Override
            public void execute(Console console, String... args) {
                if(args.length > 0) {
                    String msg = null;
                    
                    if(args[0].equals("password")) {
                        if(args.length > 1) {
                        
                            RconHash hash = new RconHash(game.getLocalSession().getRconToken());
                            msg = "password " + hash.hash(mergeArgsDelimAt(" ", 1, args).trim());
                        }
                        else {
                            console.println("rcon password [value]");
                        }
                    }
                    else {
                        msg = this.mergeArgsDelim(" ", args);
                    }
                                                
                    if(msg != null) {                        
                        protocol.sendRconMessage(new RconMessage(msg));
                    }
                }
            }
        });
        
        console.addCommand(new Command("commander") {
            
            @Override
            public void execute(Console console, String... args) {
                PlayerCommanderMessage msg = new PlayerCommanderMessage();
                ClientPlayer localPlayer = game.getLocalPlayer();
                msg.playerId = localPlayer.getId();
                msg.isCommander = !localPlayer.isCommander();
                protocol.sendPlayerCommanderMessage(msg);
            }
        });
    }
    
    /* (non-Javadoc)
     * @see palisma.shared.State#exit()
     */
    @Override
    public void exit() {
        Console console = app.getConsole();
        
        console.removeCommand("disconnect");        
        console.removeCommand("say");                
        console.removeCommand("team_say");        
        console.removeCommand("change_team");
        console.removeCommand("rcon");
        console.removeCommand("ai");
        
        Controllers.removeListener(this.controllerInput);
        
        this.dialog.destroy();
        this.sayTxtBx.destroy();
        this.teamSayTxtBx.destroy();
    }

    
    /* (non-Javadoc)
     * @see palisma.shared.State#update(leola.live.TimeStep)
     */    
    @Override
    public void update(TimeStep timeStep) {
        game.showScoreBoard(inputs.isKeyDown(Keys.TAB)||
                            controllerInput.isButtonDown(ControllerButtons.SELECT_BTN));
        
        this.sayTxtBxView.update(timeStep);
        this.teamSayTxtBxView.update(timeStep);
        
        this.cursor.update(timeStep);
        
        // TODO: Remove from final build, this enables
        // DEBUG mode while LEFT_ALT is pressed
        isDebugMode = inputs.isKeyDown(Keys.ALT_LEFT);
        
        if(!dialog.isOpen() && (sayTxtBx.isDisabled()&&teamSayTxtBx.isDisabled()) ) {
            if(controllerInput.isButtonReleased(ControllerButtons.START_BTN)) {
                dialogMenu();
            }
            
            
            inputKeys = controllerInput.pollInputs(timeStep, keyMap, cursor, inputKeys);
            inputKeys = inputs.pollInputs(timeStep, keyMap, cursor, inputKeys);
                    
            if(inputs.isKeyDown(keyMap.getSayKey())) {
                showTextBox(sayTxtBx);
            }                        
            else if(inputs.isKeyDown(keyMap.getTeamSayKey())) {
                showTextBox(teamSayTxtBx);
            }
            
            /* AI command shortcuts, the player can issue AI commands to nearby
             * Bots */
            if(this.aiShortcutsMenu.isShowing()) {
                if( this.aiShortcuts.checkShortcuts(inputs, app.getConsole(), game) ) {
                    this.aiShortcutsMenu.hide();
                }
            }
        }
        else {            
            this.uiManager.update(timeStep);
            this.uiManager.checkIfCursorIsHovering();
            this.dialogView.update(timeStep);
            
            inputKeys = 0;
        }
            
        /* capture the inputs for moving the camera if
         * we are in freeform mode
         */
        if(!game.isFreeformCamera()) {
            inputMessage.keys = inputKeys;
        }
        
        Vector2f mousePos = cursor.getCursorPos();
        inputMessage.orientation = game.calcPlayerOrientation(mousePos.x, mousePos.y);                 
        connection.getClientProtocol().sendPlayerInputMessage(inputMessage);
        connection.updateNetwork(timeStep);
                        
        game.update(timeStep);
        game.applyPlayerInput(mousePos.x, mousePos.y, inputKeys);
                
        inputKeys = 0;
        
        if( isDebugMode ) {
            debugEffects.update(timeStep);
        }
        
    }

    /* (non-Javadoc)
     * @see palisma.client.Screen#destroy()
     */
    @Override
    public void destroy() {
        Cons.println("Closing down the network connection...");
        connection.disconnect();
    }

    /* (non-Javadoc)
     * @see palisma.client.Screen#render(leola.live.gfx.Canvas)
     */
    @Override
    public void render(Canvas canvas, float alpha) {
                        
        game.render(canvas, alpha);
                        
        Camera camera = game.getCamera();
        if( isDebugMode ) {            
            debugEffects.render(canvas, camera, alpha);
        }
        
        
        this.dialogView.render(canvas, camera, alpha);
        
        this.sayTxtBxView.render(canvas, camera, alpha);
        this.teamSayTxtBxView.render(canvas, camera, alpha);
        
        this.aiShortcutsMenu.render(canvas, camera, alpha);
        
        this.cursor.render(canvas);    
        
        if(isDebugMode) {
            String message = "" + game.screenToWorldCoordinates(cursor.getX(), cursor.getY());
            int len = canvas.getWidth(message);
            canvas.drawString(message, cursor.getX() - len/2, cursor.getY() + 50, 0xffffffff);
        }
    }

    /* (non-Javadoc)
     * @see palisma.client.Screen#getInputs()
     */
    @Override
    public Inputs getInputs() {
        return this.inputs;
    }

}
