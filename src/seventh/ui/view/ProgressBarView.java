/**
 * 
 */
package seventh.ui.view;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.ProgressBar;

/**
 * The View of a {@link ProgressBar}
 * 
 * @author Tony
 *
 */
public class ProgressBarView implements Renderable {

    private ProgressBar progressBar;
    
    /**
     * 
     */
    public ProgressBarView(ProgressBar progressBar) {
        this.progressBar = progressBar;
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
        if(progressBar.isVisible()) {
            Rectangle bounds = progressBar.getBounds();
            canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, progressBar.getBackgroundColor());
                    
            int percentOfWidth = (int)(progressBar.getPercentCompleted() * bounds.width);            
            canvas.fillRect(bounds.x, bounds.y, percentOfWidth, bounds.height, progressBar.getForegroundColor());
            canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
            
            addAShadowEffect(canvas, bounds);    
        }
    }
    
    private void addAShadowEffect(Canvas canvas, Rectangle bounds) {
        int x = bounds.x;
        int y = bounds.y;
                    
        canvas.drawLine( x, y+1, x+bounds.width, y+1, 0x8f000000 );
        canvas.drawLine( x, y+2, x+bounds.width, y+2, 0x5f000000 );
        canvas.drawLine( x, y+3, x+bounds.width, y+3, 0x2f000000 );
        canvas.drawLine( x, y+4, x+bounds.width, y+4, 0x0f000000 );
        canvas.drawLine( x, y+5, x+bounds.width, y+5, 0x0b000000 );
        canvas.drawLine( x, y+6, x+bounds.width, y+6, 0x0a000000 );
        
        y = y+15;
        canvas.drawLine( x, y-6, x+bounds.width, y-6, 0x0a000000 );
        canvas.drawLine( x, y-5, x+bounds.width, y-5, 0x0b000000 );
        canvas.drawLine( x, y-4, x+bounds.width, y-4, 0x0f000000 );
        canvas.drawLine( x, y-3, x+bounds.width, y-3, 0x2f000000 );
        canvas.drawLine( x, y-2, x+bounds.width, y-2, 0x5f000000 );
        canvas.drawLine( x, y-1, x+bounds.width, y-1, 0x8f000000 );
    }
}
