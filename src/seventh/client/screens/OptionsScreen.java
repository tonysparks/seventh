/*
 * see license.txt 
 */
package seventh.client.screens;

import java.io.IOException;

import seventh.client.Inputs;
import seventh.client.KeyMap;
import seventh.client.Screen;
import seventh.client.SeventhGame;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Cursor;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Renderable;
import seventh.client.gfx.Theme;
import seventh.client.sfx.Sounds;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Cons;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.ui.Button;
import seventh.ui.KeyInput;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.Panel;
import seventh.ui.TextBox;
import seventh.ui.UserInterfaceManager;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.view.ButtonView;
import seventh.ui.view.LabelView;
import seventh.ui.view.PanelView;
import seventh.ui.view.TextBoxView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;

/**
 * Displays the players options
 * 
 * @author Tony
 *
 */
public class OptionsScreen implements Screen {
	
	
	private SeventhGame app;
	
	private UserInterfaceManager uiManager;
	
	private Theme theme;
			
	private Timer flashText, flashDelay;
	
	private KeyInput keyInput;
	
	private Panel optionsPanel;	
	private PanelView<Renderable> panelView;
	
	private TextBox nameTxtBox;
	
	private Label keyOverwriteLbl;
	
	private int isKeyModifyOn;
	
	private DisplayMode mode;
	private boolean isFullscreen;
	private int displayModeIndex;
			
