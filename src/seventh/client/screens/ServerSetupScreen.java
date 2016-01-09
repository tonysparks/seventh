/*
 * see license.txt 
 */
package seventh.client.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.client.Inputs;
import seventh.client.Screen;
import seventh.client.SeventhGame;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.client.gfx.Theme;
import seventh.game.type.GameType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.server.GameServer.GameServerSettings;
import seventh.shared.MapList;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Checkbox;
import seventh.ui.KeyInput;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.Panel;
import seventh.ui.TextBox;
import seventh.ui.UserInterfaceManager;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.CheckboxEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.events.OnCheckboxClickedListener;
import seventh.ui.view.ButtonView;
import seventh.ui.view.CheckboxView;
import seventh.ui.view.LabelView;
import seventh.ui.view.PanelView;
import seventh.ui.view.TextBoxView;

import com.badlogic.gdx.Input.Keys;

/**
 * Server setup page
 * 
 * @author Tony
 *
 */
public class ServerSetupScreen implements Screen {
		
	private boolean load;
	
	private MenuScreen menuScreen;
	private SeventhGame app;
	
	private UserInterfaceManager uiManager;
	
	private Theme theme;
			
	private KeyInput keyInput;
	
	private Panel optionsPanel;	
	private PanelView<Renderable> panelView;
		
	private GameServerSettings gameSettings;
	private List<String> mapListings;
	private int currentMapIndex;
	
	private Random random;
	private static final String[] BOT_NAMES = {
		"Messiah",
		"Irishman",
		"Outlaw",
		"Osiris",
		"Newera",
		"Uki",
		"Misty",
		"Randy",
		"Jeremy",
		"Tripwire",
		"Leo",
		"Mr.X",
	};
	
	/**
	 * 
	 */
	public ServerSetupScreen(final MenuScreen menuScreen) {
		this.menuScreen = menuScreen;
		this.theme = menuScreen.getTheme();		
		this.app = menuScreen.getApp();
		this.uiManager = menuScreen.getUiManager();			
		
		this.load = false;
		
		this.random = new Random();
		

		this.mapListings = MapList.getMapListing();
		
		this.gameSettings = new GameServerSettings();
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
		
	//	this.gameSettings.alliedTeam.add(app.getConfig().getString("name"));
		
		for(int i = 0; i < 2; i++) {
			String allied = getNextRandomName();
			this.gameSettings.alliedTeam.add(allied);
			
			String axis = getNextRandomName();
			this.gameSettings.axisTeam.add(axis);
		}
		
		
		createUI();
	}
		
	private String getNextRandomName() {
		String name = null;
		boolean found = false;
		final int maxIterations = 100;
		int i = 0;
		while(!found) {
			name = BOT_NAMES[this.random.nextInt(BOT_NAMES.length)];
			i++;
			
			if(i>maxIterations) {
				found = true;
			}
			
			if(gameSettings.alliedTeam.contains(name)) {
				continue;
			}
			
			if(gameSettings.axisTeam.contains(name)) {
				continue;
			}
			
			found = true;
		}
		
		return name;
	}
	
	private void refreshUI() {
		if(this.panelView!=null) {
			this.panelView.clear();
		}
		if(optionsPanel!=null) {
			optionsPanel.destroy();
		}
		
		createUI();
	}
	
