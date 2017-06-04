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
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.Renderable;
import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.ui.Button;
import seventh.ui.TextBox;
import seventh.ui.Widget;

/**
 * Renders a {@link Button}
 * 
 * @author Tony
 *
 */
public class TextBoxView implements Renderable {

    /**
     * The text box
     */
    private TextBox textBox;
    
    /**
     * Renders a label
     */
    private LabelView labelView, textView;
    
    private Timer blinkTimer;
    private boolean showCursor;
    
    
    /**
     * @param box
     */
    public TextBoxView(TextBox box) {
        this.textBox = box;
        this.labelView = new LabelView(box.getLabel());    
        this.textView = new LabelView(box.getTextLabel());
        
        this.blinkTimer = new Timer(true, 500);
        this.showCursor = true;    
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#render(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
     */
    public void render(Canvas renderer, Camera camera, float alpha) {
        if ( this.textBox.isVisible() ) {            
            
            Rectangle bounds = textBox.getBounds();
            String labelText = textBox.getLabelText();
            
            renderer.setFont(textBox.getTextLabel().getFont(), (int)textBox.getTextLabel().getTextSize());
            
            int originalX = bounds.x;
            int originalY = bounds.y;
            
            
            renderGradiantBackground(textBox, renderer, camera, alpha);
            
            if(textBox.hasFocus() || textBox.isHovering()) {
                renderer.drawRect(bounds.x-1, bounds.y-1, bounds.width+2, bounds.height+2, //textBox.getForegroundColor());
                        0x5ff1f401);
//                        0xff393939);
            }
                        
            Rectangle lbounds = this.textBox.getTextLabel().getBounds();//update the text
            lbounds.set(bounds);
            lbounds.x += 5;        
            lbounds.y += 3;
            this.textView.render(renderer, camera, alpha);
            
            
            if(showCursor && textBox.hasFocus()) 
            {
                String text = textBox.getText();
                int textWidth = renderer.getWidth(text.substring(0, textBox.getCursorIndex())) + 5;
                renderer.setFont(textBox.getTextLabel().getFont(), (int)textBox.getTextLabel().getTextSize());
                RenderFont.drawShadedString(renderer, "_", lbounds.x + textWidth, lbounds.y + renderer.getHeight("W"), textBox.getForegroundColor());
            }
            
            bounds.x = originalX;
            
            int width = renderer.getWidth(labelText);
            
            lbounds = this.textBox.getLabel().getBounds();
            lbounds.set(bounds);
            lbounds.x = bounds.x - (width + 20);
            lbounds.y = originalY + 5;
            this.labelView.render(renderer, camera, alpha);
            
        }
    }

    private void renderGradiantBackground(Widget w, Canvas renderer, Camera camera, float alpha) {
//        int gradiant = w.getGradiantColor();
//        int bg = w.getBackgroundColor();
        
        Rectangle bounds = w.getScreenBounds();
        
//        int a = 0;//w.getBackgroundAlpha();        
        for(int i = 0; i < bounds.height; i++ ) {
//            Vector3f.Vector3fSubtract(bg, gradiant, this.scratch1);
//            int scratch = Colors.subtract(bg, gradiant);
            
//            this.scratch1.x *= (1.0f / (bounds.height/1.5f));
//            this.scratch1.y *= (1.0f / (bounds.height/1.5f));
//            
//            Vector3f.Vector3fAdd(scratch2, scratch1, scratch2);
//            renderer.setColor(scratch2, a);
            
            //int col = (a << 24) | scratch;
            int col = 0xff383e18;
            renderer.drawLine(bounds.x, bounds.y + i, bounds.x + bounds.width, bounds.y + i, col);
        }
        
//        renderer.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y, 0xff000000);
//        renderer.drawLine(bounds.x + bounds.width, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0xff000000);
        renderer.drawRect(bounds.x,  bounds.y,  bounds.width,  bounds.height, 0xff000000);
    }
    
    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#update(org.myriad.core.TimeStep)
     */
    public void update(TimeStep timeStep) {        
        this.blinkTimer.update(timeStep);
        if(this.blinkTimer.isTime()) {
            this.showCursor = !this.showCursor;
        }
    }

    /**
     * @return the textBox
     */
    public TextBox getTextBox() {
        return textBox;
    }
    
}
