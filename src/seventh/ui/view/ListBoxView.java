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
import seventh.ui.Button;
import seventh.ui.ListBox;
import seventh.ui.events.ListHeaderChangedEvent;
import seventh.ui.events.ListItemChangedEvent;
import seventh.ui.events.OnListHeaderChangedListener;
import seventh.ui.events.OnListItemChangedListener;

/**
 * Renders the {@link ListBox}
 * 
 * @author Tony
 *
 */
public class ListBoxView<T extends Renderable> implements Renderable {

    /**
     * Elements
     */
    private ListBox box;
    private List<ButtonView> buttonViews;
    private List<ButtonView> hderButtonViews;
    
    /**
     * 
     */
    public ListBoxView(ListBox box) {
        this.buttonViews = new ArrayList<ButtonView>();
        this.hderButtonViews = new ArrayList<>();
        
        this.box = box;
        this.box.addListItemChangedListener(new OnListItemChangedListener() {
            
            @Override
            public void onListItemChanged(ListItemChangedEvent event) {
                Button button = event.getButton();
                if(event.isAdded()) {
                    buttonViews.add(new ButtonView(button));
                }
                else {
                    int removeIndex = -1;
                    int i = 0;
                    for(ButtonView view : buttonViews) {
                        if(view.getButton() == button) {
                            removeIndex = i;
                            break;
                        }
                        i++;
                    }
                    
                    if(removeIndex > -1) {
                        buttonViews.remove(removeIndex);
                    }
                }
            }
        });

        this.box.addListHeaderChangedListener(new OnListHeaderChangedListener() {
            
            @Override
            public void onListHeaderChanged(ListHeaderChangedEvent event) {
                Button button = event.getButton();
                if(event.isAdded()) {
                    hderButtonViews.add(new ButtonView(button));    
                }
                else {
                    hderButtonViews.remove(button);    
                }
            }
        });
        
        for(Button btn : box.getItems()) {
            buttonViews.add(new ButtonView(btn));
        }
        
        for(Button btn : box.getColumnHeaders()) {
            hderButtonViews.add(new ButtonView(btn));
        }
        
    }
    
    /**
     * @return the box
     */
    public ListBox getBox() {
        return box;
    }
    
    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#render(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
     */
    @Override
    public void render(Canvas renderer, Camera camera, float alpha) {
        
        Rectangle bounds = box.getBounds();
        int headerHeight = box.getHeaderHeight();       
        int headerMargin = box.getHeaderMargin();
        
        renderer.fillRect(bounds.x - 1, bounds.y, bounds.width, bounds.height + 1, box.getBackgroundColor());
        renderer.drawRect(bounds.x - 1, bounds.y, bounds.width + 1, bounds.height + 1, 0xff000000);
        
        bounds = box.getScreenBounds();
        int y = headerHeight + headerMargin;
        
        renderer.fillRect(bounds.x - 1, bounds.y, bounds.width + 1, headerHeight + 1, 0xff282c0c);
        renderer.drawRect(bounds.x - 1, bounds.y, bounds.width + 1, headerHeight + 1, 0xff000000);
        
        int hsize = hderButtonViews.size();
        for(int i = 0; i < hsize; i++) {
            ButtonView view = hderButtonViews.get(i);
            view.render(renderer, camera, alpha);
        }
        
        int size = buttonViews.size();
        for(int i = box.getIndex(); i < size; i++) {
            ButtonView view = this.buttonViews.get(i);
            Button btn = view.getButton();
            btn.getBounds().y = y;
            
            Rectangle rect = btn.getScreenBounds();                        
            if(bounds.contains(rect)) {
                btn.setDisabled(false);
                if(btn.isHovering()) {
                    renderer.fillRect(rect.x - 10, rect.y - 5, bounds.width - 10, rect.height, 0x0fffffff);
                }
                view.render(renderer, camera, alpha);               
            }
            else {
                btn.setDisabled(true);
            }
            
            y += rect.height + box.getMargin();
        }
        
        
        //Rectangle rect = box.getBounds();
        //renderer.fillRect(rect.x, rect.y + box.getHeaderHeight(), rect.width, rect.height - box.getHeaderHeight(), 0x3fff0000);
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#update(org.myriad.core.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {        
        int size = buttonViews.size();
        for(int i = 0; i < size; i++) {
            this.buttonViews.get(i).update(timeStep);
        }
        
        size = hderButtonViews.size();
        for(int i = 0; i < size; i++) {
            this.hderButtonViews.get(i).update(timeStep);
        }
    }

}
