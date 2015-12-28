/*
 * see license.txt 
 */
package seventh.client.gfx;


import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.view.ButtonView;
import seventh.ui.view.LabelView;
import seventh.ui.view.PanelView;

/**
 * @author Tony
 *
 */
public class InGameOptionsDialogView implements Renderable {

	private InGameOptionsDialog dialog;
	private PanelView<Renderable> panelView;
	private WeaponClassDialogView weaponClassDialogView;
	private SwitchTeamDialogView switchTeamDialogView;
	/**
	 * 
	 */
	public InGameOptionsDialogView(InGameOptionsDialog dialog) {
		this.dialog = dialog;
		
		this.panelView = new PanelView<Renderable>();
		this.panelView.addElement(new LabelView(dialog.getTitle()));
		this.panelView.addElement(new ButtonView(dialog.getWeaponClasses()));
		this.panelView.addElement(new ButtonView(dialog.getSwitchTeam()));
		this.panelView.addElement(new ButtonView(dialog.getOptions()));		
		this.panelView.addElement(new ButtonView(dialog.getLeaveGameBtn()));
		
		this.weaponClassDialogView= new WeaponClassDialogView(dialog.getWeaponDialog());
		this.switchTeamDialogView = new SwitchTeamDialogView(dialog.getSwitchTeamDialog());
	}
			
	
	public void clear() {
		this.panelView.clear();
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		
		if(dialog.getWeaponDialog().isVisible()) {
			weaponClassDialogView.render(canvas, camera, alpha);
		}
		else if(dialog.getSwitchTeamDialog().isVisible()) {
			switchTeamDialogView.render(canvas, camera, alpha);
		}
		else if(dialog.isVisible()) {						
			Rectangle bounds = dialog.getBounds();
			canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xaf383e18);
			canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
			
			this.panelView.render(canvas, camera, alpha);
		}
	}

}
