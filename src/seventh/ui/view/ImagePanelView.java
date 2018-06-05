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

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Rectangle;
import seventh.ui.ImagePanel;

/**
 * Renders a group of elements
 * 
 * @author Tony
 *
 */
public class ImagePanelView extends PanelView {

    private ImagePanel panel;
    
    /**
     * 
     */
    public ImagePanelView(ImagePanel panel) {
        this.panel = panel;
    }
    
    /* (non-Javadoc)
     * @see seventh.ui.view.PanelView#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
     */
    @Override
    public void render(Canvas renderer, Camera camera, float alpha) {
        
        Rectangle bounds = panel.getScreenBounds();
        renderer.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, panel.getBackgroundColor());
        renderer.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, panel.getForegroundColor());
        
        TextureRegion tex = panel.getImage();
        if(tex != null) {
            renderer.drawScaledImage(tex, bounds.x, bounds.y, bounds.width, bounds.height, null);
        }
        super.render(renderer, camera, alpha);
    }
}
