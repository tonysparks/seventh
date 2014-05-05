/*
 * see license.txt 
 */
package seventh.client.screens;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import leola.vm.types.LeoArray;
import leola.vm.types.LeoObject;
import seventh.client.Inputs;
import seventh.client.Screen;
import seventh.client.SeventhGame;
import seventh.client.gfx.Art;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Renderable;
import seventh.client.gfx.Theme;
import seventh.client.sfx.Sounds;
import seventh.game.type.GameType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Cons;
import seventh.shared.MasterServerApi;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.ListBox;
import seventh.ui.Panel;
import seventh.ui.UserInterfaceManager;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.view.ButtonView;
import seventh.ui.view.ImageButtonView;
import seventh.ui.view.LabelView;
import seventh.ui.view.ListBoxView;
import seventh.ui.view.PanelView;

import com.badlogic.gdx.Input.Keys;

/**
 * Displays the players options
 * 
 * @author Tony
 *
 */
public class ServerListingsScreen implements Screen {
	
	private MenuScreen menuScreen;
	private SeventhGame app;
	
	private UserInterfaceManager uiManager;
	
	private Theme theme;
			
	private Panel optionsPanel;	
	private PanelView<Renderable> panelView;
	
	private boolean isServerInternetOptionsDisplayed;
	private int gameTypeIndex;
	
	private Label noServersFoundLbl;
	private Queue<LeoArray> servers;
	private AtomicBoolean update;
	private ListBox serverListings;
	
	private AtomicBoolean showServersLabel;
	
