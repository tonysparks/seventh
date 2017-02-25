/*
 * see license.txt 
 */
package seventh.client.gfx;

import leola.frontend.listener.EventDispatcher;
import seventh.client.ClientTeam;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Console;
import seventh.ui.Button;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.Widget;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;

/**
 * A dialog box to pick the weapon class.
 * 
 * @author Tony
 *
 */
public class SwitchTeamDialog extends Widget {

    private ClientTeam team;

    private Label title;
    private Theme theme;
    
    private Button allied, axis, spectator;
    private Button cancel;
    private Console console;
    /**
     */
    public SwitchTeamDialog(Console console, Theme theme) {
        super(new EventDispatcher());
        
        this.console = console;
        this.team = ClientTeam.ALLIES;
        this.theme = theme;
        
        createUI();
    }
    
    /* (non-Javadoc)
     * @see seventh.ui.Widget#setBounds(seventh.math.Rectangle)
     */
    @Override
    public void setBounds(Rectangle bounds) {    
        super.setBounds(bounds);
        
        createUI();
    }
    
    /**
     * @return the team
     */
    public ClientTeam getTeam() {
        return team;
    }
    
    /**
     * @return the allied
     */
    public Button getAllied() {
        return allied;
    }
    
    /**
     * @return the axis
     */
    public Button getAxis() {
        return axis;
    }
    
    /**
     * @return the spectator
     */
    public Button getSpectator() {
        return spectator;
    }
    
    private void createUI() {
        destroyChildren();
        
        Rectangle bounds = getBounds();
        
        if(this.title!=null)this.title.destroy();
        this.title = new Label("Switch Team");
        this.title.setTheme(theme);
        //this.title.setForegroundColor(0xffffffff);
        this.title.setBounds(new Rectangle(bounds));
        this.title.getBounds().height = 30;
        this.title.getBounds().y += 30;
        this.title.setFont(theme.getSecondaryFontName());
        this.title.setTextAlignment(TextAlignment.CENTER);
        this.title.setTextSize(28);
    
        int yInc = 60;        
        Vector2f pos = new Vector2f(bounds.width/2, 100);
        
        if(this.allied!=null) this.allied.destroy();
        this.allied = setupButton(pos, "Allies", "allies"); pos.y += yInc;
        
        if(this.axis!=null) this.axis.destroy();
        this.axis = setupButton(pos, "Axis", "axis"); pos.y += yInc;
        
        if(this.spectator!=null) this.spectator.destroy();
        this.spectator = setupButton(pos, "Spectator", "spectator");
        
        if(this.cancel!=null) this.cancel.destroy();
        this.cancel = new Button();        
        this.cancel.setText("Cancel");
        this.cancel.setBounds(new Rectangle(0,0,100,40));
        this.cancel.getBounds().centerAround(bounds.x+ 200, bounds.y + bounds.height - 20);
        this.cancel.setEnableGradiant(false);
        this.cancel.setTheme(theme);
        this.cancel.getTextLabel().setFont(theme.getSecondaryFontName());
        this.cancel.getTextLabel().setForegroundColor(theme.getForegroundColor());
        this.cancel.setTextSize(22);
        this.cancel.setHoverTextSize(26);
        this.cancel.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                hide();
            }
        });
                
        addWidget(cancel);
        addWidget(title);
    }
    
    
    
    private Button setupButton(Vector2f pos, String text, final String desiredTeam) {
        Button btn = new Button();
        btn.setText(text);
        btn.setBounds(new Rectangle(200, 30));
        btn.getBounds().centerAround(pos);
        btn.setEnableGradiant(false);
        btn.setTheme(theme.newTheme().setForegroundColor(theme.getHoverColor()).setHoverColor(theme.getForegroundColor()));
        btn.getTextLabel().setFont(theme.getSecondaryFontName());
        //btn.getTextLabel().setForegroundColor(theme.getForegroundColor());
        btn.setTextSize(22);
        btn.setHoverTextSize(26);
        btn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                if(!getTeam().getName().equalsIgnoreCase(desiredTeam)) {
                    console.execute("change_team " + desiredTeam);
                }
                hide();
            }
        });
        
        addWidget(btn);
        
        return btn;
    }
    
    /**
     * @return the cancel button
     */
    public Button getCancelBtn() {
        return cancel;
    }
    
    /**
     * @return the title
     */
    public Label getTitle() {
        return title;
    }

    /**
     * @param team the team to set
     */
    public void setTeam(ClientTeam team) {
        this.team = team;        
    }
}