	private void createUI() {
		this.panelView = new PanelView<>();
		this.optionsPanel = new Panel();
		
		this.keyInput = new KeyInput();
		this.keyInput.setDisabled(true);
		this.optionsPanel.addWidget(keyInput);
				
		Vector2f uiPos = new Vector2f(180, app.getScreenHeight() - 30);
		
		Button saveBtn = setupButton(uiPos, "Play",false);		
		saveBtn.getTextLabel().setFont(theme.getPrimaryFontName());
		saveBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				load = true;
				if(app.getTerminal().getInputText().equals("connect ")) {
					app.getTerminal().setInputText("");
				}
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
		headerLbl.setBounds(new Rectangle(0,50, app.getScreenWidth(), 80));
		headerLbl.setTextAlignment(TextAlignment.CENTER);
		headerLbl.setFont(theme.getPrimaryFontName());
		headerLbl.setTextSize(45);
				

		uiPos.x = 10;
		uiPos.y = 120;
		setupLabel(uiPos, "Settings", true);
		
		final int startX = 30;
		final int startY = 160;
		final int yInc = 20;
		final int xInc = 100;
		
		final int toggleX = 290;
		
		uiPos.x = startX;
		uiPos.y = startY;		
		
		setupLabel(uiPos, "Server Name: ", false);
		
		uiPos.x = toggleX+95;
		uiPos.y += yInc;
		
		final TextBox serverNameTxtBox = setupTextBox(uiPos, this.gameSettings.serverName);
		serverNameTxtBox.setBounds(new Rectangle(240, 30));
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
		final Button mapBtn = setupButton(uiPos, gameSettings.currentMap, true);
		mapBtn.getBounds().setSize(340, 30);
		mapBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				currentMapIndex++;
				if(currentMapIndex>=mapListings.size()) {
					currentMapIndex = 0;
				}
				
				gameSettings.currentMap = mapListings.isEmpty() ? "" : mapListings.get(currentMapIndex);
				mapBtn.setText(gameSettings.currentMap);
			}
		}); 
		
		uiPos.x = startX;
		uiPos.y += yInc;
		setupLabel(uiPos, "Max Players: ", false);
						
		uiPos.x = toggleX + 30;
		final Label maxPlayersLbl = setupLabel(uiPos, Integer.toString(gameSettings.maxPlayers), false);
		
		uiPos.x = toggleX;
		uiPos.y += yInc;
		setupButton(uiPos, "-", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
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
		setupButton(uiPos, "+", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				gameSettings.maxPlayers++;
				if(gameSettings.maxPlayers > 12) {
					gameSettings.maxPlayers = 12;
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
		setupButton(uiPos, "-", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				if(gameSettings.gameType.equals(GameType.Type.OBJ)) {
					gameSettings.maxScore--;		
					if(gameSettings.maxScore < 1) {
						gameSettings.maxScore = 1;
					}
				}
				else {
					gameSettings.maxScore -= 10;		
					if(gameSettings.maxScore < 1) {
						gameSettings.maxScore = 0;
					}
				}
				maxScoreLbl.setText(Integer.toString(gameSettings.maxScore));
			}
		}); 

		uiPos.x += xInc;
		setupButton(uiPos, "+", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				if(gameSettings.gameType.equals(GameType.Type.OBJ)) {
					gameSettings.maxScore++;							
				}
				else {
					gameSettings.maxScore += 10;							
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
		setupButton(uiPos, "-", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
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
		setupButton(uiPos, "+", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				gameSettings.matchTime++;				
				matchTimeLbl.setText(Long.toString(gameSettings.matchTime));
			}
		}); 
		
		gameTypeBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				switch (gameSettings.gameType) {
					case OBJ:
						gameSettings.gameType = GameType.Type.TDM;
						gameSettings.matchTime = 20;
						gameSettings.maxScore = 50;
						break;
					case TDM:
						gameSettings.gameType = GameType.Type.OBJ;
						gameSettings.matchTime = 3;
						gameSettings.maxScore = 7;
						break;
					default:
						gameSettings.gameType = GameType.Type.TDM;
						break;
				}
				
				maxScoreLbl.setText(Integer.toString(gameSettings.maxScore));
				matchTimeLbl.setText(Long.toString(gameSettings.matchTime));
				
				event.getButton().setText(gameSettings.gameType.getDisplayName());
			}
		});
		

		uiPos.x = startX;
		uiPos.y += yInc;
		
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
		
		this.optionsPanel.addWidget(isDedicatedServer);
		this.panelView.addElement(new CheckboxView(isDedicatedServer));
		
		
		uiPos.x = toggleX - xInc; 		
		
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
		
		this.optionsPanel.addWidget(isLAN);
		this.panelView.addElement(new CheckboxView(isLAN));
		
		
		uiPos.x = 10;
		uiPos.y += yInc * 2;
		setupLabel(uiPos, "Team", true);
		
		uiPos.x = startX + 100;
		uiPos.y += yInc + 30;
		setupButton(uiPos, "-", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				if(gameSettings.alliedTeam.size() > 0) {
					gameSettings.alliedTeam.remove(gameSettings.alliedTeam.size()-1);
					refreshUI();
				}
			}
		});
		uiPos.x += 10;
		uiPos.y -= 20;
		setupLabel(uiPos, "Allies", false);
		
		uiPos.x += 100;
		uiPos.y += 20;
		setupButton(uiPos, "+", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				if(gameSettings.axisTeam.size() + gameSettings.alliedTeam.size() < gameSettings.maxPlayers-1) {
					String name = getNextRandomName();
					gameSettings.alliedTeam.add(name);
					
					refreshUI();
				}
			}
		});
		

		uiPos.x = 550;		
		setupButton(uiPos, "-", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				if(gameSettings.axisTeam.size() > 0) {
					gameSettings.axisTeam.remove(gameSettings.axisTeam.size()-1);
					refreshUI();
				}
			}
		});
		
		uiPos.x += 20;
		uiPos.y -= 20;
		setupLabel(uiPos, "Axis", false);
		
		uiPos.x += 90;
		uiPos.y += 20;
		setupButton(uiPos, "+", true).addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				if(gameSettings.axisTeam.size() + gameSettings.alliedTeam.size() < gameSettings.maxPlayers-1) {
				
					String name = getNextRandomName();
					gameSettings.axisTeam.add(name);
					
					refreshUI();
				}
			}
		});

		
		
		uiPos.x = startX;
		uiPos.y += yInc;
		createBotsPanel(uiPos);
			
		this.panelView.addElement(new LabelView(headerLbl));
				
	}
	
	private void createBotsPanel(Vector2f pos) {

//		int startX = (int)pos.x;	
		int startY = (int)pos.y;
		
		int i = 0;
		for(String name : gameSettings.alliedTeam) {
			final int nameIndex = i++;
			final TextBox box = setupTextBox(pos, name);
			box.addInputListenerToFront(new Inputs() {
			    
			    @Override
			    public boolean keyUp(int key) {
			        gameSettings.alliedTeam.set(nameIndex, box.getText());
			        return false;
			    }
			});
			
			if(i%2==0) {
				pos.x -= 170;
				pos.y += 40;
			}
			else {								
				pos.x += 170;
			}
			
		}
		
		pos.x = 450;
		pos.y = startY;
		
		i = 0;
		for(String name : gameSettings.axisTeam) {
            final int nameIndex = i++;
            final TextBox box = setupTextBox(pos, name);
            box.addInputListenerToFront(new Inputs() {
                
                @Override
                public boolean keyUp(int key) {
                    gameSettings.axisTeam.set(nameIndex, box.getText());
                    return false;
                }
            });
            			
			if(i%2==0) {
				pos.x -= 170;
				pos.y += 40;
			}
			else {								
				pos.x += 170;
			}
		}
		
	}
	
	private Button setupButton(Vector2f pos, final String text, boolean smallBtn) {
		Button btn = new Button();
		btn.setTheme(theme);
		btn.setText(text);
		if(smallBtn) {
			btn.setTextSize(18);
			btn.setHoverTextSize(22);
			btn.setBounds(new Rectangle(40, 30));
		}
		else { 
			btn.setTextSize(24);
			btn.setHoverTextSize(28);
			btn.setBounds(new Rectangle(140, 40));
		}
		
		btn.getBounds().centerAround(pos);
		btn.setForegroundColor(theme.getForegroundColor());
		btn.setEnableGradiant(false);			
		btn.getTextLabel().setFont(theme.getSecondaryFontName());
		btn.getTextLabel().setTextAlignment(TextAlignment.LEFT);
		btn.getTextLabel().setForegroundColor(theme.getForegroundColor());
				
		this.optionsPanel.addWidget(btn);
		this.panelView.addElement(new ButtonView(btn));
		
		return btn;
	}
	
	private Label setupLabel(Vector2f pos, final String text, boolean header) {
		Label lbl = new Label(text);
		lbl.setTheme(theme);
		lbl.setBounds(new Rectangle((int)pos.x, (int)pos.y, 400, 40));
		lbl.setTextAlignment(TextAlignment.LEFT);
		if(header) {
			lbl.setForegroundColor(theme.getForegroundColor());
			lbl.setFont(theme.getSecondaryFontName());
			lbl.setTextSize(24);
		}
		else {
			lbl.setForegroundColor(0xffffffff);
			lbl.setFont(theme.getSecondaryFontName());
			lbl.setTextSize(18);
		}
		
		this.optionsPanel.addWidget(lbl);
		this.panelView.addElement(new LabelView(lbl));
		
		return lbl;
	}
	
	
	private TextBox setupTextBox(Vector2f pos, final String text) {
		TextBox box = new TextBox();
		box.setBounds(new Rectangle((int)pos.x, (int)pos.y, 120, 30));
		box.getTextLabel().setFont(theme.getSecondaryFontName());
		box.setText(text);
		box.setMaxSize(16);
		box.setTextSize(14);		
		box.setFocus(false);
		
		this.optionsPanel.addWidget(box);
		this.panelView.addElement(new TextBoxView(box));
		
		return box;
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
		this.optionsPanel.destroy();
		this.panelView.clear();
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.Screen#destroy()
	 */
	@Override
	public void destroy() {
		this.optionsPanel.destroy();
		this.panelView.clear();
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.State#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		if(load) {
			menuScreen.startLocalServer(gameSettings);
			load = false;
		}
		
		panelView.update(timeStep);
		
		if(uiManager.isKeyDown(Keys.ESCAPE)) {
			app.setScreen(menuScreen);
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
//		canvas.setFont(theme.getPrimaryFontName(), 54);
//		canvas.boldFont();
//		
		this.menuScreen.getUiManager().render(canvas);
		
		canvas.end();
	}

	/* (non-Javadoc)
	 * @see seventh.client.Screen#getInputs()
	 */
	@Override
	public Inputs getInputs() {
		return this.menuScreen.getInputs();
	}

}
