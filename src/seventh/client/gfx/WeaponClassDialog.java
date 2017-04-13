/*
 * see license.txt 
 */
package seventh.client.gfx;

import leola.frontend.listener.EventDispatcher;
import seventh.client.ClientTeam;
import seventh.client.network.ClientConnection;
import seventh.game.entities.Entity.Type;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.network.messages.PlayerSwitchWeaponClassMessage;
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
public class WeaponClassDialog extends Widget {

    private ClientTeam team;

    private Label title;
    private Theme theme;
    
    private Button[] weaponClasses;
    private Label[] weaponClassDescriptions;
    private Button cancel;
    
    private ClientConnection connection;
    private InGameOptionsDialog owner;
    /**
     */
    public WeaponClassDialog(InGameOptionsDialog owner, ClientConnection network, Theme theme) {
        super(new EventDispatcher());
        
        this.owner = owner;
        this.connection = network;
        
        this.team = ClientTeam.ALLIES;
        this.theme = theme;

        this.weaponClasses = new Button[6];
        this.weaponClassDescriptions = new Label[6];
        
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
    
    /**
     * @return the weaponClassDescriptions
     */
    public Label[] getWeaponClassDescriptions() {
        return weaponClassDescriptions;
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
        this.title.setHorizontalTextAlignment(TextAlignment.CENTER);
        this.title.setTextSize(28);
                    
        refreshButtons();

        this.cancel = new Button();        
        this.cancel.setText("Cancel");
        this.cancel.setBounds(new Rectangle(0,0,100,40));
        this.cancel.getBounds().centerAround(bounds.x + 225, bounds.y + bounds.height - 20);
        this.cancel.setEnableGradiant(false);
        this.cancel.setTheme(theme);
        this.cancel.getTextLabel().setFont(theme.getSecondaryFontName());
        this.cancel.getTextLabel().setForegroundColor(theme.getForegroundColor());
        this.cancel.setTextSize(22);
        this.cancel.setHoverTextSize(26);
        this.cancel.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                owner.close();
            }
        });
                
        addWidget(cancel);
        addWidget(title);
    }
        
    private Vector2f refreshButtons() {
        Rectangle bounds = getBounds();
        
        Vector2f pos = new Vector2f();
        pos.x = bounds.x + 120;
        pos.y = bounds.y + 70;
        
        int yInc = 90;
        
        
        for(int i = 0; i < weaponClasses.length; i++) {
            if( this.weaponClasses[i] != null ) {
                removeWidget(weaponClasses[i]);
                this.weaponClasses[i].destroy();
                this.weaponClasses[i] = null;
                
                removeWidget(this.weaponClassDescriptions[i]);
                this.weaponClassDescriptions[i].destroy();
                this.weaponClassDescriptions[i] = null;
            }
        }
        
        
        switch(team) {
            case ALLIES:
                this.weaponClasses[0] =setupButton(pos, Type.THOMPSON); 
                this.weaponClassDescriptions[0] = setupLabel(pos, Type.THOMPSON); pos.y += yInc;
                
                this.weaponClasses[1] =setupButton(pos, Type.M1_GARAND); 
                this.weaponClassDescriptions[1] = setupLabel(pos, Type.M1_GARAND); pos.y += yInc;
                
                this.weaponClasses[2] =setupButton(pos, Type.SPRINGFIELD); 
                this.weaponClassDescriptions[2] = setupLabel(pos, Type.SPRINGFIELD); pos.y += yInc;
                break;
            case AXIS:
                this.weaponClasses[0] =setupButton(pos, Type.MP40); 
                this.weaponClassDescriptions[0] = setupLabel(pos, Type.MP40); pos.y += yInc;
                
                this.weaponClasses[1] =setupButton(pos, Type.MP44);                
                this.weaponClassDescriptions[1] = setupLabel(pos, Type.MP44); pos.y += yInc;
                
                this.weaponClasses[2] =setupButton(pos, Type.KAR98); 
                this.weaponClassDescriptions[2] = setupLabel(pos, Type.KAR98); pos.y += yInc;
                break;        
            default:
                break;
            
        }        
        this.weaponClasses[3] =setupButton(pos, Type.RISKER); 
        this.weaponClassDescriptions[3] = setupLabel(pos, Type.RISKER); pos.y += yInc;
        
        this.weaponClasses[4] =setupButton(pos, Type.SHOTGUN); 
        this.weaponClassDescriptions[4] = setupLabel(pos, Type.SHOTGUN); pos.y += yInc;
        
        this.weaponClasses[5] =setupButton(pos, Type.ROCKET_LAUNCHER);
        this.weaponClassDescriptions[5] = setupLabel(pos, Type.ROCKET_LAUNCHER); 
                
        return pos;
    }
    
    private String getClassDescription(Type type) {
        String message = "";
        switch(type) {
            case THOMPSON:
                message = "Thompson | 30/180 rnds\n" +
                          "Pistol | 9/27 rnds \n" +
                          "2 Frag Grenades";
                break;
            case M1_GARAND:                
                message = "M1 Garand | 8/40 rnds\n" +
                          "Pistol | 9/27 rnds \n" +
                          "2 Smoke Grenades";
                break;
            case SPRINGFIELD:                
                message = "Springfield | 5/35 rnds\n" +
                          "Pistol | 9/27 rnds \n" +
                          "1 Frag Grenades";
                break;
            case MP40:
                message = "MP40 | 32/160 rnds\n" +
                          "Pistol | 9/27 rnds \n" +
                          "2 Frag Grenades";
                break;
            case MP44:                
                message = "MP44 | 30/120 rnds\n" +
                          "Pistol | 9/27 rnds \n" +
                          "2 Smoke Grenades";
                break;
            case KAR98:                
                message = "KAR-98 | 5/25 rnds\n" +
                          "Pistol | 9/27 rnds \n" +
                          "1 Frag Grenade";
                break;
            case RISKER:                
                message = "MG-z | 21/42 rnds\n" +
                          "Pistol | 9/27 rnds";
                break;
            case SHOTGUN:                
                message = "Shotgun | 5/35 rnds\n" +
                          "Pistol | 9/27 rnds";
                break;
            case ROCKET_LAUNCHER:                
                message = "M1 | 5 rnds\n" +
                          "Pistol | 9/27 rnds\n" +
                          "5 Frag Grenades";
                break;
            default:;
        }
        
        return message;
    }
    
    
    private Button setupButton(Vector2f pos, final Type type) {
        final Button btn = new Button();
        btn.setBounds(new Rectangle((int)pos.x, (int)pos.y, 320, 80));
        btn.setBorder(false);
//        btn.setText(getClassDescription(type));
//        btn.setTextSize(12);
//        btn.setHoverTextSize(12);
//        btn.getTextLabel().setForegroundColor(this.theme.getForegroundColor());
//        btn.getTextLabel().setHorizontalTextAlignment(TextAlignment.LEFT);
//        btn.getTextLabel().setVerticalTextAlignment(TextAlignment.BOTTOM);
                
        btn.addOnButtonClickedListener(new OnButtonClickedListener() {
            
            @Override
            public void onButtonClicked(ButtonEvent event) {
                if(connection.isConnected()) {
                    PlayerSwitchWeaponClassMessage msg = new PlayerSwitchWeaponClassMessage();
                    msg.weaponType = type.netValue();
                    connection.getClientProtocol().sendPlayerSwitchWeaponClassMessage(msg);
                }
                
                owner.close();
            }
        });
        
        addWidget(btn);
        
        return btn;
    }
    
    private Label setupLabel(Vector2f pos, final Type type) {
        Label lbl = new Label(this.getClassDescription(type));
        lbl.setBounds(new Rectangle((int)pos.x + 80, (int)pos.y, 220, 80));
        lbl.setTextSize(14);
        lbl.setForegroundColor(this.theme.getForegroundColor()); //0xff363e0f
        lbl.setShadow(false);
        lbl.setFont("Consola");
        lbl.setHorizontalTextAlignment(TextAlignment.LEFT);
        lbl.setVerticalTextAlignment(TextAlignment.TOP);
        lbl.hide();
        
        addWidget(lbl);
        
        return lbl;
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
            owner.close();
        }
    }
    
    @Override
    public void show() {     
        super.show();
        
        for(Label lbl : weaponClassDescriptions) {
            lbl.hide();
        }
    }
}
