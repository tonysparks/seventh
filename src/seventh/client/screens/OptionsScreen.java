/*
 * see license.txt 
 */
package seventh.client.screens;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;

import seventh.client.SeventhGame;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Cursor;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Theme;
import seventh.client.gfx.VideoConfig;
import seventh.client.inputs.Inputs;
import seventh.client.inputs.KeyMap;
import seventh.client.sfx.Sounds;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Cons;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.ui.Button;
import seventh.ui.Checkbox;
import seventh.ui.KeyInput;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.Panel;
import seventh.ui.Slider;
import seventh.ui.TextBox;
import seventh.ui.UserInterfaceManager;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.CheckboxEvent;
import seventh.ui.events.HoverEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.events.OnCheckboxClickedListener;
import seventh.ui.events.OnHoverListener;
import seventh.ui.events.OnSliderMovedListener;
import seventh.ui.events.SliderMovedEvent;
import seventh.ui.view.ButtonView;
import seventh.ui.view.CheckboxView;
import seventh.ui.view.LabelView;
import seventh.ui.view.PanelView;
import seventh.ui.view.SliderView;
import seventh.ui.view.TextBoxView;

/**
 * Displays the players options
 * 
 * @author Tony
 *
 */
public class OptionsScreen implements Screen {
    
    /**
     * Cached for performance reasons.
     */
    private static final DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();
    
    
    private SeventhGame app;
    
    private UserInterfaceManager uiManager;
    
    private Theme theme;
            
    private Timer flashText, flashDelay;
    
    private KeyInput keyInput;
    
    private Panel optionsPanel;    
    private PanelView panelView;
    
    private TextBox nameTxtBox;
    
    private Label keyOverwriteLbl;
    
    private int isKeyModifyOn;
    
    private DisplayMode mode;    
    private boolean isFullscreen;
    private int displayModeIndex;
    
    private Button applyBtn;
        
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
        this.panelView = new PanelView();
        this.optionsPanel = new Panel();
        
        this.keyInput = new KeyInput();
        this.keyInput.setDisabled(true);
        this.optionsPanel.addWidget(keyInput);
        
        this.isFullscreen = app.isFullscreen();
        
        Vector2f uiPos = new Vector2f(200, app.getScreenHeight() - 20);
        
        if(applyBtn != null) {
            applyBtn.destroy();
        }
        
        applyBtn = setupButton(uiPos, "Apply", false);
        applyBtn.setVisible(false);
        applyBtn.getBounds().setSize(140, 80);
        applyBtn.getTextLabel().setFont(theme.getPrimaryFontName());
        applyBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
            
                VideoConfig vConfig = app.getConfig().getVideo();
                if(isFullscreen != app.isFullscreen()) {
                    vConfig.setFullscreen(isFullscreen);
                    
                    boolean success = false;
                    
                    if(!isFullscreen) {
                        vConfig.setWidth(SeventhGame.DEFAULT_MINIMIZED_SCREEN_WIDTH);
                        vConfig.setHeight(SeventhGame.DEFAULT_MINIMIZED_SCREEN_HEIGHT);                        
                        success = Gdx.graphics.setDisplayMode(SeventhGame.DEFAULT_MINIMIZED_SCREEN_WIDTH, SeventhGame.DEFAULT_MINIMIZED_SCREEN_HEIGHT, isFullscreen);                            
                    }
                    else if (mode!=null) {
                        vConfig.setWidth(mode.width);
                        vConfig.setHeight(mode.height);
                        success = Gdx.graphics.setDisplayMode(mode.width, mode.height, true);        
                    }
                    else {                                                        
                        success = Gdx.graphics.setDisplayMode(app.getScreenWidth(), app.getScreenHeight(), true);
                    }
                    
                    if(success) {
                        app.restartVideo();
                    }
                }
                else if(mode!=null) {
                    if(app.isFullscreen()) {                            
                        vConfig.setWidth(mode.width);
                        vConfig.setHeight(mode.height);
                        if(Gdx.graphics.setDisplayMode(mode.width, mode.height, true)) {
                            app.restartVideo();
                        }
                    }
                }
                
