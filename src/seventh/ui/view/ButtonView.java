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

import com.badlogic.gdx.graphics.Color;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Colors;
import seventh.client.gfx.Renderable;
import seventh.client.gfx.Theme;
import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.Button;
import seventh.ui.Label;
import seventh.ui.Widget;

/**
 * Renders a {@link Button}
 * 
 * @author Tony
 *
 */
public class ButtonView implements Renderable {

    /**
     * The Button
     */
    private Button button;
    
    /**
     * Renders a label
     */
    private LabelView labelView;
    
    private Color srcColor, dstColor, currentColor;
    private float time;
    
    /**
     * @param button
     */
    public ButtonView(Button button) {
        this.button = button;

        this.srcColor = new Color();
        this.dstColor = new Color();
        this.currentColor = new Color();
                
        this.labelView = new LabelView(this.button.getTextLabel()) {            
            @Override
            protected void setColor(Canvas renderer, Label label) {
                renderer.setColor(Color.argb8888(currentColor), (int) (currentColor.a * 255) );
                //super.setColor(renderer, label);
            }
        };
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#render(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
     */
    public void render(Canvas renderer, Camera camera, float alpha) {
        if ( this.button.isVisible() ) {
            if ( this.button.gradiantEnabled() ) {
                renderGradiantBackground(this.button, renderer, camera, alpha);
            }
            
            this.labelView.render(renderer, camera, alpha);
        }
    }

    private void renderGradiantBackground(Widget w, Canvas renderer, Camera camera, float alpha) {
        int gradiant = w.getGradiantColor();
        int bg = w.getBackgroundColor();
        
        Rectangle bounds = w.getScreenBounds();
        
        int a = w.getBackgroundAlpha();        
        for(int i = 0; i < bounds.height; i++ ) {
//            Vector3f.Vector3fSubtract(bg, gradiant, this.scratch1);
            int scratch = Colors.subtract(bg, gradiant);
            
//            this.scratch1.x *= (1.0f / (bounds.height/1.5f));
//            this.scratch1.y *= (1.0f / (bounds.height/1.5f));
//            
//            Vector3f.Vector3fAdd(scratch2, scratch1, scratch2);
//            renderer.setColor(scratch2, a);
            
            int col = (a << 24) | scratch;
            renderer.drawLine(bounds.x, bounds.y + i, bounds.x + bounds.width, bounds.y + i, col);
        }
    }
    
    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#update(org.myriad.core.TimeStep)
     */
    public void update(TimeStep timeStep) {
        if(button.isHovering()) {
            Theme theme = button.getTheme();                
            Color.argb8888ToColor(this.srcColor, button.getForegroundColor());
            Color.argb8888ToColor(this.dstColor, (theme!=null) ? theme.getHoverColor() : button.getForegroundColor());
            
            time += timeStep.asFraction() * 0.79;                        
            float t = (float)Math.cos(time);
            this.currentColor.set(this.srcColor.lerp(dstColor, t));
        }
        else {
            time = 0;
            Color.argb8888ToColor(currentColor, this.button.getForegroundColor());
            currentColor.a = this.button.getForegroundAlpha() / 255;
        }
    }

    /**
     * @return the button
     */
    public Button getButton() {
        return button;
    }
    
}