	/**
	 * 
	 */
	public ServerListingsScreen(final MenuScreen menuScreen) {
		this.menuScreen = menuScreen;
		this.theme = menuScreen.getTheme();		
		this.app = menuScreen.getApp();
		this.uiManager = menuScreen.getUiManager();			
		
		this.isServerInternetOptionsDisplayed = true;
		this.gameTypeIndex = GameType.Type.values().length;
		
		this.showServersLabel = new AtomicBoolean(true);
		
		this.servers = new ConcurrentLinkedQueue<>();
		this.update = new AtomicBoolean();
		
		createUI();
		queryInternetServers();
	}
	
	
	private void createUI() {
		this.panelView = new PanelView<>();
		this.optionsPanel = new Panel();
		
		Vector2f uiPos = new Vector2f(200, app.getScreenHeight() - 30);
	//	final int startX = 200;
		final int startY = (int)uiPos.y;
		
		Button saveBtn = setupButton(uiPos, "Refresh");
		saveBtn.getBounds().setSize(140, 80);
		saveBtn.getTextLabel().setFont(theme.getPrimaryFontName());
		saveBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				queryInternetServers();
			}
		});
		
		final int yInc = 30;
		
		uiPos.x = 200;
		uiPos.y = 200;
		
		final Button serversBtn = setupButton(uiPos, isServerInternetOptionsDisplayed ? "Servers: Internet" : "Servers: LAN");
		serversBtn.getBounds().setSize(140, 20);
		serversBtn.getTextLabel().setFont(theme.getSecondaryFontName());
		serversBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				isServerInternetOptionsDisplayed = !isServerInternetOptionsDisplayed;
				if(isServerInternetOptionsDisplayed) {
					serversBtn.setText("Servers: Internet");
				}
				else {
					serversBtn.setText("Servers: LAN");
				}
				
				refreshConfigUI();
			}
		});
		
		uiPos.y += yInc;
		
		final Button gameTypeBtn = setupButton(uiPos, gameTypeIndex==GameType.Type.values().length ? "Game Type: All" 
				: "Game Type: " + GameType.Type.values()[gameTypeIndex]);
		gameTypeBtn.getBounds().setSize(140, 20);
		gameTypeBtn.getTextLabel().setFont(theme.getSecondaryFontName());
		gameTypeBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				gameTypeIndex++;
				if(gameTypeIndex == GameType.Type.values().length) {
					gameTypeBtn.setText("Game Type: All");
				}
				else {
					if(gameTypeIndex > GameType.Type.values().length) {
						gameTypeIndex = 0;
					}
					gameTypeBtn.setText("Game Type: " + GameType.Type.values()[gameTypeIndex]);
				}
				
				refreshConfigUI();
				queryInternetServers();
			}
		});
		
		
		uiPos.y += yInc;
		uiPos.x = 80;
		
		serverListings = new ListBox();
		serverListings.setBackgroundColor(0xff383e18);
		serverListings.setBounds(new Rectangle((int)uiPos.x, (int)uiPos.y, app.getScreenWidth() - 220, 400));
		
		uiPos.x = 140;
		uiPos.y = 40;
		
		LeoArray entries = servers.poll();
		if(entries!=null) {
			for(LeoObject entry : entries) {
				serverListings.addItem(setupServerEntryButton(uiPos, parseEntry(entry)));
				uiPos.y += yInc;			
			}
		}
		update.set(false);
		
		optionsPanel.addWidget(serverListings);
		panelView.addElement(new ListBoxView<>(serverListings));
		
		uiPos.x = app.getScreenWidth()/2;
		uiPos.y = 650;
		
		this.noServersFoundLbl = new Label("No Servers Found.");
		this.noServersFoundLbl.setBounds(new Rectangle(app.getScreenWidth(), 30));
		this.noServersFoundLbl.getBounds().centerAround(uiPos);
		this.noServersFoundLbl.setTextAlignment(TextAlignment.CENTER);
		if(!this.showServersLabel.get()) {
			noServersFoundLbl.hide();
		}
		
		optionsPanel.addWidget(noServersFoundLbl);
		panelView.addElement(new LabelView(noServersFoundLbl));
		
		uiPos.x = app.getScreenWidth() - 100;
		uiPos.y = 420;
		
		Button upArrow = setupButton(uiPos, "", false, false);
		upArrow.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				serverListings.previousIndex();
			}
		});
		panelView.addElement(new ImageButtonView(upArrow, Art.upArrow));
		
		uiPos.y += yInc*3;
		Button downArrow = setupButton(uiPos, "", false, false);
		downArrow.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				serverListings.nextIndex();
			}
		});
		panelView.addElement(new ImageButtonView(downArrow, Art.downArrow));
		
		uiPos.x = app.getScreenWidth() - 80;
		uiPos.y = startY;
		
		Button cancelBtn = setupButton(uiPos, "Back");
		cancelBtn.getBounds().setSize(140, 80);
		cancelBtn.getTextLabel().setFont(theme.getPrimaryFontName());
		cancelBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				app.setScreen(menuScreen);
				Sounds.playGlobalSound(Sounds.uiNavigate);
			}
		});

	}

	/**
	 * Parses the JSON
	 * @param entry
	 * @return
	 */
	private String[] parseEntry(LeoObject object) {		
		String[] result = {"Error", ""};
		try {
			LeoArray axis = object.getObject("axis").as();
			LeoArray allies = object.getObject("allies").as();
			
			result = new String[] { String.format("%-50s %-10s %d/%d", object.getObject("server_name"), object.getObject("game_type")
					, axis.size() + allies.size(), 12), object.getObject("address") +":"+ object.getObject("port") };
		}
		catch(Exception e) {			
		}
		return result;
	}
	
	private void refreshConfigUI() {
		this.optionsPanel.destroy();
				
		createUI();
	}
	
	private void queryInternetServers() {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				MasterServerApi api = new MasterServerApi(app.getConfig());
				try {
					LeoObject result = api.getServerListings(null);					
					if(LeoObject.isTrue(result)) {
						showServersLabel.set(false);
						if(result.isArray()) {
							LeoArray array = result.as();
//							if(!array.isEmpty()) {
//								
//								LeoObject m = array.get(0);
//								for(int i = 0; i < 12; i++) {
//									LeoMap clone = m.clone().as();
//									clone.setObject("server_name", Leola.toLeoObject("Something Something" +i));
//									array.add(clone);
//								}
//							}
							servers.add(array);
						}												
					}
					else {
						showServersLabel.set(true);
					}
					
					update.set(true);
				} 
				catch (Exception e) {
					Cons.println("Unable to retrieve data from the master server: " + e);
				}
			}
		});
		
		thread.start();
	}
	
	private Button setupServerEntryButton(Vector2f pos, final String[] settings) {
		Button btn = setupButton(pos, settings[0], true, false);
		btn.setBounds(new Rectangle(app.getScreenWidth() - 280, 24));
		btn.getBounds().x += 20;
		btn.setHoverTextSize(19);
		btn.getTextLabel().setForegroundColor(0xffffffff);
		btn.getTextLabel().setFont("Courier New");
		btn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				if(settings!=null && settings.length > 1) {
					String address = settings[1];
					if(address!=null && !"".equals(address)) {
						app.getConsole().execute("connect " + address);
					}
				}
			}
		});
		
		return btn;
	}
	
	private Button setupButton(Vector2f pos, String text) {
		return setupButton(pos, text, true, true);
	}
	
	private Button setupButton(Vector2f pos, final String text, boolean setView, boolean addView) {
		Button btn = new Button();
		btn.setTheme(theme);
		btn.setText(text);
		btn.setBounds(new Rectangle(240, 30));
		if(!setView) {
			btn.setBounds(new Rectangle(24, 24));
			btn.setBorder(false);
		}
		
		btn.getBounds().centerAround(pos);
		btn.setForegroundColor(theme.getForegroundColor());
		btn.setEnableGradiant(false);
		btn.setTextSize(18);
		btn.setHoverTextSize(24);
		
		btn.getTextLabel().setFont(theme.getSecondaryFontName());		
		btn.getTextLabel().setTextAlignment(TextAlignment.LEFT);
		btn.getTextLabel().setForegroundColor(theme.getForegroundColor());
		
		this.optionsPanel.addWidget(btn);
		if(setView && addView) {
			this.panelView.addElement(new ButtonView(btn));
		}
		
		return btn;
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.State#enter()
	 */
	@Override
	public void enter() {
		this.optionsPanel.show();
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
			
		if(update.get()) {
			refreshConfigUI();
		}
	
			
		if(uiManager.isKeyDown(Keys.ESCAPE)) {
			app.setScreen(menuScreen);
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
		String message = "Seventh Servers";
		RenderFont.drawShadedString(canvas, message
				, canvas.getWidth()/2 - canvas.getWidth(message)/2, canvas.getHeight()/9, fontColor);
				
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