                applyBtn.setVisible(false);
            }
        });
        
        uiPos.x = app.getScreenWidth() - 80;

        Button backBtn = setupButton(uiPos, "Back", false);
        backBtn.getBounds().setSize(140, 80);
        backBtn.getTextLabel().setFont(theme.getPrimaryFontName());
        backBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {                
                app.popScreen();
                Sounds.playGlobalSound(Sounds.uiNavigate);
            }
        });
        
        
        nameTxtBox = new TextBox();
        nameTxtBox.setLabelText("Name: ");
        nameTxtBox.setTheme(theme);
        nameTxtBox.setBounds(new Rectangle(app.getScreenWidth()/2 - 50, 55, 200, 25));        
        //nameTxtBox.setFont(theme.getSecondaryFontName()); 
        nameTxtBox.setFont(Theme.DEFAULT_FONT);
        nameTxtBox.setTextSize(14);
        nameTxtBox.getLabel().setTextSize(18);
        nameTxtBox.setFocus(false);
        nameTxtBox.setMaxSize(16);
        nameTxtBox.setText(app.getConfig().getPlayerName());
        nameTxtBox.getTextLabel().setForegroundColor(0xffffffff);
        
        
        this.optionsPanel.addWidget(nameTxtBox);
        
        // ~~~ Controls
        
        KeyMap keys = app.getKeyMap();
                
        Label controlsLbl = new Label("Controls");
        controlsLbl.setTheme(theme);
        controlsLbl.setBounds(new Rectangle(10, 90, 400, 40));
        controlsLbl.setHorizontalTextAlignment(TextAlignment.LEFT);
        controlsLbl.setFont(theme.getPrimaryFontName());
        controlsLbl.setTextSize(14);
        
        
        final int startX = 270;
        final int startY = 110;
        final int yInc = 25;
        
        uiPos.x = startX;
        uiPos.y = startY;        
        setupButton(uiPos, "up: '" + keys.keyString(keys.getUpKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "down: '" + keys.keyString(keys.getDownKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "left: '" + keys.keyString(keys.getLeftKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "right: '" + keys.keyString(keys.getRightKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "walk: '" + keys.keyString(keys.getWalkKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "sprint: '" + keys.keyString(keys.getSprintKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "crouch: '" + keys.keyString(keys.getCrouchKey())+"'"); uiPos.y += yInc;
        
        uiPos.x = 155;
        uiPos.y -= 5;
        
        Checkbox invertMouseChkBx = new Checkbox(app.getConfig().getKeyMap().isMouseInverted());
        invertMouseChkBx.setTheme(theme);                
        invertMouseChkBx.setLabelText("Invert Mouse");
        invertMouseChkBx.getBounds().setLocation(uiPos);
        invertMouseChkBx.getLabel().setForegroundColor(theme.getForegroundColor());
        invertMouseChkBx.addCheckboxClickedListener(new OnCheckboxClickedListener() {
            
            @Override
            public void onCheckboxClicked(CheckboxEvent event) {
                app.getConfig().getKeyMap().setMouseInverted(event.getCheckbox().isChecked()); 
            }
        });
        
        this.optionsPanel.addWidget(invertMouseChkBx);
        this.panelView.addElement(new CheckboxView(invertMouseChkBx));
        
        uiPos.x = app.getScreenWidth() / 2 + 100;
        uiPos.y = startY;
        setupButton(uiPos, "reload: '" + keys.keyString(keys.getReloadKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "drop weapon: '" + keys.keyString(keys.getDropWeaponKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "melee attack: '" + keys.keyString(keys.getMeleeAttackKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "throw grenade: '" + keys.keyString(keys.getThrowGrenadeKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "fire: '" + keys.keyString(keys.getFireKey())+"'"); uiPos.y += yInc;
        setupButton(uiPos, "use: '" + keys.keyString(keys.getUseKey()) +"'" ); uiPos.y += yInc;
        setupButton(uiPos, "iron sights: '" + keys.keyString(keys.getIronSightsKey()) +"'" ); uiPos.y += yInc;
        
        uiPos.y += 10;
        final Slider mouseSensitivitySlider = new Slider();
        
        final Button mouseSensitivityBtn = setupButton(uiPos, "Mouse Sensitivity:", false);
        mouseSensitivityBtn.getBounds().width = 80;
        mouseSensitivityBtn.setHoverTextSize(16);
        mouseSensitivityBtn.getTextLabel().setForegroundColor(theme.getForegroundColor());
        mouseSensitivityBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                int value = mouseSensitivitySlider.getIndex();
                value += 10;
                if(value > 100) {
                    value = 0;
                }
                mouseSensitivitySlider.moveHandle(value);
            }
        });
        
        uiPos.x = 570;
        uiPos.y -= 8;
        
        final Cursor cursor = uiManager.getCursor();
        
        mouseSensitivitySlider.setTheme(theme);
        mouseSensitivitySlider.getBounds().setSize(100, 5);
        mouseSensitivitySlider.getBounds().setLocation(uiPos);
        // valid ranges from 0.5 to 2.0
        int handlePos = (int)((cursor.getMouseSensitivity()/2f) * 100.0f);
        mouseSensitivitySlider.moveHandle(handlePos);
        mouseSensitivitySlider.addSliderMoveListener(new OnSliderMovedListener() {
            
            @Override
            public void onSliderMoved(SliderMovedEvent event) {                
                int value = event.getSlider().getIndex();
                
                float sensitivity = value / 50f;
                if(value <= 0) {
                    sensitivity = 0.5f;
                }
                cursor.setMouseSensitivity(sensitivity);
            }
        });
        mouseSensitivitySlider.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                uiManager.getCursor().touchAccuracy();
            }
        });
        
        optionsPanel.addWidget(mouseSensitivitySlider);
        panelView.addElement(new SliderView(mouseSensitivitySlider));
        
        uiPos.y += yInc;
        
        keyOverwriteLbl = new Label("Press any key");
        keyOverwriteLbl.setTheme(theme);
        keyOverwriteLbl.setBounds(new Rectangle(app.getScreenWidth(), 40));
        keyOverwriteLbl.getBounds().y = app.getScreenHeight() - 30;
        keyOverwriteLbl.setHorizontalTextAlignment(TextAlignment.CENTER);
        keyOverwriteLbl.setFont(theme.getPrimaryFontName());
        keyOverwriteLbl.setTextSize(18);
        keyOverwriteLbl.setForegroundColor(0xffffffff);
        
        
        this.panelView.addElement(new LabelView(keyOverwriteLbl));
        this.panelView.addElement(new LabelView(controlsLbl));
        this.panelView.addElement(new TextBoxView(nameTxtBox));
                
        final int bottomPanelY = 360;
        
        // ~~~ Video 
        
        Label videoLbl = new Label("Video");
        videoLbl.setTheme(theme);
        videoLbl.setBounds(new Rectangle(10, bottomPanelY - 50, 470, 40));
        videoLbl.setHorizontalTextAlignment(TextAlignment.LEFT);
        videoLbl.setFont(theme.getPrimaryFontName());
        videoLbl.setTextSize(16);
        
        uiPos.x = startX - 100;
        uiPos.y = bottomPanelY;
        final Button resBtn = setupButton(uiPos, "Resolution: '" +  Gdx.graphics.getWidth()+"x" + Gdx.graphics.getHeight() + "'", false); uiPos.y += yInc;
        resBtn.getTextLabel().setForegroundColor(0xffffffff);
        resBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {                
                displayModeIndex = (displayModeIndex + 1) % displayModes.length;
                mode = displayModes[displayModeIndex];
                                
                resBtn.setText("Resolution: '" +  mode.width+"x" + mode.height + "'");
                applyBtn.setVisible(true);
            }
        });
        
        final Button fullscreenBtn = setupButton(uiPos, "Fullscreen: '" +  app.isFullscreen() + "'", false); uiPos.y += yInc;
        fullscreenBtn.getTextLabel().setForegroundColor(0xffffffff);
        fullscreenBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                isFullscreen = !isFullscreen;                        
                fullscreenBtn.setText("Fullscreen: '" +  isFullscreen + "'");
                applyBtn.setVisible(true);
            }
        });
        
        final Button vsyncBtn = setupButton(uiPos, "VSync: '" +  app.isVSync() + "'", false); uiPos.y += yInc;
        vsyncBtn.getTextLabel().setForegroundColor(0xffffffff);
        vsyncBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                app.setVSync(!app.isVSync());
                app.getConfig().getVideo().setVsync(app.isVSync());
                vsyncBtn.setText("VSync: '" +  app.isVSync() + "'");                   
            }
        });
        
        this.panelView.addElement(new LabelView(videoLbl));
        
        
        // ~~~ Sound 
        
        Label soundLbl = new Label("Sound");
        soundLbl.setTheme(theme);
        soundLbl.setBounds(new Rectangle(app.getScreenWidth()/2 - 120, bottomPanelY-50, 400, 40));
        soundLbl.setHorizontalTextAlignment(TextAlignment.LEFT);
        soundLbl.setFont(theme.getPrimaryFontName());
        soundLbl.setTextSize(16);
                
        this.panelView.addElement(new LabelView(soundLbl));
        
        uiPos.x = app.getScreenWidth()/3 + startX - 135;
        uiPos.y = bottomPanelY - 7;
                        
        final Slider slider = new Slider();        
        slider.setTheme(theme);
        slider.getBounds().setSize(100, 5);
        slider.getBounds().setLocation(uiPos);
        slider.addSliderMoveListener(new OnSliderMovedListener() {
            
            @Override
            public void onSliderMoved(SliderMovedEvent event) {
                int value = event.getSlider().getIndex();                    
                Sounds.setVolume(value / 100.0f);
            }
        });
        slider.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                uiManager.getCursor().touchAccuracy();
            }
        });
        
        int v = (int)(Sounds.getVolume() * 100.0f);
        slider.moveHandle(v);
        this.optionsPanel.addWidget(slider);
        this.panelView.addElement(new SliderView(slider));
        
        uiPos.x = app.getScreenWidth()/3 + startX - 90;
        uiPos.y = bottomPanelY;
        
        final Button sndBtn = setupButton(uiPos, "Volume: ", false); uiPos.y += yInc;
        sndBtn.getBounds().width = 80;
        sndBtn.getTextLabel().setForegroundColor(0xffffffff);
        sndBtn.setHoverTextSize(16);
        sndBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                int value = slider.getIndex();
                value += 10;
                if(value > 100) {
                    value = 0;
                }
                slider.moveHandle(value);
            }
        });
        
        final int thirdSectionX = app.getScreenWidth() - ((app.getScreenWidth() / 3) + 40);
        
        Label gameLbl = new Label("Game");
        gameLbl.setTheme(theme);
        gameLbl.setBounds(new Rectangle(thirdSectionX + 30, bottomPanelY-50, 400, 40));
        gameLbl.setHorizontalTextAlignment(TextAlignment.LEFT);
        gameLbl.setFont(theme.getPrimaryFontName());
        gameLbl.setTextSize(16);
                
        this.panelView.addElement(new LabelView(gameLbl));
        
        uiPos.x = thirdSectionX + 60;
        uiPos.y = bottomPanelY - 15;
        
        Checkbox weaponRecoilEnabledChkBx = new Checkbox(app.getConfig().getWeaponRecoilEnabled());
        weaponRecoilEnabledChkBx.setTheme(theme);                
        weaponRecoilEnabledChkBx.setLabelText("Weapon Recoil");
        weaponRecoilEnabledChkBx.getBounds().setLocation(uiPos);
        weaponRecoilEnabledChkBx.getLabel().setForegroundColor(theme.getForegroundColor());
        weaponRecoilEnabledChkBx.addCheckboxClickedListener(new OnCheckboxClickedListener() {
            
            @Override
            public void onCheckboxClicked(CheckboxEvent event) {
                app.getConfig().setWeaponRecoilEnabled(event.getCheckbox().isChecked());                
            }
        });
        weaponRecoilEnabledChkBx.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                uiManager.getCursor().touchAccuracy();
            }
        });
        
        this.optionsPanel.addWidget(weaponRecoilEnabledChkBx);
        this.panelView.addElement(new CheckboxView(weaponRecoilEnabledChkBx));
        
                
        uiPos.y = bottomPanelY + 12;
        
        Checkbox bloodEnabledChkBx = new Checkbox(app.getConfig().getBloodEnabled());
        bloodEnabledChkBx.setTheme(theme);                
        bloodEnabledChkBx.setLabelText("Blood");
        bloodEnabledChkBx.getBounds().setLocation(uiPos);
        bloodEnabledChkBx.getLabel().setForegroundColor(theme.getForegroundColor());
        bloodEnabledChkBx.addCheckboxClickedListener(new OnCheckboxClickedListener() {
            
            @Override
            public void onCheckboxClicked(CheckboxEvent event) {
                app.getConfig().setBloodEnabled(event.getCheckbox().isChecked());                
            }
        });
        bloodEnabledChkBx.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                uiManager.getCursor().touchAccuracy();
            }
        });
        this.optionsPanel.addWidget(bloodEnabledChkBx);
        this.panelView.addElement(new CheckboxView(bloodEnabledChkBx));
        
        
        uiPos.y = bottomPanelY + 39;
        
        Checkbox followReticleEnabledChkBx = new Checkbox(app.getConfig().getFollowReticleEnabled());
        followReticleEnabledChkBx.setTheme(theme);                
        followReticleEnabledChkBx.setLabelText("Camera Follow Reticle");
        followReticleEnabledChkBx.getBounds().setLocation(uiPos);
        followReticleEnabledChkBx.getLabel().setForegroundColor(theme.getForegroundColor());
        followReticleEnabledChkBx.addCheckboxClickedListener(new OnCheckboxClickedListener() {
            
            @Override
            public void onCheckboxClicked(CheckboxEvent event) {
                app.getConfig().setFollowReticleEnabled(event.getCheckbox().isChecked());                
            }
        });
        followReticleEnabledChkBx.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                uiManager.getCursor().touchAccuracy();
            }
        });
        this.optionsPanel.addWidget(followReticleEnabledChkBx);
        this.panelView.addElement(new CheckboxView(followReticleEnabledChkBx));
        
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
        btn.setTextSize(15);
        btn.setHoverTextSize(20);        
        btn.getTextLabel().setFont(theme.getSecondaryFontName());
        btn.getTextLabel().setHorizontalTextAlignment(TextAlignment.LEFT);
        btn.getTextLabel().setForegroundColor(theme.getForegroundColor());
        btn.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                uiManager.getCursor().touchAccuracy();
            }
        });
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
        this.applyBtn.setVisible(false);
        
        this.keyInput.setDisabled(true);
    }

    /* (non-Javadoc)
     * @see seventh.shared.State#exit()
     */
    @Override
    public void exit() {
        this.optionsPanel.destroy();
        
        try {
            
            if(nameTxtBox!=null) {
                String name = nameTxtBox.getText();
                String cfgName = app.getConfig().getPlayerName(); 
                if(name != null && cfgName != null) {
                    if(!name.equals(cfgName)) {
                        // change the configuration file
                        // and if we are connected to a server,
                        // let the server know we changed our name 
                        app.getConfig().setPlayerName(name);
                        Cons.getImpl().execute("name", name);
                    }
                }
            }
            
            app.getConfig().setMouseSensitivity(uiManager.getCursor().getMouseSensitivity());                    
            app.getConfig().save();

        }
        catch (IOException e) {
            Cons.println("Unable to save the configuration file:");
            Cons.println(e);
            
            app.getTerminal().open();
        }        
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Screen#destroy()
     */
    @Override
    public void destroy() {
        this.optionsPanel.destroy();
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.State#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.uiManager.update(timeStep);
        this.panelView.update(timeStep);
        
        this.uiManager.checkIfCursorIsHovering();
        
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
    public void render(Canvas canvas, float alpha) {
        canvas.fillRect(0, 0, canvas.getWidth() + 100,  canvas.getHeight() + 100, theme.getBackgroundColor());
        
        this.panelView.render(canvas, null, 0);
        
        canvas.begin();
        canvas.setFont(theme.getPrimaryFontName(), 34);
        canvas.boldFont();
        
        int fontColor = theme.getForegroundColor();
        String message = "Options";
        RenderFont.drawShadedString(canvas, message
                , canvas.getWidth()/2 - RenderFont.getTextWidth(canvas, message)/2, canvas.getHeight()/12, fontColor);
                
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
