/*
 * see license.txt 
 */
package seventh.client.screens;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.SeventhGame;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Theme;
import seventh.client.inputs.Inputs;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.server.GameServer.GameServerSettings;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.ImagePanel;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.Panel;
import seventh.ui.TextBox;
import seventh.ui.UserInterfaceManager;
import seventh.ui.events.HoverEvent;
import seventh.ui.events.OnHoverListener;
import seventh.ui.view.ButtonView;
import seventh.ui.view.ImagePanelView;
import seventh.ui.view.LabelView;
import seventh.ui.view.PanelView;
import seventh.ui.view.TextBoxView;

/**
 * @author Tony
 *
 */
public abstract class AbstractServerSetupScreen implements Screen {

    protected SeventhGame app;
    protected GameServerSettings gameSettings;
    
    
    protected UserInterfaceManager uiManager;    
    protected Theme theme;
    
    protected Panel optionsPanel;    
    protected PanelView panelView;
    
    protected Random random;
    
    private static final String[] BOT_NAMES = {
        "[b] Messiah",
        "[b] Irishman",
        "[b] Outlaw",
        "[b] Osiris",
        "[b] Newera",
        "[b] Uki",
        "[b] Misty",
        "[b] Randy",
        "[b] Jeremy",
        "[b] Tripwire",
        "[b] Leo",
        "[b] nvm",
        "[b] Ella",
        
    };

    
    /**
     * @param app
     * @param gameSettings
     */
    public AbstractServerSetupScreen(SeventhGame app, GameServerSettings gameSettings) {
        this.app = app;
        this.gameSettings = gameSettings;
        
        this.uiManager = app.getUiManager();
        this.theme = app.getTheme();
        
        this.random = new Random();        
    }
    
    
    @Override
    public Inputs getInputs() {     
        return this.uiManager;
    }

    protected Button setupButton(Vector2f pos, final String text, boolean smallBtn) {
        Button btn = new Button();
        btn.setTheme(theme);
        btn.setText(text);
        if(smallBtn) {
            btn.setTextSize(16);
            btn.setHoverTextSize(18);
            btn.setBounds(new Rectangle(40, 30));            
        }
        else { 
            btn.setTextSize(24);
            btn.setHoverTextSize(28);
            btn.setBounds(new Rectangle(140, 40));
            
            if(text.length() == 1) {
                btn.setBounds(new Rectangle(40, 30));
            }
            else {
                btn.setBounds(new Rectangle(140, 40));    
            }
        }
        
        btn.getBounds().centerAround(pos);
        btn.setForegroundColor(theme.getForegroundColor());
        btn.setEnableGradiant(false);            
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
        this.panelView.addElement(new ButtonView(btn));
        
        return btn;
    }
    
    protected Label setupLabel(Vector2f pos, final String text, boolean header) {
        Label lbl = new Label(text);
        lbl.setTheme(theme);
        lbl.setBounds(new Rectangle((int)pos.x, (int)pos.y, 400, 40));
        lbl.setHorizontalTextAlignment(TextAlignment.LEFT);
        if(header) {
            lbl.setForegroundColor(theme.getForegroundColor());
            lbl.setFont(theme.getSecondaryFontName());
            lbl.setTextSize(22);
        }
        else {
            lbl.setForegroundColor(0xffffffff);
            lbl.setFont(theme.getSecondaryFontName());
            lbl.setTextSize(16);
        }
        
        this.optionsPanel.addWidget(lbl);
        this.panelView.addElement(new LabelView(lbl));
        
        return lbl;
    }
    
    
    protected TextBox setupTextBox(Vector2f pos, final String text) {
        TextBox box = new TextBox();
        box.setBounds(new Rectangle((int)pos.x, (int)pos.y, 120, 20));
        box.getTextLabel().setFont(theme.getSecondaryFontName());
        box.setText(text);
        box.setMaxSize(16);
        box.setTextSize(14);        
        box.setFocus(false);
        
        this.optionsPanel.addWidget(box);
        this.panelView.addElement(new TextBoxView(box));
        
        return box;
    }
    
    protected ImagePanel setupImagePanel(Vector2f pos, TextureRegion image) {
        ImagePanel panel = new ImagePanel(image);
        panel.setBounds(new Rectangle((int)pos.x, (int)pos.y, image.getRegionWidth(), image.getRegionHeight()));
        panel.setBackgroundColor(0x00000000);
        panel.setForegroundColor(0x00000000);
        
        this.optionsPanel.addWidget(panel);
        this.panelView.addElement(new ImagePanelView(panel));
        
        return panel;
    }
    
    protected String getNextRandomName() {
        String name = null;
        boolean found = false;
        final int maxIterations = 100;
        int i = 0;
        while (!found) {
            name = BOT_NAMES[this.random.nextInt(BOT_NAMES.length)];
            i++;

            if (i > maxIterations) {
                found = true;
            }

            if (gameSettings.alliedTeam.contains(name)) {
                continue;
            }

            if (gameSettings.axisTeam.contains(name)) {
                continue;
            }

            found = true;
        }

        return name;
    }
    
    protected abstract void createUI();
    
    protected void refreshUI() {
        if(this.panelView!=null) {
            this.panelView.clear();
        }
        if(optionsPanel!=null) {
            optionsPanel.destroy();
        }
        
        createUI();
    }
    
    @Override
    public void enter() {
        refreshUI();
        
        this.optionsPanel.show();        
    }

    @Override
    public void exit() {
        this.optionsPanel.destroy();
        this.panelView.clear();
    }
    
    @Override
    public void destroy() {
        this.optionsPanel.destroy();
        this.panelView.clear();        
    }

    @Override
    public void update(TimeStep timeStep) {
        uiManager.update(timeStep);
        uiManager.checkIfCursorIsHovering();
               
        panelView.update(timeStep);        
    }        

    @Override
    public void render(Canvas canvas, float alpha) {
        canvas.fillRect(0, 0, canvas.getWidth() + 100,  canvas.getHeight() + 100, theme.getBackgroundColor());
        
        this.panelView.render(canvas, null, 0);
        
        canvas.begin();
        this.uiManager.render(canvas);
        canvas.end();
    }
}
