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

import java.util.ArrayList;
import java.util.List;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.math.Rectangle;
import seventh.shared.TimeStep;
import seventh.ui.Widget;

/**
 * Renders a group of elements
 * 
 * @author Tony
 *
 */
public class PanelView implements Renderable {

    /**
     * Elements
     */
    private List<Renderable> uiElements;
    private Widget panel;
    
    public PanelView() {
        this(null);
    }
    
    /**
     * @param panel
     */
    public PanelView(Widget panel) {
        this.panel = panel;
        this.uiElements = new ArrayList<>();
    }
    
    public PanelView clear() {
        this.uiElements.clear();
        return this;
    }
    
    /**
     * Adds an element
     * @param element
     */
    public PanelView addElement(Renderable element) {
        this.uiElements.add(element);
        return this;
    }
    
    /**
     * @return the uiElements
     */
    public List<Renderable> getUiElements() {
        return uiElements;
    }
    
    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#render(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
     */
    @Override
    public void render(Canvas renderer, Camera camera, float alpha) {
        if(this.panel != null && this.panel.getBorderWidth() > 0) {
            
            
            Rectangle bounds = this.panel.getBounds();
            int x = bounds.x;
            int y = bounds.y;
            int w = bounds.width;
            int h = bounds.height;
            
            int color = this.panel.getBorderColor();
            
            renderer.fillRect(x, y, w, h, this.panel.getBackgroundColor());
            
            for(int i = 0; i < this.panel.getBorderWidth(); i++) {
                renderer.drawRect(x+i, y+i, w-i*2, h-i*2, color);
            }
        }
        
        int size = this.uiElements.size();
        for(int i = 0; i < size; i++) {
            this.uiElements.get(i).render(renderer, camera, alpha);
        }
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#update(org.myriad.core.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        int size = this.uiElements.size();
        for(int i = 0; i < size; i++) {
            this.uiElements.get(i).update(timeStep);
        }
    }

}
