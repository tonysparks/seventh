/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import leola.frontend.listener.EventDispatcher;
import seventh.client.ClientTeam;
import seventh.client.network.ClientConnection;
import seventh.game.entities.Entity.Type;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.network.messages.PlayerSwitchWeaponClassMessage;
import seventh.ui.Button;
import seventh.ui.Label;
import seventh.ui.Widget;
import seventh.ui.Label.TextAlignment;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;

/**
 * A dialog box to pick the weapon class.
 * 
 * @author Tony
 *
 */
public class WeaponClassDialog extends Widget {

    private ClientTeam team;

    private Label title;
    private Theme theme;
    
    private Button[] weaponClasses;
    private Button cancel;
    
    private ClientConnection connection;
    
    /**
     */
    public WeaponClassDialog(ClientConnection network, Theme theme) {
        super(new EventDispatcher());
        
        this.connection = network;
        
        this.team = ClientTeam.ALLIES;
        this.theme = theme;

        this.weaponClasses = new Button[6];
        
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
     * @return the weaponClasses
     */
    public Button[] getWeaponClasses() {
        return weaponClasses;
    }
    
    private void createUI() {
        destroyChildren();
        
        Rectangle bounds = getBounds();
        
        this.title = new Label("Select a Weapon");
        this.title.setTheme(theme);
        //this.title.setForegroundColor(0xffffffff);
        this.title.setBounds(new Rectangle(bounds));
        this.title.getBounds().height = 30;
        this.title.getBounds().y += 30;
        this.title.setFont(theme.getSecondaryFontName());
        this.title.setTextAlignment(TextAlignment.CENTER);
        this.title.setTextSize(28);
                    
        refreshButtons();

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
        
    private Vector2f refreshButtons() {
        Rectangle bounds = getBounds();
        
        Vector2f pos = new Vector2f();
        pos.x = bounds.x + 100;
        pos.y = bounds.y + 100;
        
        int yInc = 90;
        
        
        for(int i = 0; i < weaponClasses.length; i++) {
            if( this.weaponClasses[i] != null ) {
                removeWidget(weaponClasses[i]);
                this.weaponClasses[i].destroy();
                this.weaponClasses[i] = null;
            }
        }
        
        
        switch(team) {
            case ALLIES:
                this.weaponClasses[0] =setupButton(pos, Art.thompsonIcon, Type.THOMPSON); pos.y += yInc;
                this.weaponClasses[1] =setupButton(pos, Art.m1GarandIcon, Type.M1_GARAND); pos.y += yInc;
                this.weaponClasses[2] =setupButton(pos, Art.springfieldIcon, Type.SPRINGFIELD); pos.y += yInc;
                break;
            case AXIS:
                this.weaponClasses[0] =setupButton(pos, Art.mp40Icon, Type.MP40); pos.y += yInc;
                this.weaponClasses[1] =setupButton(pos, Art.mp44Icon, Type.MP44); pos.y += yInc;
                this.weaponClasses[2] =setupButton(pos, Art.kar98Icon, Type.KAR98); pos.y += yInc;
                break;        
            default:
                break;
            
        }        
        this.weaponClasses[3] =setupButton(pos, Art.riskerIcon, Type.RISKER); pos.y += yInc;
        this.weaponClasses[4] =setupButton(pos, Art.shotgunIcon, Type.SHOTGUN); pos.y += yInc;
        this.weaponClasses[5] =setupButton(pos, Art.rocketIcon, Type.ROCKET_LAUNCHER);
                
        return pos;
    }
    
    private Button setupButton(Vector2f pos, TextureRegion tex, final Type type) {
        Button btn = new Button();
        btn.setBounds(new Rectangle((int)pos.x, (int)pos.y, 200, 80));
        btn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                if(connection.isConnected()) {
                    PlayerSwitchWeaponClassMessage msg = new PlayerSwitchWeaponClassMessage();
                    msg.weaponType = type.netValue();
                    connection.getClientProtocol().sendPlayerSwitchWeaponClassMessage(msg);
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
        refreshButtons();
        
        if(this.isDisabled()) {
            hide();
        }
    }
}
