/*
**************************************************************************************
*Myriad Engine                                                                       *
*Copyright (C) 2006-2007, 5d Studios (www.5d-Studios.com)                            *
*                                                                                    *
*This library is free software; you can redistribute it and/or                       *
*modify it under the terms of the GNU Lesser General Public                          *
*License as published by the Free Software Foundation; either                        *
*version 2.1 of the License, or (at your option) any later version.                  *
*                                                                                    *
*This library is distributed in the hope that it will be useful,                     *
*but WITHOUT ANY WARRANTY; without even the implied warranty of                      *
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU                   *
*Lesser General Public License for more details.                                     *
*                                                                                    *
*You should have received a copy of the GNU Lesser General Public                    *
*License along with this library; if not, write to the Free Software                 *
*Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA      *
**************************************************************************************
*/
package seventh.ui.view;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.ui.Button;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Image for a button, with optional Text overlay.
 * 
 * @author Tony
 *
 */
public class ImageButtonView extends ButtonView {

    /**
     * Button Image
     */
    private TextureRegion buttonImage;
    
    /**
     * Button down image
     */
    private TextureRegion buttonDownImage;
    
    /**
     * Button up image
     */
    private TextureRegion buttonUpImage;
    
    
    /**
     * @param button
     * @param buttonImage
     */
    public ImageButtonView(Button button, TextureRegion buttonImage) {
        this(button, buttonImage, buttonImage, buttonImage);
    }
    


    /**
     * @param button
     * @param buttonImage
     * @param buttonDownImg
     */
    public ImageButtonView(Button button, TextureRegion buttonImage, TextureRegion buttonUpImg, TextureRegion buttonDownImg) {
        super(button);
        
        this.buttonImage = buttonImage;
        this.buttonUpImage = buttonUpImg;
        this.buttonDownImage = buttonDownImg;
        
        button.setEnableGradiant(false);
        button.setBackgroundAlpha(0);        
    }
    
    /**
     * @return the buttonImage
     */
    public TextureRegion getButtonImage() {
        return buttonImage;
    }



    /**
     * @param buttonImage the buttonImage to set
     */
    public void setButtonImage(TextureRegion buttonImage) {
        this.buttonImage = buttonImage;
    }



    /**
     * @return the buttonDownImage
     */
    public TextureRegion getButtonDownImage() {
        return buttonDownImage;
    }



    /**
     * @param buttonDownImage the buttonDownImage to set
     */
    public void setButtonDownImage(TextureRegion buttonDownImage) {
        this.buttonDownImage = buttonDownImage;
    }



    /**
     * @return the buttonUpImage
     */
    public TextureRegion getButtonUpImage() {
        return buttonUpImage;
    }



    /**
     * @param buttonUpImage the buttonUpImage to set
     */
    public void setButtonUpImage(TextureRegion buttonUpImage) {
        this.buttonUpImage = buttonUpImage;
    }


    
    /* (non-Javadoc)
     * @see com.fived.ricochet.ui.view.ButtonView#render(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
     */
    @Override
    public void render(Canvas renderer, Camera camera, float alpha) {                
        Button button = getButton();
        if ( button.isVisible() ) {
            Vector2f position = button.getScreenPosition();

            boolean makeBig = false;
            Rectangle bounds = button.getBounds();
            if(button.hasBorder()) {
                if(button.isHovering()) {
                    renderer.drawRect((int)position.x, (int)position.y, bounds.width, bounds.height, 0xffffffff);
                }
                else {
                    renderer.drawRect((int)position.x, (int)position.y, bounds.width, bounds.height, 0xff000000);
                }
            }
            else {
                makeBig = button.isHovering();
            }

            
            int color = button.getForegroundColor();
            
            if ( this.buttonImage != null ) {
                if(this.buttonImage instanceof Sprite) {
                    Sprite sprite = (Sprite)this.buttonImage;
                    sprite.setPosition(position.x, position.y);
                    renderer.drawRawSprite( sprite );
                }
                else {
                
                    // center the button icon
                    int uw = this.buttonUpImage.getRegionWidth();
                    int uh = this.buttonUpImage.getRegionHeight();
                    
                    int w = uw / 2 - this.buttonImage.getRegionWidth() / 2;
                    int h = uh / 2 - this.buttonImage.getRegionHeight() / 2  + 5;
                    
                    if ( button.isPressed() ) {
                        if ( this.buttonDownImage != null) {        
                            renderer.drawScaledImage(this.buttonDownImage, (int)position.x, (int)position.y, uw, uh, color);
                        }            
                        renderer.drawScaledImage(this.buttonImage, (int)position.x + w, (int)position.y + h + 5, 
                                this.buttonImage.getRegionWidth(), this.buttonImage.getRegionHeight(), color);
                    }
                    else {
                        //renderer.drawImage(this.buttonUpImage, (int)position.x, (int)position.y, color);
                        renderer.drawScaledImage(this.buttonImage, (int)position.x + w, (int)position.y + h, 
                                this.buttonImage.getRegionWidth(), this.buttonImage.getRegionHeight(), color);
                    }
                }
            }
            else {
                Rectangle r = button.getBounds();            
                if ( button.isPressed() ) { 
                    if ( this.buttonDownImage != null) {                        
                        renderer.drawScaledImage(this.buttonDownImage, (int)position.x, (int)position.y, r.width, r.height, color);
                    }
                    else {
                        renderer.drawScaledImage(this.buttonUpImage, (int)position.x, (int)position.y + 5, r.width, r.height, color);    
                    }
                }
                else {
                    if(makeBig) {
                        renderer.drawScaledImage(this.buttonUpImage, (int)position.x, (int)position.y, r.width + 5, r.height + 5, color);
                    }
                    else {
                        renderer.drawScaledImage(this.buttonUpImage, (int)position.x, (int)position.y, r.width, r.height, color);
                    }
                }
            }
            
            
            super.render(renderer, camera, alpha);
        }
    }

}
