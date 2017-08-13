/*
 * see license.txt 
 */
package seventh.client.screens;

import com.badlogic.gdx.Input.Keys;

import seventh.client.SeventhGame;
import seventh.client.gfx.Art;
import seventh.client.inputs.Inputs;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.server.GameServer.GameServerSettings;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.Panel;
import seventh.ui.TextBox;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.view.LabelView;
import seventh.ui.view.PanelView;

/**
 * @author Tony
 *
 */
public class ServerTeamsSetupScreen extends AbstractServerSetupScreen {

    private boolean load;

    /**
     * 
     */
    public ServerTeamsSetupScreen(SeventhGame app, GameServerSettings gameSettings) {
        super(app, gameSettings);
    }

    @Override
    protected void createUI() {
        this.panelView = new PanelView();
        this.optionsPanel = new Panel();

        Label headerLbl = new Label("Game Setup");
        headerLbl.setTheme(theme);
        headerLbl.setBounds(new Rectangle(0, 0, app.getScreenWidth(), 80));
        headerLbl.setHorizontalTextAlignment(TextAlignment.CENTER);
        headerLbl.setFont(theme.getPrimaryFontName());
        headerLbl.setTextSize(34);
        this.panelView.addElement(new LabelView(headerLbl));

        Vector2f uiPos = new Vector2f(180, app.getScreenHeight() - 30);

        Button saveBtn = setupButton(uiPos, "Play", false);
        saveBtn.getTextLabel().setFont(theme.getPrimaryFontName());
        saveBtn.addOnButtonClickedListener(new OnButtonClickedListener() {

            @Override
            public void onButtonClicked(ButtonEvent event) {
                load = true;
                if (app.getTerminal().getInputText().equals("connect ")) {
                    app.getTerminal().setInputText("");
                }
            }
        });

        uiPos.x = app.getScreenWidth() - 140;

        Button cancelBtn = setupButton(uiPos, "Back", false);
        cancelBtn.getTextLabel().setFont(theme.getPrimaryFontName());
        cancelBtn.addOnButtonClickedListener(new OnButtonClickedListener() {

            @Override
            public void onButtonClicked(ButtonEvent event) {
                app.popScreen();
            }
        });

        // main panel

        final int startX = 30;
        final int startY = 70;
        final int yInc = 15;

        uiPos.x = startX;
        uiPos.y = startY;

        setupLabel(uiPos, "Teams", true);

        uiPos.x = startX + 128;
        uiPos.y += yInc + 5;
        
        setupImagePanel(uiPos, Art.alliedIcon);
        
        uiPos.x = startX + 100;
        uiPos.y += yInc*3 - 25;
        
        setupButton(uiPos, "-", false).addOnButtonClickedListener(new OnButtonClickedListener() {

            @Override
            public void onButtonClicked(ButtonEvent event) {
                if (gameSettings.alliedTeam.size() > 0) {
                    gameSettings.alliedTeam.remove(gameSettings.alliedTeam.size() - 1);
                    refreshUI();
                }
            }
        });
                
        uiPos.x += 10;
        uiPos.y -= 20;
                
        setupLabel(uiPos, "Allies", false);

        uiPos.x += 100;
        uiPos.y += 20;
        setupButton(uiPos, "+", false).addOnButtonClickedListener(new OnButtonClickedListener() {

            @Override
            public void onButtonClicked(ButtonEvent event) {
                if (gameSettings.axisTeam.size() + gameSettings.alliedTeam.size() < gameSettings.maxPlayers - 1) {
                    String name = getNextRandomName();
                    gameSettings.alliedTeam.add(name);

                    refreshUI();
                }
            }
        });

        uiPos.x = 550;
        setupButton(uiPos, "-", false).addOnButtonClickedListener(new OnButtonClickedListener() {

            @Override
            public void onButtonClicked(ButtonEvent event) {
                if (gameSettings.axisTeam.size() > 0) {
                    gameSettings.axisTeam.remove(gameSettings.axisTeam.size() - 1);
                    refreshUI();
                }
            }
        });
        
        Vector2f otherPos = new Vector2f(uiPos);
        otherPos.x += 28;
        otherPos.y -= 20;
        
        setupImagePanel(otherPos, Art.axisIcon);

        uiPos.x += 20;
        uiPos.y -= 20;
        
        setupLabel(uiPos, "Axis", false);

        uiPos.x += 90;
        uiPos.y += 20;
        setupButton(uiPos, "+", false).addOnButtonClickedListener(new OnButtonClickedListener() {

            @Override
            public void onButtonClicked(ButtonEvent event) {
                if (gameSettings.axisTeam.size() + gameSettings.alliedTeam.size() < gameSettings.maxPlayers - 1) {

                    String name = getNextRandomName();
                    gameSettings.axisTeam.add(name);

                    refreshUI();
                }
            }
        });

        uiPos.x = startX;
        uiPos.y += yInc;
        createBotsPanel(uiPos);

    }



    private void createBotsPanel(Vector2f pos) {

        // int startX = (int)pos.x;
        int startY = (int) pos.y;

        final int xInc = 170;
        final int yInc = 25;
        
        int i = 0;
        for (String name : gameSettings.alliedTeam) {
            final int nameIndex = i++;
            final TextBox box = setupTextBox(pos, name);
            box.addInputListenerToFront(new Inputs() {

                @Override
                public boolean keyUp(int key) {
                    gameSettings.alliedTeam.set(nameIndex, box.getText());
                    return false;
                }
            });

            if (i % 2 == 0) {
                pos.x -= xInc;
                pos.y += yInc;
            } else {
                pos.x += xInc;
            }

        }

        pos.x = 450;
        pos.y = startY;

        i = 0;
        for (String name : gameSettings.axisTeam) {
            final int nameIndex = i++;
            final TextBox box = setupTextBox(pos, name);
            box.addInputListenerToFront(new Inputs() {

                @Override
                public boolean keyUp(int key) {
                    gameSettings.axisTeam.set(nameIndex, box.getText());
                    return false;
                }
            });

            if (i % 2 == 0) {
                pos.x -= xInc;
                pos.y += yInc;
            } else {
                pos.x += xInc;
            }
        }

    }

    @Override
    public void update(TimeStep timeStep) {
        super.update(timeStep);

        if(load) {
            app.getMenuScreen().startLocalServer(gameSettings);
            load = false;
        }
        
        if(uiManager.isKeyDown(Keys.ESCAPE)) {
            app.popScreen();
        }
    }

}
