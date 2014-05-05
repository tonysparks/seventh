/*
 * see license.txt 
 */
package seventh.client.gfx;


import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.view.ButtonView;
import seventh.ui.view.ImageButtonView;
import seventh.ui.view.LabelView;
import seventh.ui.view.PanelView;

/**
 * @author Tony
 *
 */
public class WeaponClassDialogView implements Renderable {

	private WeaponClassDialog dialog;
	private PanelView<Renderable> panelView;
	/**
	 * 
	 */
	public WeaponClassDialogView(WeaponClassDialog dialog) {
		this.dialog = dialog;
		
		this.panelView = new PanelView<Renderable>();
		this.panelView.addElement(new LabelView(dialog.getTitle()));
		this.panelView.addElement(new ButtonView(dialog.getCancelBtn()));
		
		Button[] btns = dialog.getWeaponClasses();
		if(btns.length != 6) {
			throw new IllegalArgumentException("Don't have all the weapons defined!");
		}
		
		switch(dialog.getTeam()) {
			case ALLIES:
				this.panelView.addElement(new ImageButtonView(btns[0], Art.thompsonIcon));				
				this.panelView.addElement(new ImageButtonView(btns[1], Art.m1GarandIcon));
				this.panelView.addElement(new ImageButtonView(btns[2], Art.springfieldIcon));			
				break;
			case AXIS:
				this.panelView.addElement(new ImageButtonView(btns[0], Art.mp40Icon));
				this.panelView.addElement(new ImageButtonView(btns[1], Art.mp44Icon));
				this.panelView.addElement(new ImageButtonView(btns[2], Art.kar98Icon));
				break;		
			default:
				break;			
		}
		
		this.panelView.addElement(new ImageButtonView(btns[3], Art.riskerIcon));
		this.panelView.addElement(new ImageButtonView(btns[4], Art.shotgunIcon));
		this.panelView.addElement(new ImageButtonView(btns[5], Art.rocketIcon)); 
		
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
	public void render(Canvas canvas, Camera camera, long alpha) {
		if(dialog.isVisible()) {
			Rectangle bounds = dialog.getBounds();
			canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xaf383e18);
			canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
			
			this.panelView.render(canvas, camera, alpha);
		}
	}

}
