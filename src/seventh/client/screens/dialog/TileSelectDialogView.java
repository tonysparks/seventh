/*
 * see license.txt 
 */
package seventh.client.screens.dialog;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.shared.TimeStep;
import seventh.ui.view.PanelView;

/**
 * @author Tony
 *
 */
public class TileSelectDialogView implements Renderable {

    private TileSelectDialog dialog;
    private PanelView panelView;
    /**
     * 
     */
    public TileSelectDialogView(TileSelectDialog dialog) {
        this.dialog = dialog;
        
        this.panelView = new PanelView(dialog);
    }

    @Override
    public void update(TimeStep timeStep) {
    }

    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        this.panelView.render(canvas, camera, alpha);
        this.dialog.getPanelView().render(canvas, camera, alpha);        
    }

}
