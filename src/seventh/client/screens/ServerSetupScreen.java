/*
 * see license.txt 
 */
package seventh.client.screens;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.SeventhGame;
import seventh.client.gfx.Art;
import seventh.client.inputs.Inputs;
import seventh.game.type.GameType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.server.GameServer.GameServerSettings;
import seventh.shared.MapList;
import seventh.shared.MapList.MapEntry;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Checkbox;
import seventh.ui.ImagePanel;
import seventh.ui.KeyInput;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.Panel;
import seventh.ui.TextBox;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.CheckboxEvent;
import seventh.ui.events.HoverEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.events.OnCheckboxClickedListener;
import seventh.ui.events.OnHoverListener;
import seventh.ui.view.CheckboxView;
import seventh.ui.view.ImagePanelView;
import seventh.ui.view.LabelView;
import seventh.ui.view.PanelView;

/**
 * Server setup page
 * 
 * @author Tony
 *
 */
public class ServerSetupScreen extends AbstractServerSetupScreen {
        
    private KeyInput keyInput;
    
    private List<MapEntry> mapListings;
    private Map<String, TextureRegion> mapPreviews;
    
    private int currentMapIndex;
    private int gameTypeIndex;
    
    private MenuScreen menuScreen;
    
    private static final GameType.Type[] GAME_TYPES = GameType.Type.values();
    
    /**
     * @param app
     */
    public ServerSetupScreen(SeventhGame app) {   
        super(app, new GameServerSettings());                   
        this.menuScreen = app.getMenuScreen();

        this.mapListings = MapList.getMapListing();
        this.mapPreviews = buildMapPreviews(mapListings);
        
        this.gameSettings.currentMap = mapListings.isEmpty() ? null : this.mapListings.get(0);
        this.gameSettings.maxPlayers = SeventhConstants.MAX_PLAYERS;
        this.gameSettings.gameType = GameType.Type.TDM;
        
        this.gameSettings.maxScore = 50;
        this.gameSettings.matchTime = 20;
        
        this.gameSettings.alliedTeam = new ArrayList<String>();
        this.gameSettings.axisTeam = new ArrayList<String>();
        this.gameSettings.isDedicatedServer = false;
        this.gameSettings.isLAN = true;
        this.gameSettings.serverName = app.getConfig().getPlayerName() + "'s Server";
        this.gameSettings.port = SeventhConstants.DEFAULT_PORT; // TODO: Make random within range?
        
    //    this.gameSettings.alliedTeam.add(app.getConfig().getString("name"));
        
        for(int i = 0; i < 2; i++) {
            String allied = getNextRandomName();
            this.gameSettings.alliedTeam.add(allied);
            
            String axis = getNextRandomName();
            this.gameSettings.axisTeam.add(axis);
        }
        
    }
    
    private Map<String, TextureRegion> buildMapPreviews(List<MapEntry> mapList) {
        Map<String, TextureRegion> mapPreviews = new HashMap<>();
        for(MapEntry e : mapList) {            
            String fileName = MapList.stripFileExtension(e.getFileName()) + ".png";
            File previewFile = new File(fileName);
            if(previewFile.exists()) {
                TextureRegion tex = Art.loadImage(fileName);
                mapPreviews.put(e.getDisplayName(), tex);
            }
        }
        return mapPreviews;
    }
       
    private TextureRegion getCurrentMapPreview() {
        if(this.currentMapIndex > -1 && this.currentMapIndex < this.mapListings.size()) {
            String mapName = this.mapListings.get(this.currentMapIndex).getDisplayName();
            TextureRegion tex = this.mapPreviews.get(mapName); 
            return tex;
        }
        return null;
    }
    
