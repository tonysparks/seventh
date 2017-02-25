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
public class SwitchTeamDialogView implements Renderable {

    private SwitchTeamDialog dialog;
    private PanelView panelView;
    /**
     * 
     */
    public SwitchTeamDialogView(SwitchTeamDialog dialog) {
        this.dialog = dialog;
        
        this.panelView = new PanelView();
        this.panelView.addElement(new LabelView(dialog.getTitle()));
        this.panelView.addElement(new ButtonView(dialog.getCancelBtn()));
        this.panelView.addElement(new ButtonView(dialog.getAllied()));
        this.panelView.addElement(new ButtonView(dialog.getAxis()));
        this.panelView.addElement(new ButtonView(dialog.getSpectator()));                 
    }
            
    
    public void clear() {
        this.panelView.clear();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        this.panelView.update(timeStep);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        if(dialog.isVisible()) {
            Rectangle bounds = dialog.getBounds();
            canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xaf383e18);
            canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, 0xff000000);
            
            this.panelView.render(canvas, camera, alpha);
        }
    }

}
