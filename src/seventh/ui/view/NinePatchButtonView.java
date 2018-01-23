/*
 * see license.txt 
 */
package seventh.ui.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Skin;

/**
 * @author Tony
 *
 */
public class NinePatchButtonView implements Renderable {

    private Button btn;
    private NinePatch backgroundPatch;
    private NinePatch clickedPatch;
    
    private LabelView label;
    
    public NinePatchButtonView(Button btn, Skin skin) {
        this(btn, skin.buttonBackground, skin.buttonOnClick);
    }
    
    /**
     * 
     */
    public NinePatchButtonView(Button btn, TextureRegion background, TextureRegion clicked) {
        this.btn = btn;
        
        int left = 52;
        int right = 52;
        int top = 52;
        int bottom = 52;
        //
        this.backgroundPatch = new NinePatch(background, left, right, top, bottom);
        this.clickedPatch = new NinePatch(clicked,38,38,38,38);
        
        this.label = new LabelView(this.btn.getTextLabel());
        this.btn.getTextLabel().getBounds().y += 10;
    }
    
    public NinePatchButtonView(Button btn, 
            NinePatch background, 
            NinePatch clicked) {
        this.btn = btn;
        
        this.backgroundPatch = background;
        this.clickedPatch = clicked;
        
    }
    
    @Override
    public void update(TimeStep timeStep) {
    }
    
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        //this.backgroundPatch.setColor(new Color(0xffff00ff)); //0xf1f401ff
        this.backgroundPatch.draw(canvas.getSpriteBatch(), this.btn.getScreenPosition().x, this.btn.getScreenPosition().y, 
                this.btn.getBounds().width, this.btn.getBounds().height);
        
//        if(this.icon != null) {
//            int xOffset = (this.btn.getBounds().width - this.icon.getRegionWidth()) / 2;
//            int yOffset = (this.btn.getBounds().height - this.icon.getRegionHeight()) / 2;
//            canvas.drawImage(this.icon, this.btn.getScreenPosition().x + xOffset, this.btn.getScreenPosition().y + yOffset, null);
//        }
        
        this.label.render(canvas, camera, alpha);
        
        if(this.btn.isPressed()) {            
            this.clickedPatch.setColor(Color.WHITE);
            this.clickedPatch.draw(canvas.getSpriteBatch(), this.btn.getScreenPosition().x+12, this.btn.getScreenPosition().y+24, 
                this.btn.getBounds().width-34, this.btn.getBounds().height-34);
        }
        else if(this.btn.isHovering()) {
            this.clickedPatch.setColor(new Color(0xffffff5f));
            this.clickedPatch.draw(canvas.getSpriteBatch(), this.btn.getScreenPosition().x+12, this.btn.getScreenPosition().y+24, 
                    this.btn.getBounds().width-34, this.btn.getBounds().height-34);
        }        
    }
}