    @Override
    protected void createUI() {
        this.panelView = new PanelView();
        this.optionsPanel = new Panel();
        
        this.keyInput = new KeyInput();
        this.keyInput.setDisabled(true);
        this.optionsPanel.addWidget(keyInput);
                
        Vector2f uiPos = new Vector2f(180, app.getScreenHeight() - 30);
        
        Button saveBtn = setupButton(uiPos, "Next", false);        
        saveBtn.getTextLabel().setFont(theme.getPrimaryFontName());
        saveBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                app.pushScreen(new ServerTeamsSetupScreen(app, gameSettings));
            }
        });
        
        uiPos.x = app.getScreenWidth() - 140;

        Button cancelBtn = setupButton(uiPos, "Cancel",false);        
        cancelBtn.getTextLabel().setFont(theme.getPrimaryFontName());
        cancelBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                app.setScreen(menuScreen);
            }
        });
        

            
        Label headerLbl = new Label("Game Setup");
        headerLbl.setTheme(theme);
        headerLbl.setBounds(new Rectangle(0,0, app.getScreenWidth(), 80));
        headerLbl.setHorizontalTextAlignment(TextAlignment.CENTER);
        headerLbl.setFont(theme.getPrimaryFontName());
        headerLbl.setTextSize(34);
                

        uiPos.x = 10;
        uiPos.y = 90;
        setupLabel(uiPos, "Settings", true);
        
        final int startX = 30;
        final int startY = 120;
        final int yInc = 20;
        final int xInc = 100;
        
        final int toggleX = 200;
        
        uiPos.x = startX;
        uiPos.y = startY;        
        
        final ImagePanel previewPanel = new ImagePanel(getCurrentMapPreview());
        previewPanel.setBounds(new Rectangle(app.getScreenWidth() - 400, startY + 5, 325, 275));//168, 168));        
        previewPanel.setBackgroundColor(0xff383e18);
        previewPanel.setForegroundColor(0xff000000);
        
        setupLabel(uiPos, "Server Name: ", false);
        
        uiPos.x = toggleX+95;
        uiPos.y += yInc;
        
        final TextBox serverNameTxtBox = setupTextBox(uiPos, this.gameSettings.serverName);
        serverNameTxtBox.setBounds(new Rectangle(240, 25));
        serverNameTxtBox.getBounds().centerAround(uiPos);
        serverNameTxtBox.setMaxSize(26);
        serverNameTxtBox.addInputListenerToFront(new Inputs() {
            
            @Override
            public boolean keyUp(int key) {
                gameSettings.serverName = serverNameTxtBox.getText(); 
                return false;
            }
        });
        
        uiPos.x = startX;
        uiPos.y += yInc;
        
        setupLabel(uiPos, "GameType: ", false);
        
        uiPos.x = toggleX;
        uiPos.y += yInc;
        Button gameTypeBtn = setupButton(uiPos, this.gameSettings.gameType.getDisplayName(), true);
        gameTypeBtn.getBounds().setSize(340, 30);
        
        uiPos.x = startX;
        uiPos.y += yInc;
        
        setupLabel(uiPos, "Map: ", false);
        
        uiPos.x = toggleX;
        uiPos.y += yInc;
        final Button mapBtn = setupButton(uiPos, gameSettings.currentMap.getDisplayName(), true);
        mapBtn.getBounds().setSize(340, 30);
        mapBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                currentMapIndex++;
                if(currentMapIndex>=mapListings.size()) {
                    currentMapIndex = 0;
                }
                
                gameSettings.currentMap = mapListings.isEmpty() ? new MapEntry("") : mapListings.get(currentMapIndex); 
                mapBtn.setText(gameSettings.currentMap.getDisplayName());
                previewPanel.setImage(getCurrentMapPreview());
            }
        }); 
        
        uiPos.x = startX;
        uiPos.y += yInc;
        setupLabel(uiPos, "Max Players: ", false);
                        
        uiPos.x = toggleX + 30;
        final Label maxPlayersLbl = setupLabel(uiPos, Integer.toString(gameSettings.maxPlayers), false);
        
        uiPos.x = toggleX;
        uiPos.y += yInc;
        setupButton(uiPos, "-", false).addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                gameSettings.maxPlayers--;
                int minSize = (gameSettings.axisTeam.size() + gameSettings.alliedTeam.size());
                if(gameSettings.maxPlayers < minSize) {
                    gameSettings.maxPlayers = minSize;
                }
                
                maxPlayersLbl.setText(Integer.toString(gameSettings.maxPlayers));
            }
        }); 
        
        uiPos.x += xInc;
        setupButton(uiPos, "+", false).addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                gameSettings.maxPlayers++;
                if(gameSettings.maxPlayers > SeventhConstants.MAX_PLAYERS) {
                    gameSettings.maxPlayers = SeventhConstants.MAX_PLAYERS;
                }
                
                maxPlayersLbl.setText(Integer.toString(gameSettings.maxPlayers));
            }
        }); 

                         
        

        uiPos.x = startX;
        uiPos.y += yInc;
        setupLabel(uiPos, "Max Score: ", false);
        
        uiPos.x = toggleX + 30;
        final Label maxScoreLbl = setupLabel(uiPos, Integer.toString(gameSettings.maxScore), false);
        
        uiPos.x = toggleX;
        uiPos.y += yInc;
        setupButton(uiPos, "-", false).addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                if(gameSettings.gameType.equals(GameType.Type.TDM)) {
                    gameSettings.maxScore -= 10;        
                    if(gameSettings.maxScore < 1) {
                        gameSettings.maxScore = 0;
                    }
                }
                else {
                    gameSettings.maxScore--;        
                    if(gameSettings.maxScore < 1) {
                        gameSettings.maxScore = 1;
                    }
                }
                maxScoreLbl.setText(Integer.toString(gameSettings.maxScore));
            }
        }); 

        uiPos.x += xInc;
        setupButton(uiPos, "+", false).addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                if(gameSettings.gameType.equals(GameType.Type.TDM)) {
                    gameSettings.maxScore += 10;                        
                }
                else {
                    gameSettings.maxScore++;                    
                                
                }            
                maxScoreLbl.setText(Integer.toString(gameSettings.maxScore));
            }
        }); 
        
        uiPos.x = startX;
        uiPos.y += yInc;
        setupLabel(uiPos, "Match Time: ", false);
        
        uiPos.x = toggleX + 30;
        final Label matchTimeLbl = setupLabel(uiPos, Long.toString(gameSettings.matchTime), false);
        
        uiPos.x = toggleX;
        uiPos.y += yInc;
        setupButton(uiPos, "-", false).addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                gameSettings.matchTime--;        
                if(gameSettings.matchTime < 1) {
                    gameSettings.matchTime = 1;
                }
                matchTimeLbl.setText(Long.toString(gameSettings.matchTime));
            }
        });
        

        uiPos.x += xInc;
        setupButton(uiPos, "+", false).addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                gameSettings.matchTime++;                
                matchTimeLbl.setText(Long.toString(gameSettings.matchTime));
            }
        }); 
        
        gameTypeBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                gameTypeIndex = (gameTypeIndex + 1) % GAME_TYPES.length;
                
                switch (GAME_TYPES[gameTypeIndex]) {
                    case OBJ:
                        gameSettings.gameType = GameType.Type.OBJ;
                        gameSettings.matchTime = 3;
                        gameSettings.maxScore = 7;
                        break;
                    case TDM:
                        gameSettings.gameType = GameType.Type.TDM;
                        gameSettings.matchTime = 20;
                        gameSettings.maxScore = 50;
                        break;
                    case CTF:
                        gameSettings.gameType = GameType.Type.CTF;
                        gameSettings.matchTime = 20;
                        gameSettings.maxScore = 3;
                        break;
                    case CMD:
                        gameSettings.gameType = GameType.Type.CMD;
                        gameSettings.matchTime = 20;
                        gameSettings.maxScore = 50;
                        break;
                    case SVR: {
                        gameSettings.gameType = GameType.Type.SVR;
                        gameSettings.matchTime = 20;
                        gameSettings.maxScore = 50;
                        break;
                    }
                }
                
                maxScoreLbl.setText(Integer.toString(gameSettings.maxScore));
                matchTimeLbl.setText(Long.toString(gameSettings.matchTime));
                
                // reset the map listing
                mapListings = MapList.getMapListing(gameSettings.gameType);
                currentMapIndex = 0;
                gameSettings.currentMap = mapListings.isEmpty() ? new MapEntry("") : mapListings.get(currentMapIndex);
                mapBtn.setText(gameSettings.currentMap.getDisplayName());
                previewPanel.setImage(getCurrentMapPreview());                
                
                event.getButton().setText(gameSettings.gameType.getDisplayName());
            }
        });
        

        uiPos.x = startX;
        uiPos.y += yInc + 20;
        
        Checkbox isDedicatedServer = new Checkbox(gameSettings.isDedicatedServer);
        isDedicatedServer.setTheme(theme);                
        isDedicatedServer.setLabelText("Public Server");
        isDedicatedServer.getBounds().setLocation(uiPos);
        isDedicatedServer.addCheckboxClickedListener(new OnCheckboxClickedListener() {
            
            @Override
            public void onCheckboxClicked(CheckboxEvent event) {
                gameSettings.isDedicatedServer = event.getCheckbox().isChecked();                
            }
        });
        isDedicatedServer.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                uiManager.getCursor().touchAccuracy();
            }
        });
        
        this.optionsPanel.addWidget(isDedicatedServer);
        this.panelView.addElement(new CheckboxView(isDedicatedServer));
        
        
        uiPos.x = toggleX ;//- xInc;         
        
        Checkbox isLAN = new Checkbox(!gameSettings.isDedicatedServer);
        isLAN.setTheme(theme);                
        isLAN.setLabelText("LAN");
        isLAN.getBounds().setLocation(uiPos);
        isLAN.addCheckboxClickedListener(new OnCheckboxClickedListener() {
            
            @Override
            public void onCheckboxClicked(CheckboxEvent event) {
                gameSettings.isLAN = event.getCheckbox().isChecked();
            }
        });
        isLAN.addOnHoverListener(new OnHoverListener() {
            
            @Override
            public void onHover(HoverEvent event) {
                uiManager.getCursor().touchAccuracy();
            }
        });
        
        this.optionsPanel.addWidget(isLAN);
        this.panelView.addElement(new CheckboxView(isLAN));
                   
        this.panelView.addElement(new LabelView(headerLbl));
        
        this.optionsPanel.addWidget(previewPanel);
        this.panelView.addElement(new ImagePanelView(previewPanel));
                
    }
    
    @Override
    public void enter() {
        super.enter();
        this.keyInput.setDisabled(true);
    }
    
    @Override
    public void destroy() {
        super.destroy();
        
        for(TextureRegion tex : this.mapPreviews.values()) {
            tex.getTexture().dispose();
        }
    }
    
    @Override
    public void update(TimeStep timeStep) {
        super.update(timeStep);
        
        if(uiManager.isKeyDown(Keys.ESCAPE)) {
            app.setScreen(menuScreen);
        }
    }
}
