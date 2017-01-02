/*
 * see license.txt 
 */
package seventh.client.gfx;

import leola.frontend.listener.EventDispatcher;
import seventh.client.ClientTeam;
import seventh.client.network.ClientConnection;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Console;
import seventh.ui.Button;
import seventh.ui.Label;
import seventh.ui.Label.TextAlignment;
import seventh.ui.Widget;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.HoverEvent;
import seventh.ui.events.OnButtonClickedListener;
import seventh.ui.events.OnHoverListener;

/**
 * A dialog box to pick the weapon class.
 * 
 * @author Tony
 *
 */
public class InGameOptionsDialog extends Widget {

	private Label title;
	private Theme theme;
	
	private Button switchTeam;
	private Button options;
	private Button weaponClasses;
	private Button leaveGame;
	
	private WeaponClassDialog weaponDialog;
	private SwitchTeamDialog switchTeamDialog;
	
	/**
	 */
	public InGameOptionsDialog(Console console, ClientConnection network, Theme theme) {
		super(new EventDispatcher());
		this.theme = theme;
				
		this.weaponDialog = new WeaponClassDialog(network, theme);
		this.weaponDialog.setBounds(new Rectangle(400, 680));
		this.weaponDialog.hide();
		
		this.switchTeamDialog = new SwitchTeamDialog(console, theme);		
		this.switchTeamDialog.setBounds(new Rectangle(400, 380));
		this.switchTeamDialog.hide();
		
		createUI();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ui.Widget#destroy()
	 */
	@Override
	public void destroy() {	
		super.destroy();
		this.weaponDialog.destroy();
		this.switchTeamDialog.destroy();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ui.Widget#isDisabled()
	 */
	@Override
	public boolean isDisabled() {	
		return super.isDisabled();// || this.weaponDialog.isDisabled();
	}
	
	/**
	 * @return true if this dialog (or sub dialogs) are open
	 */
	public boolean isOpen() {
		return !super.isDisabled() || !this.weaponDialog.isDisabled() || !this.switchTeamDialog.isDisabled();
	}
	
	/**
	 * Closes this dialog window
	 */
	public void close() {
		hide();
		this.weaponDialog.hide();
		this.switchTeamDialog.hide();
	}
	
	/**
	 * @return the weaponDialog
	 */
	public WeaponClassDialog getWeaponDialog() {
		return weaponDialog;
	}
	
	/**
	 * @return the switchTeamDialog
	 */
	public SwitchTeamDialog getSwitchTeamDialog() {
		return switchTeamDialog;
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
	 * @param team the team to set
	 */
	public void setTeam(ClientTeam team) {		
		this.weaponDialog.setTeam(team);
		this.switchTeamDialog.setTeam(team);
	}
	
	
	private void createUI() {
		destroyChildren();
		
		Rectangle bounds = getBounds();
		
		if(this.title!=null) title.destroy();
		this.title = new Label("Options");
		this.title.setTheme(theme);
		//this.title.setForegroundColor(0xffffffff);
		this.title.setBounds(new Rectangle(bounds));
		this.title.getBounds().height = 30;
		this.title.getBounds().y += 30;
		this.title.setFont(theme.getSecondaryFontName());
		this.title.setTextAlignment(TextAlignment.CENTER);
		this.title.setTextSize(32);
	
				
		int yInc = 60;		
		Vector2f pos = new Vector2f(bounds.width/2, 100);
		
		if(this.weaponClasses!=null) this.weaponClasses.destroy();
		this.weaponClasses =setupButton(pos, "Pick Weapon"); pos.y += yInc;
		this.weaponClasses.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				weaponDialog.getBounds().setLocation(getBounds().x, getBounds().y-150);
				weaponDialog.show();				
			}
		});
		
		if(this.switchTeam!=null) this.switchTeam.destroy();
		this.switchTeam =setupButton(pos, "Change Team"); pos.y += yInc;
		this.switchTeam.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {
				switchTeamDialog.getBounds().setLocation(getBounds().x, getBounds().y);
				switchTeamDialog.show();
			}
		});
		
		if(this.options!=null) this.options.destroy();
		this.options =setupButton(pos, "Options");
		
		
		if(this.leaveGame!=null) this.leaveGame.destroy();
		this.leaveGame = new Button();		
		this.leaveGame.setText("Quit Game");
		this.leaveGame.setBounds(new Rectangle(0,0,100,40));
		this.leaveGame.getBounds().centerAround(bounds.x+ 200, bounds.y + bounds.height - 20);
		this.leaveGame.setEnableGradiant(false);
		this.leaveGame.getTextLabel().setFont(theme.getSecondaryFontName());
		this.leaveGame.getTextLabel().setForegroundColor(theme.getForegroundColor());
		this.leaveGame.setTextSize(22);
		this.leaveGame.setHoverTextSize(26);
				
		addWidget(leaveGame);
		addWidget(title);
	}
	
	private Button setupButton(Vector2f pos, String text) {
		Button btn = new Button();
		btn.setText(text);
		btn.setBounds(new Rectangle(200, 30));
		btn.getBounds().centerAround(pos);
		btn.setEnableGradiant(false);
		btn.getTextLabel().setFont(theme.getSecondaryFontName());
		//btn.getTextLabel().setForegroundColor(theme.getForegroundColor());
		btn.setTextSize(22);
		btn.setHoverTextSize(26);
		
		btn.addOnButtonClickedListener(new OnButtonClickedListener() {
			
			@Override
			public void onButtonClicked(ButtonEvent event) {				
				hide();
			}
		});
		
		btn.addOnHoverListener(new OnHoverListener() {
			
			@Override
			public void onHover(HoverEvent event) {
				// TODO
			}
		});
		
		addWidget(btn);
		
		return btn;
	}
	
	/**
	 * @return the leaveGame
	 */
	public Button getLeaveGameBtn() {
		return leaveGame;
	}
	
	/**
	 * @return the title
	 */
	public Label getTitle() {
		return title;
	}

	/**
	 * @return the weaponClasses
	 */
	public Button getWeaponClasses() {
		return weaponClasses;
	}
	
	/**
	 * @return the options
	 */
	public Button getOptions() {
		return options;
	}
	
	/**
	 * @return the switchTeam
	 */
	public Button getSwitchTeam() {
		return switchTeam;
	}
}
