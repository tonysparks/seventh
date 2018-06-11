/*
 * see license.txt 
 */
package seventh.client.screens;

import java.net.DatagramPacket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.badlogic.gdx.Input.Keys;

import leola.vm.types.LeoArray;
import leola.vm.types.LeoObject;
import seventh.client.SeventhGame;
import seventh.client.gfx.Art;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Theme;
import seventh.client.inputs.Inputs;
import seventh.client.sfx.Sounds;
import seventh.game.type.GameType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.BroadcastListener;
import seventh.shared.BroadcastListener.OnMessageReceivedListener;
import seventh.shared.Broadcaster;
import seventh.shared.Cons;
import seventh.shared.JSON;
import seventh.shared.LANServerConfig;
import seventh.shared.MasterServerClient;
import seventh.shared.ServerInfo;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.ListBox;
import seventh.ui.Panel;
import seventh.ui.UserInterfaceManager;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.HoverEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.events.OnHoverListener;
import seventh.ui.view.ButtonView;
import seventh.ui.view.ImageButtonView;
import seventh.ui.view.LabelView;
import seventh.ui.view.ListBoxView;
import seventh.ui.view.PanelView;

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
    private PanelView panelView;
    
    private boolean isServerInternetOptionsDisplayed;
    private int gameTypeIndex;
    
    private Label noServersFoundLbl;
    private Queue<ServerInfo> servers;
    private AtomicBoolean update;
    private AtomicBoolean queryingLan;
    private ListBox serverListings;
    
    private AtomicBoolean showNoServersLabel;
    
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
        
        this.showNoServersLabel = new AtomicBoolean(true);
        
        this.servers = new ConcurrentLinkedQueue<>();
        this.update = new AtomicBoolean();
        this.queryingLan = new AtomicBoolean();
        
        createUI();
        queryMultiplayerServers();
    }
    
    
    private void createUI() {
        this.panelView = new PanelView();
        this.optionsPanel = new Panel();
        
        Vector2f uiPos = new Vector2f(200, app.getScreenHeight() - 30);
    //    final int startX = 200;
        final int startY = (int)uiPos.y;
        
        Button saveBtn = setupButton(uiPos, "Refresh");
        saveBtn.getBounds().setSize(140, 80);
        saveBtn.getTextLabel().setFont(theme.getPrimaryFontName());
        saveBtn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                queryMultiplayerServers();
            }
        });
        
        final int yInc = 30;
        
        uiPos.x = 200;
        uiPos.y = 100;
        
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
                queryMultiplayerServers();
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
                queryMultiplayerServers();
            }
        });
        
        
        uiPos.y += yInc - 20;
        uiPos.x = 80;
        
        serverListings = new ListBox();
        serverListings.setBackgroundColor(0xff383e18);
        //serverListings.setTheme(theme);
        serverListings.setBounds(new Rectangle((int)uiPos.x, (int)uiPos.y, app.getScreenWidth() - 220, 260));
        serverListings.addColumnHeader("Server Name", 240)
                      .addColumnHeader("Game Type", 150)
                      .addColumnHeader("Map", 120)
                      .addColumnHeader("Players", 80);
        
        uiPos.x = 140;
        uiPos.y = 40;
        
        while(!servers.isEmpty()) {
            ServerInfo info = servers.poll();
            if(info!=null) {
                serverListings.addItem(setupServerEntryButton(uiPos, parseEntry(info)));
                uiPos.y += yInc;            
            }
        }
        update.set(false);
        
        optionsPanel.addWidget(serverListings);
        panelView.addElement(new ListBoxView<>(serverListings));
        
        uiPos.x = app.getScreenWidth()/2 - 20;
        uiPos.y = 390;
        
        this.noServersFoundLbl = new Label("No Servers Found.");
        this.noServersFoundLbl.setFont(theme.getSecondaryFontName());
        this.noServersFoundLbl.setTextSize(18);
        this.noServersFoundLbl.setBounds(new Rectangle(app.getScreenWidth(), 30));
        this.noServersFoundLbl.getBounds().centerAround(uiPos);
        this.noServersFoundLbl.setHorizontalTextAlignment(TextAlignment.CENTER);        
        checkServerListingLabel();
        
        optionsPanel.addWidget(noServersFoundLbl);
        panelView.addElement(new LabelView(noServersFoundLbl));
        
        uiPos.x = app.getScreenWidth() - 100;
        uiPos.y = 220;
        
        Button upArrow = setupButton(uiPos, "", false, false);
        //upArrow.setForegroundColor(0xff282c0c);
        upArrow.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                serverListings.previousIndex();
            }
        });
        panelView.addElement(new ImageButtonView(upArrow, null, Art.upArrow, null));
        
        uiPos.y += yInc*3;
        Button downArrow = setupButton(uiPos, "", false, false);
        //downArrow.setForegroundColor(0xff282c0c);
        downArrow.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                serverListings.nextIndex();
            }
        });
        panelView.addElement(new ImageButtonView(downArrow, null, Art.downArrow, null));
        
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
    private String[] parseEntry(ServerInfo info) {        
        String[] result = {"Error", ""};
        try {
            result = new String[] { 
                    String.format("%-35s %-12s %-23s %d/%d", 
                            info.getServerName(), info.getGameType(), info.getMapName(), info.getAxis().size() + info.getAllies().size(), 12), 
                    info.getAddress() +":"+ info.getPort() 
            };
        }
        catch(Exception e) {            
        }
        return result;
    }
    
    private void refreshConfigUI() {
        this.optionsPanel.destroy();
                
        createUI();
    }
    
    private void queryMultiplayerServers() {
        if(isServerInternetOptionsDisplayed) {
            queryInternetServers();
        }
        else {
            queryLANServer();
        }
        
        checkServerListingLabel();
    }
    
    private void checkServerListingLabel() {
        if(!this.showNoServersLabel.get()) {
            noServersFoundLbl.hide();
        }
    }
    
    private void queryInternetServers() {
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                servers.clear();
                
                MasterServerClient api = new MasterServerClient(app.getConfig().getMasterServerConfig());
                try {
                    String gameType = gameTypeIndex>=0 && gameTypeIndex < GameType.Type.values().length ? GameType.Type.values()[gameTypeIndex].name() : null; 
                    LeoObject result = api.getServerListings(gameType);                    
                    if(LeoObject.isTrue(result)) {
                        
                        if(result.isArray()) {
                            LeoArray array = result.as();
                            showNoServersLabel.set(array.isEmpty());
                            for(LeoObject obj : array) {
                                ServerInfo info = new ServerInfo(obj);
                                servers.add(info);
                            }
                        }                                                
                    }
                    
                    showNoServersLabel.set(servers.isEmpty());
                    
                    update.set(true);
                } 
                catch (Exception e) {
                    Cons.println("Unable to retrieve data from the master server: " + e);
                }
            }
        });
        
        thread.start();
    }
    
    private synchronized void queryLANServer() {
        if(this.queryingLan.get()) {
            return;
        }
        
        this.queryingLan.set(true);
        this.servers.clear();
        
        LANServerConfig config = app.getConfig().getLANServerConfig();
        try {
            final BroadcastListener listener = new BroadcastListener(config.getMtu(), config.getBroadcastAddress(), config.getPort());
        
            listener.addOnMessageReceivedListener(new OnMessageReceivedListener() {
                
                @Override
                public void onMessage(DatagramPacket packet) {
                    String msg = new String(packet.getData(), packet.getOffset(), packet.getLength()).trim();
                    try {
                        if(!msg.equalsIgnoreCase("ping")) {
                            LeoObject object = JSON.parseJson(msg);
                            ServerInfo info = new ServerInfo(object);
                            boolean filter = false;
                            String gameType = gameTypeIndex>=0 && gameTypeIndex < GameType.Type.values().length ? 
                                                GameType.Type.values()[gameTypeIndex].name() : null;
                                                
                            if(gameType != null) {
                                String type = info.getGameType();
                                if(!type.equalsIgnoreCase(gameType)) {
                                    filter = true;
                                }
                            }
                            
                            if(!filter) {
                                if(!servers.contains(info)) {                                    
                                    servers.add(info);
                                }
                            }
                        }
                    } 
                    catch (Exception e) {
                        Cons.println("*** ERROR: Problem parsing response message from LAN server: " + e);                                
                    }                                                        
                }
            });
            
            
            Thread serverThread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        listener.start();
                    }                    
                    catch(Exception e) {
                        if(!e.toString().contains("socket closed")) {
                            Cons.println("*** ERROR: Problem with listening for broadcast messages from LAN server: " + e);
                        }
                    }
                    finally {
                        queryingLan.set(false);
                    }
                }
            }, "client-lan-response-listener");
            serverThread.setDaemon(true);
            serverThread.start();
            
            Thread pingThread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    
                    
                    try {                    
                        LANServerConfig config = app.getConfig().getLANServerConfig();
                        try(Broadcaster caster = new Broadcaster(config.getMtu(), config.getBroadcastAddress(), config.getPort())) {
                            caster.broadcastMessage("ping");
                            Thread.sleep(1_000);
                            
                            int attempts = 0;
                            while(/*servers.isEmpty()&&*/attempts<2) {
                                caster.broadcastMessage("ping");
                                Thread.sleep(1_000);    
                                attempts++;
                            }
                        }
                        
                        listener.close();
                        showNoServersLabel.set(servers.isEmpty());
                        update.set(true);
                    } 
                    catch (Exception e) {
                        Cons.println("*** ERROR: Unable to retrieve LAN data: " + e);
                    }
                }
            }, "client-lan-request");
            pingThread.setDaemon(true);
            pingThread.start();
        }
        catch(Exception e) {
            Cons.println("*** ERROR: Unable to retrieve LAN data: " + e);
        }
    }
    
    private Button setupServerEntryButton(Vector2f pos, final String[] settings) {
        Button btn = setupButton(pos, settings[0], true, false);
        btn.setBounds(new Rectangle(app.getScreenWidth() - 280, 24));
        btn.getBounds().x += 20;
        btn.setTextSize(11);
        btn.setHoverTextSize(11);
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
        this.uiManager.update(timeStep);
        this.panelView.update(timeStep);
        
        uiManager.checkIfCursorIsHovering();
                
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
    public void render(Canvas canvas, float alpha) {
        canvas.fillRect(0, 0, canvas.getWidth() + 100,  canvas.getHeight() + 100, theme.getBackgroundColor());
        
        this.panelView.render(canvas, null, 0);
        
        canvas.begin();
        canvas.setFont(theme.getPrimaryFontName(), 34);
        canvas.boldFont();
        
        int fontColor = theme.getForegroundColor();
        String message = "Servers";
        RenderFont.drawShadedString(canvas, message
                , canvas.getWidth()/2 - RenderFont.getTextWidth(canvas, message)/2, canvas.getHeight()/12, fontColor);
                
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
