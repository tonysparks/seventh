/*
 * see license.txt 
 */
package seventh.ui.view;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.Checkbox;
import seventh.ui.Label;

/**
 * @author Tony
 *
 */
public class CheckboxView implements Renderable {

	private Checkbox checkbox;
	private LabelView labelView;	
	
	/**
	 * 
	 */
	public CheckboxView(Checkbox checkbox) {
		this.checkbox = checkbox;
		this.labelView = new LabelView(checkbox.getLabel());
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
		
		Rectangle bounds = checkbox.getBounds();
		Label label = checkbox.getLabel();
		canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff383e18);
		
		canvas.setFont(label.getFont(), (int)label.getTextSize());
		
		if(checkbox.isChecked()) {
			canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
			canvas.drawString("x", bounds.x + 5, bounds.y + canvas.getHeight("W") - 5, 0xffffffff);
		}
		else {
			canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
		}
		
		if(checkbox.isHovering()) {
			canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, checkbox.getForegroundColor());
		}
						
		labelView.render(canvas, camera, alpha);
	}

}
