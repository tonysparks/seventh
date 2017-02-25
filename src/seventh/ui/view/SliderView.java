/*
 * see license.txt 
 */
package seventh.ui.view;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Colors;
import seventh.client.gfx.Renderable;
import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Slider;

/**
 * @author Tony
 *
 */
public class SliderView implements Renderable {

    private ButtonView handleView;
    private Slider slider;
    
    /**
     * 
     */
    public SliderView(Slider slider) {
        this.slider = slider;
        this.handleView = new ButtonView(slider.getHandle());
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.handleView.update(timeStep);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        Rectangle bounds = slider.getBounds();        
        canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff383e18);
        canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
//        if(slider.isHovering()) {
//            canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, Colors.setAlpha(slider.getForegroundColor(), 100));    
//        }
        
        this.handleView.render(canvas, camera, alpha);
        Button handle = slider.getHandle();
        bounds = handle.getScreenBounds();
        canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff383e18);
        
        int a = 100;
        if(handle.isHovering()) {
            a = 240;
        }
        
        canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, Colors.setAlpha(slider.getForegroundColor(), a));
        canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
    }

}