	/**
	 * 
	 */
	public OptionsScreen(SeventhGame app) {
		this.app = app;
		this.theme = app.getTheme();				
		this.uiManager = app.getUiManager();			
		
		this.flashText = new Timer(true, 1000);		
		this.flashDelay = new Timer(false, 800);
		
		this.displayModeIndex = 0;
		
		createUI();
	}
	
	
	private void createUI() {
		this.panelView = new PanelView<>();
		this.optionsPanel = new Panel();
		
		this.keyInput = new KeyInput();
		this.keyInput.setDisabled(true);
		this.optionsPanel.addWidget(keyInput);
		
		this.isFullscreen = app.isFullscreen();
		
		Vector2f uiPos = new Vector2f(200, app.getScreenHeight() - 30);
		
		Button saveBtn = setupButton(uiPos, "Save", false);
		saveBtn.getBounds().setSize(140, 80);
		saveBtn.getTextLabel().setFont(theme.getPrimaryFontName());
		saveBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				try {
					if(isFullscreen != app.isFullscreen()) {
						app.getConfig().set(isFullscreen, "video", "fullscreen");
						
						if(!isFullscreen) {
							app.getConfig().set(SeventhGame.DEFAULT_MINIMIZED_SCREEN_WIDTH, "video", "width");
							app.getConfig().set(SeventhGame.DEFAULT_MINIMIZED_SCREEN_HEIGHT, "video", "height");						
							Gdx.graphics.setDisplayMode(SeventhGame.DEFAULT_MINIMIZED_SCREEN_WIDTH, SeventhGame.DEFAULT_MINIMIZED_SCREEN_HEIGHT, isFullscreen);							
						}
						else if (mode!=null) {
							app.getConfig().set(mode.width, "video", "width");
							app.getConfig().set(mode.height, "video", "height");
							Gdx.graphics.setDisplayMode(mode.width, mode.height, true);		
						}
						else {														
							Gdx.graphics.setDisplayMode(app.getScreenWidth(), app.getScreenHeight(), true);
						}
						app.restartVideo();
					}
					else if(mode!=null) {
						if(app.isFullscreen()) {
							app.getConfig().set(mode.width, "video", "width");
							app.getConfig().set(mode.height, "video", "height");
							Gdx.graphics.setDisplayMode(mode.width, mode.height, true);
							app.restartVideo();
						}
					}
					
					if(nameTxtBox!=null) {
						String name = nameTxtBox.getText();
						String cfgName = app.getConfig().getString("name"); 
						if(name != null && cfgName != null) {
							if(!name.equals(cfgName)) {
								app.getConfig().set(name, "name");
							}
						}
					}
					
					app.getConfig().set(uiManager.getCursor().getMouseSensitivity(), "mouse_sensitivity");
					
					app.getConfig().save();
				} catch (IOException e) {
					Cons.println("Unable to save the configuration file:");
					Cons.println(e);
					
					app.getTerminal().open();
				}
				app.popScreen();
				Sounds.playGlobalSound(Sounds.uiNavigate);
			}
		});
		
		uiPos.x = app.getScreenWidth() - 80;

		Button cancelBtn = setupButton(uiPos, "Cancel", false);
		cancelBtn.getBounds().setSize(140, 80);
		cancelBtn.getTextLabel().setFont(theme.getPrimaryFontName());
		cancelBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.popScreen();
				Sounds.playGlobalSound(Sounds.uiNavigate);
			}
		});
		
		
		nameTxtBox = new TextBox();
		nameTxtBox.setLabelText("Name: ");
		nameTxtBox.setTheme(theme);
		nameTxtBox.setBounds(new Rectangle(app.getScreenWidth()/2 - 50, 160, 200, 30));		
		nameTxtBox.setFont(theme.getSecondaryFontName()); 
		nameTxtBox.setTextSize(18);
		nameTxtBox.getLabel().setTextSize(24);
		nameTxtBox.setFocus(false);
		nameTxtBox.setMaxSize(16);
		nameTxtBox.setText(app.getConfig().getString("name"));
		nameTxtBox.getTextLabel().setForegroundColor(0xffffffff);
		
		this.optionsPanel.addWidget(nameTxtBox);
		
		// ~~~ Controls
		
		KeyMap keys = app.getKeyMap();
				
		Label controlsLbl = new Label("Controls");
		controlsLbl.setTheme(theme);
		controlsLbl.setBounds(new Rectangle(10, 200, 400, 40));
		controlsLbl.setTextAlignment(TextAlignment.LEFT);
		controlsLbl.setFont(theme.getPrimaryFontName());
		controlsLbl.setTextSize(18);
		
		
		final int startX = 230;
		final int startY = 270;
		final int yInc = 30;
		
		uiPos.x = startX;
		uiPos.y = startY;		
		setupButton(uiPos, "up: '" + keys.keyString(keys.getUpKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "down: '" + keys.keyString(keys.getDownKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "left: '" + keys.keyString(keys.getLeftKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "right: '" + keys.keyString(keys.getRightKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "walk: '" + keys.keyString(keys.getWalkKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "sprint: '" + keys.keyString(keys.getSprintKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "crouch: '" + keys.keyString(keys.getCrouchKey())+"'"); uiPos.y += yInc;
		
		uiPos.x = app.getScreenWidth() / 2 + 80;
		uiPos.y = startY;
		setupButton(uiPos, "reload: '" + keys.keyString(keys.getReloadKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "drop weapon: '" + keys.keyString(keys.getDropWeaponKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "melee attack: '" + keys.keyString(keys.getMeleeAttack())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "throw grenade: '" + keys.keyString(keys.getThrowGrenadeKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "fire: '" + keys.keyString(keys.getFireKey())+"'"); uiPos.y += yInc;
		setupButton(uiPos, "use: '" + keys.keyString(keys.getUseKey()) +"'" ); uiPos.y += yInc;
		
		
		uiPos.y += yInc;
		
		Label mouseSensitivityLbl = new Label("Mouse Sensitivity: ");
		final Label mouseSensitivityValueLbl = new Label("Mouse Sensitivity: ");
		
//		mouseSensitivityLbl.setTheme(theme);
		mouseSensitivityLbl.setBounds(new Rectangle(80, 20));
		mouseSensitivityLbl.getBounds().x = 110;
		mouseSensitivityLbl.getBounds().y = (int)uiPos.y;
		mouseSensitivityLbl.setTextAlignment(TextAlignment.LEFT);
		mouseSensitivityLbl.setFont(theme.getSecondaryFontName());
		mouseSensitivityLbl.setTextSize(18);
		mouseSensitivityLbl.setForegroundColor(0xffffffff);
		
		optionsPanel.addWidget(mouseSensitivityLbl);
		panelView.addElement(new LabelView(mouseSensitivityLbl));
		
		uiPos.x = 420;
		uiPos.y += 10;
		Button mouseMinusBtn = setupButton(uiPos, "-", false );
		mouseMinusBtn.getBounds().width = 50;
		mouseMinusBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
				
				@Override
				public void onButtonClicked(ButtonEvent event) {
					Cursor cursor = uiManager.getCursor();
					float sensitivity = cursor.getMouseSensitivity();
					sensitivity -= 0.01f;
					if(sensitivity<=0.05f) {
						sensitivity=0.05f;
					}
					
					cursor.setMouseSensitivity(sensitivity);
					mouseSensitivityValueLbl.setText( (int)(sensitivity*100) + "");
				}
			});
		
		
		uiPos.x -= 90;
		uiPos.y -= 10;
		mouseSensitivityValueLbl.setBounds(new Rectangle(80, 20));
		mouseSensitivityValueLbl.getBounds().x = (int)uiPos.x;
		mouseSensitivityValueLbl.getBounds().y = (int)uiPos.y;
		mouseSensitivityValueLbl.setTextAlignment(TextAlignment.LEFT);
		mouseSensitivityValueLbl.setFont(theme.getSecondaryFontName());
		mouseSensitivityValueLbl.setTextSize(18);
		mouseSensitivityValueLbl.setText( (int)(uiManager.getCursor().getMouseSensitivity()*100) + "");
		
		optionsPanel.addWidget(mouseSensitivityValueLbl);
		panelView.addElement(new LabelView(mouseSensitivityValueLbl));
		
		uiPos.x += 175;
		uiPos.y += 10;
		Button mousePlusBtn = setupButton(uiPos, "+", false );
		mousePlusBtn.getBounds().width = 50;
		mousePlusBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
				
				@Override
				public void onButtonClicked(ButtonEvent event) {
					Cursor cursor = uiManager.getCursor();
					float sensitivity = cursor.getMouseSensitivity();
					sensitivity += 0.01f;
										
					cursor.setMouseSensitivity(sensitivity);
					mouseSensitivityValueLbl.setText( (int)(sensitivity*100) + "");
				}
			});
		; uiPos.y += yInc;
		
		keyOverwriteLbl = new Label("Press any key");
		keyOverwriteLbl.setTheme(theme);
		keyOverwriteLbl.setBounds(new Rectangle(app.getScreenWidth(), 40));
		keyOverwriteLbl.getBounds().y = app.getScreenHeight() - 120;
		keyOverwriteLbl.setTextAlignment(TextAlignment.CENTER);
		keyOverwriteLbl.setFont(theme.getPrimaryFontName());
		keyOverwriteLbl.setTextSize(18);
		
		
		this.panelView.addElement(new LabelView(keyOverwriteLbl));
		this.panelView.addElement(new LabelView(controlsLbl));
		this.panelView.addElement(new TextBoxView(nameTxtBox));
				
		final int bottomPanelY = 570;
		
		// ~~~ Video 
		
		Label videoLbl = new Label("Video");
		videoLbl.setTheme(theme);
		videoLbl.setBounds(new Rectangle(10, bottomPanelY - 70, 470, 40));
		videoLbl.setTextAlignment(TextAlignment.LEFT);
		videoLbl.setFont(theme.getPrimaryFontName());
		videoLbl.setTextSize(18);
		
		uiPos.x = startX;
		uiPos.y = bottomPanelY;
		final Button resBtn = setupButton(uiPos, "Resolution: '" +  app.getScreenWidth()+"x" + app.getScreenHeight() + "'", false); uiPos.y += yInc;
		resBtn.getTextLabel().setForegroundColor(0xffffffff);
		resBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				DisplayMode[] modes = Gdx.graphics.getDisplayModes();
				displayModeIndex = (displayModeIndex + 1) % modes.length;
				mode = modes[displayModeIndex];
								
				resBtn.setText("Resolution: '" +  mode.width+"x" + mode.height + "'");
			}
		});
		
		final Button fullscreenBtn = setupButton(uiPos, "Fullscreen: '" +  app.isFullscreen() + "'", false); uiPos.y += yInc;
		fullscreenBtn.getTextLabel().setForegroundColor(0xffffffff);
		fullscreenBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				isFullscreen = !app.isFullscreen();						
				fullscreenBtn.setText("Fullscreen: '" +  isFullscreen + "'");
			}
		});
		
		final Button vsyncBtn = setupButton(uiPos, "VSync: '" +  app.isVSync() + "'", false); uiPos.y += yInc;
		vsyncBtn.getTextLabel().setForegroundColor(0xffffffff);
		vsyncBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.setVSync(!app.isVSync());
				app.getConfig().set(app.isVSync(), "video", "vsync");
				vsyncBtn.setText("VSync: '" +  app.isVSync() + "'");				
			}
		});
		
		this.panelView.addElement(new LabelView(videoLbl));
		
		
		// ~~~ Sound 
		
		Label soundLbl = new Label("Sound");
		soundLbl.setTheme(theme);
		soundLbl.setBounds(new Rectangle(app.getScreenWidth()/2, bottomPanelY-70, 400, 40));
		soundLbl.setTextAlignment(TextAlignment.LEFT);
		soundLbl.setFont(theme.getPrimaryFontName());
		soundLbl.setTextSize(18);
		
		uiPos.x = app.getScreenWidth()/2 + startX;
		uiPos.y = bottomPanelY;
		final Button sndBtn = setupButton(uiPos, "Volume: '" +  Sounds.getVolume() * 100 + "%'", false); uiPos.y += yInc;
		sndBtn.getTextLabel().setForegroundColor(0xffffffff);
		sndBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				int v = (int)(Sounds.getVolume() * 100.0f);
				v += 10;
				if(v>100) {
					v = 0;
				}
				Sounds.setVolume(v/100.0f);
				sndBtn.setText("Volume: '" + v + "%'");
			}
		});
		
		this.panelView.addElement(new LabelView(soundLbl));
	}

	private void refreshConfigUI() {
		this.optionsPanel.destroy();
		this.keyInput.destroy();
		this.keyOverwriteLbl.destroy();
		
		createUI();
	}
	
	private Button setupButton(Vector2f pos, String text) {
		return setupButton(pos, text, true);
	}
	
	private Button setupButton(Vector2f pos, final String text, boolean configChange) {
		Button btn = new Button();
		btn.setTheme(theme);
		btn.setText(text);
		btn.setBounds(new Rectangle(240, 30));
		btn.getBounds().centerAround(pos);
		btn.setForegroundColor(theme.getForegroundColor());
		btn.setEnableGradiant(false);
		btn.setTextSize(18);
		btn.setHoverTextSize(22);		
		btn.getTextLabel().setFont(theme.getSecondaryFontName());
		btn.getTextLabel().setTextAlignment(TextAlignment.LEFT);
		btn.getTextLabel().setForegroundColor(theme.getForegroundColor());
		
		if(configChange) {
			btn.getTextLabel().setForegroundColor(0xffffffff);
			btn.addOnButtonClickedListener(new OnButtonClickedListener() {			
				@Override
				public void onButtonClicked(ButtonEvent event) {
					keyInput.reset();
					
					String inputType = text.substring(0, text.indexOf(":")).replace(" ", "_");
					keyInput.setKeymap(inputType);
					
					isKeyModifyOn++;
				}
			});
		}
		
		this.optionsPanel.addWidget(btn);
		this.panelView.addElement(new ButtonView(btn));
		
		return btn;
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.State#enter()
	 */
	@Override
	public void enter() {
		this.optionsPanel.show();
		this.keyInput.setDisabled(true);
	}

	/* (non-Javadoc)
	 * @see seventh.shared.State#exit()
	 */
	@Override
	public void exit() {
		this.optionsPanel.hide();
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Screen#destroy()
	 */
	@Override
	public void destroy() {
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.State#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.panelView.update(timeStep);
		
		if(this.isKeyModifyOn>0) {
			/* skip a frame */
			if(this.isKeyModifyOn>5) {
				this.keyInput.setDisabled(false);
			}
			this.isKeyModifyOn++;
		
		
			if(!this.keyInput.isDisabled()) {
				this.uiManager.hideMouse();	
				
				this.flashText.update(timeStep);
				if(this.flashText.isTime()) {
					this.flashDelay.reset();
					this.flashDelay.start();
				}
		
				this.flashDelay.update(timeStep);
				
				if(!this.flashDelay.isTime()) {			
					this.keyOverwriteLbl.show();
				}	
				else {
					this.keyOverwriteLbl.hide();
				}
				
				if(keyInput.isCancelled()) {
					keyInput.setDisabled(true);
					Sounds.playGlobalSound(Sounds.uiSelect);
					isKeyModifyOn = 0;
				}
				else if(keyInput.isDone()) {
					app.getKeyMap().setKey(keyInput.getKeymap(), keyInput.getKey());
					keyInput.setDisabled(true);
					isKeyModifyOn = 0;
					
					refreshConfigUI();
					Sounds.playGlobalSound(Sounds.uiSelect);
					
				}
			}
		}
		else {
			this.uiManager.showMouse();
			this.keyOverwriteLbl.hide();	
			
			if(uiManager.isKeyDown(Keys.ESCAPE)) {
				app.popScreen();
			}
		}
	}




	/* (non-Javadoc)
	 * @see seventh.client.Screen#render(seventh.client.gfx.Canvas)
	 */
	@Override
	public void render(Canvas canvas) {
		canvas.fillRect(0, 0, canvas.getWidth() + 100,  canvas.getHeight() + 100, theme.getBackgroundColor());
		
		this.panelView.render(canvas, null, 0);
		
		canvas.begin();
		canvas.setFont(theme.getPrimaryFontName(), 54);
		canvas.boldFont();
		
		int fontColor = theme.getForegroundColor();
		String message = "Options";
		RenderFont.drawShadedString(canvas, message
				, canvas.getWidth()/2 - canvas.getWidth(message)/2, canvas.getHeight()/9, fontColor);
				
		this.uiManager.render(canvas);
		
		canvas.end();
	}

	/* (non-Javadoc)
	 * @see seventh.client.Screen#getInputs()
	 */
	@Override
	public Inputs getInputs() {
		return this.uiManager;
	}

}
