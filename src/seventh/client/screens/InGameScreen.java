/*
 * see license.txt 
 */
package seventh.client.screens;

import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientPlayerEntity;
import seventh.client.ClientTeam;
import seventh.client.ControllerInput;
import seventh.client.Inputs;
import seventh.client.KeyMap;
import seventh.client.Network;
import seventh.client.Screen;
import seventh.client.SeventhGame;
import seventh.client.gfx.Art;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Cursor;
import seventh.client.gfx.InGameOptionsDialog;
import seventh.client.gfx.InGameOptionsDialogView;
import seventh.client.gfx.Theme;
import seventh.client.gfx.particle.DebugAnimationEffect;
import seventh.client.gfx.particle.DebugSpriteEffect;
import seventh.client.gfx.particle.Effects;
import seventh.client.sfx.Sounds;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.RconMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.network.messages.UserInputMessage;
import seventh.shared.Command;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.RconHash;
import seventh.shared.TimeStep;
import seventh.ui.TextBox;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.view.TextBoxView;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;

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
	private Network network;
	private ClientGame game;
	
	private Cursor cursor;
	
	private UserInputMessage inputMessage;
	private int inputKeys;
		
	private InGameOptionsDialog dialog;
	private InGameOptionsDialogView dialogView;
	
	private TextBox sayTxtBx;
	private TextBox teamSayTxtBx;
	
	private TextBoxView sayTxtBxView;
	private TextBoxView teamSayTxtBxView;
	
	private KeyMap keyMap;
		
	private ControllerInput controllerInput;	
	private Inputs inputs = new Inputs() {		
		public boolean keyUp(int key) {
			if(key == Keys.ESCAPE) {
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

				return true;
				
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
	
	private float[] movements;
	
	/**
	 * 
	 */
	public InGameScreen(final SeventhGame app, final Network network, final ClientGame game) {
		this.app = app;
		this.network = network;
		this.game = game;
				
		this.inputMessage = new UserInputMessage();
		this.keyMap = app.getKeyMap();
		
		this.isDebugMode = false;
		this.debugEffects = new Effects();
		
		this.cursor = app.getUiManager().getCursor();
		
		createUI();
				
		this.movements = new float[4];
		
		this.controllerInput = new ControllerInput() {
			/* (non-Javadoc)
			 * @see seventh.client.ControllerInput#buttonUp(com.badlogic.gdx.controllers.Controller, int)
			 */
			@Override
			public boolean buttonUp(Controller controller, int button) {
				super.buttonUp(controller, button);
				if(button == 3) {
					inputKeys |= Actions.WEAPON_SWITCH_UP.getMask();
				}
				return true;
			}
			
			@Override
			public boolean axisMoved(Controller controller, int axisCode, float value) {			
				super.axisMoved(controller, axisCode, value);			
				if(axisCode<4) {
					movements[axisCode] = value;
				}
							
				return true;
			}
		};		
		Controllers.addListener(this.controllerInput);				
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
		
		this.dialog = new InGameOptionsDialog(app.getConsole(), network, app.getTheme());		
		ClientPlayer player = game.getLocalPlayer();
		if(player!=null) {
			this.dialog.setTeam(player.getTeam());
		}
		this.dialog.setBounds(new Rectangle(0,0, 400, 380));
		this.dialog.getBounds().centerAround(new Vector2f(app.getScreenWidth()/2, app.getScreenHeight()/2));
		this.dialog.getLeaveGameBtn().addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {				
				network.disconnect();
				app.getConsole().execute("kill_local_server");

				app.setScreen(new MenuScreen(app));
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
	
	/**
	 * @return the app
	 */
	public SeventhGame getApp() {
		return app;
	}

	/* (non-Javadoc)
	 * @see palisma.shared.State#enter()
	 */
	@Override
	public void enter() {
		Console console = app.getConsole();
		console.addCommand(new Command("ai") {
			
			@Override
			public void execute(Console console, String... args) {
				if(args.length > 1) {
										
//					AICommandMessage msg = new AICommandMessage();
//					msg.botId = Integer.parseInt(args[0]);
//					String cmd = args[1];
//					if("plant".equalsIgnoreCase(cmd)) {
//						msg.command = new PlantBombAICommand();
//					}
//					else if("defuse".equalsIgnoreCase(cmd)) {
//						msg.command = new DefuseBombAICommand();
//					}
//					else if("defend".equalsIgnoreCase(cmd)) {
//						ClientPlayer player = game.getLocalPlayer();
//						Vector2f pos = player.isAlive() ? player.getEntity().getCenterPos().createClone() : new Vector2f();
//						if(args.length > 2) {
//							pos.x = Integer.parseInt(args[2]);
//							pos.y = Integer.parseInt(args[3]);
//						}
//						msg.command = new DefendAICommand(pos);
//					}
//					else {
//						msg.command = new DefaultAICommand();
//					}
//					network.queueSendReliableMessage(msg);	
				}
				else {
					console.println("<usage> ai [botid] [command] [args]");
				}
				
			}
		});
		
		console.addCommand(new Command("disconnect") {			
			@Override
			public void execute(Console console, String... args) {
				network.disconnect();
				app.setScreen(new MenuScreen(app));
			}
		});
		
		console.addCommand(new Command("say"){
			/* (non-Javadoc)
			 * @see seventh.shared.Command#execute(seventh.shared.Console, java.lang.String[])
			 */
			@Override
			public void execute(Console console, String... args) {
				TextMessage msg = new TextMessage();
				msg.message = mergeArgsDelim(" ", args);
				msg.playerId = game.getLocalPlayer().getId();
				
				network.queueSendReliableMessage(msg);
			}
		});
		
		
		console.addCommand(new Command("team_say"){
			/* (non-Javadoc)
			 * @see seventh.shared.Command#execute(seventh.shared.Console, java.lang.String[])
			 */
			@Override
			public void execute(Console console, String... args) {
				TeamTextMessage msg = new TeamTextMessage();
				msg.message = mergeArgsDelim(" ", args);
				msg.playerId = game.getLocalPlayer().getId();
								
				network.queueSendReliableMessage(msg);
			}
		});
		
		console.addCommand(new Command("change_team"){
			/* (non-Javadoc)
			 * @see seventh.shared.Command#execute(seventh.shared.Console, java.lang.String[])
			 */
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
					
					network.queueSendReliableMessage(msg);
				}
			}
		});
		
		console.addCommand(new Command("rcon") {
			/* (non-Javadoc)
			 * @see seventh.shared.Command#execute(seventh.shared.Console, java.lang.String[])
			 */
			@Override
			public void execute(Console console, String... args) {
				if(args.length > 0) {
					String msg = null;
					
					if(args[0].equals("password")) {
						if(args.length > 1) {
						
							RconHash hash = new RconHash(game.getRconToken());
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
						network.queueSendReliableMessage(new RconMessage(msg));
					}
				}
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

	private void debugReloadPlayerModel() {
		ClientPlayer player = game.getLocalPlayer();
		if(player != null && player.isAlive()) {
			ClientPlayerEntity ent = player.getEntity();
			ent.setPlayer(player);
			ent.changeTeam(ent.getTeam());
		}
	}
	
	/* (non-Javadoc)
	 * @see palisma.shared.State#update(leola.live.TimeStep)
	 */	
	@Override
	public void update(TimeStep timeStep) {
		game.showScoreBoard(inputs.isKeyDown(Keys.TAB));
		
		this.sayTxtBxView.update(timeStep);
		this.teamSayTxtBxView.update(timeStep);
		
		// DEBUG
		if(inputs.isKeyDown(Keys.ALT_LEFT)) {
			
			if( inputs.isKeyDown(Keys.P)) {
				Art.reload();
				debugReloadPlayerModel();
				game.debugReloadGfx();
			}
			
			isDebugMode = true;
			
			
			if(this.debugEffects.size() < 5) {
				this.debugEffects.clearEffects();
				this.debugEffects.addEffect(new DebugAnimationEffect(Art.newAlliedBackDeathAnim(), new Vector2f(88, 92), -90, false));
				this.debugEffects.addEffect(new DebugAnimationEffect(Art.newAlliedFrontDeathAnim(), new Vector2f(188, 92), -90, false));
								
				this.debugEffects.addEffect(new DebugAnimationEffect(Art.newAxisBackDeathAnim(), new Vector2f(88, 242), -90, false));
				this.debugEffects.addEffect(new DebugAnimationEffect(Art.newAxisFrontDeathAnim(), new Vector2f(188, 242), -90, false));
				

				this.debugEffects.addEffect(new DebugSpriteEffect(Art.alliedCharacterModel.getFrame(1), new Vector2f(80,80), 90, 0x6fffffff));
				this.debugEffects.addEffect(new DebugSpriteEffect(Art.alliedCharacterModel.getFrame(1), new Vector2f(180,80), 90, 0x6fffffff));
				
				this.debugEffects.addEffect(new DebugSpriteEffect(Art.axisCharacterModel.getFrame(1), new Vector2f(80,230), 90, 0x6fffffff));
				this.debugEffects.addEffect(new DebugSpriteEffect(Art.axisCharacterModel.getFrame(1), new Vector2f(180,230), 90, 0x6fffffff));
			}
		}
		else isDebugMode = false;
		// ~~~~
		
		if(!dialog.isOpen() && (sayTxtBx.isDisabled()&&teamSayTxtBx.isDisabled()) ) {
		
			if(controllerInput.isConnected()) {
				float sensitivity = 0.2f;
				
				if(movements[0] > sensitivity) {
					inputKeys |= Actions.DOWN.getMask();
				}
				if(movements[0] < -sensitivity) {
					inputKeys |= Actions.UP.getMask();
				}
				
				if(movements[1] > sensitivity) {
					inputKeys |= Actions.RIGHT.getMask();
				}
				if(movements[1] < -sensitivity) {
					inputKeys |= Actions.LEFT.getMask();
				}
				
				sensitivity = 0.3f;
				
				float dx = movements[3];
				float dy = movements[2];
				
				if( Math.abs(dx) > sensitivity || Math.abs(dy) > sensitivity) {
					cursor.moveByDelta(dx, dy);
				}
				
//				for(int i = 0; i < movements.length;i++) {
//					movements[i] = false;
//				}
				
				if(controllerInput.isRightTriggerDown()) {
					inputKeys |= Actions.FIRE.getMask();
				}
				if(controllerInput.isLeftTriggerDown()) {
					inputKeys |= Actions.THROW_GRENADE.getMask();
				}
				if(controllerInput.isButtonDown(2)) {
					inputKeys |= Actions.RELOAD.getMask();
				}
//				if(controllerInput.isButtonDown(3)) {
//					inputKeys |= Actions.WEAPON_SWITCH_UP.getMask();
//				}
				if(controllerInput.isButtonDown(1)||controllerInput.isButtonDown(5)) {
					inputKeys |= Actions.MELEE_ATTACK.getMask();
				}
				if(controllerInput.isButtonDown(4)) {
					inputKeys |= Actions.CROUCH.getMask();
				}
				
				
				if(controllerInput.isPovDirectionDown(PovDirection.north)) {
					inputKeys |= Actions.UP.getMask();
				}
				else if(controllerInput.isPovDirectionDown(PovDirection.northEast)) {
					inputKeys |= Actions.UP.getMask();
					inputKeys |= Actions.RIGHT.getMask();
				}
				
				else if(controllerInput.isPovDirectionDown(PovDirection.northWest)) {
					inputKeys |= Actions.UP.getMask();
					inputKeys |= Actions.LEFT.getMask();
				}
				
				else if(controllerInput.isPovDirectionDown(PovDirection.south)) {
					inputKeys |= Actions.DOWN.getMask();
				}
				else if(controllerInput.isPovDirectionDown(PovDirection.southEast)) {
					inputKeys |= Actions.DOWN.getMask();
					inputKeys |= Actions.RIGHT.getMask();
				}
				else if(controllerInput.isPovDirectionDown(PovDirection.southWest)) {
					inputKeys |= Actions.DOWN.getMask();
					inputKeys |= Actions.LEFT.getMask();
				}
				else if(controllerInput.isPovDirectionDown(PovDirection.east)) {					
					inputKeys |= Actions.RIGHT.getMask();
				}
				else if(controllerInput.isPovDirectionDown(PovDirection.west)) {					
					inputKeys |= Actions.LEFT.getMask();
				}
			}
			
			if(inputs.isKeyDown(keyMap.getSayKey())) {
				showTextBox(sayTxtBx);
			}						
			else if(inputs.isKeyDown(keyMap.getTeamSayKey())) {
				showTextBox(teamSayTxtBx);
			}
			
			if(inputs.isKeyDown(keyMap.getWalkKey())) {
				inputKeys |= Actions.WALK.getMask();
			}
			
			if(inputs.isKeyDown(keyMap.getCrouchKey())) {
				inputKeys |= Actions.CROUCH.getMask();
			}
			
			if(inputs.isKeyDown(keyMap.getSprintKey())) {
				inputKeys |= Actions.SPRINT.getMask();
			}
			
			if(inputs.isKeyDown(keyMap.getUseKey())) {
				inputKeys |= Actions.USE.getMask();
			}
			
			if(inputs.isKeyDown(keyMap.getDropWeaponKey())) {
				inputKeys |= Actions.DROP_WEAPON.getMask();
			}
			
			if(inputs.isKeyDown(keyMap.getMeleeAttack())) {
				inputKeys |= Actions.MELEE_ATTACK.getMask();
			}
			
			if(inputs.isKeyDown(keyMap.getReloadKey()) ) {
				inputKeys |= Actions.RELOAD.getMask();
			}
			
			if(inputs.isKeyDown(keyMap.getUpKey())) {
				inputKeys |= Actions.UP.getMask();
			}
			else if(inputs.isKeyDown(keyMap.getDownKey())) {
				inputKeys |= Actions.DOWN.getMask();
			}
			
			if(inputs.isKeyDown(keyMap.getLeftKey())) {
				inputKeys |= Actions.LEFT.getMask();
			}
			else if(inputs.isKeyDown(keyMap.getRightKey())) {
				inputKeys |= Actions.RIGHT.getMask();
			}
			
			if(inputs.isButtonDown(keyMap.getFireKey()) || inputs.isKeyDown(keyMap.getFireKey()) ) {
				inputKeys |= Actions.FIRE.getMask();
			}
			
			if(inputs.isButtonDown(keyMap.getThrowGrenadeKey()) || inputs.isKeyDown(keyMap.getThrowGrenadeKey())) {
				inputKeys |= Actions.THROW_GRENADE.getMask();
			}
		}
				
		inputMessage.keys = inputKeys;
		
		Vector2f mousePos = cursor.getCursorPos();
		inputMessage.orientation = game.calcPlayerOrientation(mousePos.x, mousePos.y); 		
		network.sendUnReliableMessage(inputMessage);		
		network.updateNetwork(timeStep);
						
		game.update(timeStep);
		game.applyPlayerInput(inputKeys);
		
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
		network.disconnect();
	}

	/* (non-Javadoc)
	 * @see palisma.client.Screen#render(leola.live.gfx.Canvas)
	 */
	@Override
	public void render(Canvas canvas) {
						
		game.render(canvas);
						
		if( isDebugMode ) {			
			debugEffects.render(canvas, game.getCamera(), 0);
		}
		
		
		this.dialogView.render(canvas, game.getCamera(), 0);
		
		this.sayTxtBxView.render(canvas, game.getCamera(), 0);
		this.teamSayTxtBxView.render(canvas, game.getCamera(), 0);
		
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
