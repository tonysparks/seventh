/*
 * see license.txt 
 */
package seventh.ui.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Skin;

/**
 * @author Tony
 *
 */
public class IconButtonView implements Renderable {

    private Button btn;
    private TextureRegion background;
    private TextureRegion clicked;
    private TextureRegion icon;
    
    
    public IconButtonView(Button btn, Skin skin, TextureRegion icon) {
        this(btn, skin.buttonBackground, skin.buttonOnClick, icon);
    }
    
    /**
     * 
     */
    public IconButtonView(Button btn, TextureRegion background, TextureRegion clicked, TextureRegion icon) {
        this.btn = btn;
        this.background = background;
        this.clicked = clicked;
        this.icon = icon;
    }
        
    @Override
    public void update(TimeStep timeStep) {
    }
    
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        Rectangle bounds = this.btn.getBounds();
        canvas.drawScaledImage(this.background, this.btn.getScreenPosition().x, this.btn.getScreenPosition().y,
                bounds.width, bounds.height, null);
        
        if(this.icon != null) {
            int xScale = (int)((float)bounds.width * 0.6f);
            int yScale = (int)((float)bounds.width * 0.6f);
            
            int xOffset = (bounds.width - xScale) / 2;
            int yOffset = (bounds.height - yScale) / 2;
            canvas.drawScaledImage(this.icon, this.btn.getScreenPosition().x + xOffset, this.btn.getScreenPosition().y + yOffset, 
                    xScale, yScale, null);
        }
        
        if(this.btn.isPressed()) {            
            canvas.drawScaledImage(this.clicked, this.btn.getScreenPosition().x, this.btn.getScreenPosition().y, 
                    bounds.width, bounds.height, 0xffffffff);            
        }
        else if(this.btn.isHovering()) {
            canvas.drawScaledImage(this.clicked, this.btn.getScreenPosition().x, this.btn.getScreenPosition().y, 
                    bounds.width, bounds.height, 0x5fffffff);            
        }
        
    }
}
