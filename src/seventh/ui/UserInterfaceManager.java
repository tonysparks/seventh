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
package seventh.ui;


import java.util.List;

import leola.frontend.listener.EventDispatcher;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Cursor;
import seventh.client.gfx.ReticleCursor;
import seventh.client.inputs.Inputs;
import seventh.shared.TimeStep;

/**
 * Use the {@link UserInterfaceManager} to add any {@link Widget}s and register them to the 
 * designated {@link EventDispatcher} and delegates user input to each component.
 * 
 * @author Tony
 *
 */
public class UserInterfaceManager extends Inputs {
    
    
    /**
     * Widgets
     */
    private List<Widget> widgets;
    private Cursor cursor;
    
    public UserInterfaceManager(UserInterfaceManager parent) {
        this.cursor = parent.getCursor();
        this.widgets = parent.widgets;
    }
    
    /**
     * @param eventDispatcher
     */
    public UserInterfaceManager() {
        this.cursor = new ReticleCursor();//new ImageCursor();
        
        // pretty big hack, should be passed in a cleaner
        // manner
        this.widgets = Widget.globalInputListener.getGlobalWidgets();
        Widget.globalInputListener.cursor = this.cursor;
    }
    
    public void hideMouse() {
        this.cursor.setVisible(false);
    }
    
    public void showMouse() {
        this.cursor.setVisible(true);
    }
    
    /**
     * @return the cursor
     */
    public Cursor getCursor() {
        return cursor;
    }
    
    /**
     * @param cursor the cursor to set
     */
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
    
    
    
    /**
     * Destroys the widgets
     */
    public void destroy() {
        Widget.globalInputListener.destroy();
    }
    
    /**
     * Checks to see if the {@link Cursor} is hovering over
     * any {@link Widget}s
     */
    public void checkIfCursorIsHovering() {
        boolean isHovering = false;
        int size = this.widgets.size();
        for(int i = 0; i < size; i++) {
            Widget w = this.widgets.get(i);
            if(w instanceof Hoverable && !w.isDisabled()) {
                Hoverable h = (Hoverable)w;
                if(h.isHovering()) {
                    isHovering = true;
                    break;
                }
            }
        }
        
        getCursor().setColor(isHovering ? 0xafff0000 : 0xafffff00);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Inputs#keyDown(int)
     */
    @Override
    public boolean keyDown(int key) {
        return Widget.globalInputListener.keyDown(key);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Inputs#keyUp(int)
     */
    @Override
    public boolean keyUp(int key) {
        return Widget.globalInputListener.keyUp(key);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Inputs#mouseMoved(int, int)
     */
    @Override
    public boolean mouseMoved(int x, int y) {
        cursor.moveTo(x,y);
        return Widget.globalInputListener.mouseMoved(cursor.getX(), cursor.getY());
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Inputs#touchDown(int, int, int, int)
     */
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        return Widget.globalInputListener.touchDown(cursor.getX(), cursor.getY(), pointer, button);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Inputs#touchUp(int, int, int, int)
     */
    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        return Widget.globalInputListener.touchUp(cursor.getX(), cursor.getY(), pointer, button);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Inputs#keyTyped(char)
     */
    @Override
    public boolean keyTyped(char key) {
        return Widget.globalInputListener.keyTyped(key);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Inputs#scrolled(int)
     */
    @Override
    public boolean scrolled(int amount) {
        return Widget.globalInputListener.scrolled(amount);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.Inputs#touchDragged(int, int, int)
     */
    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        cursor.moveTo(x,y);
        return Widget.globalInputListener.touchDragged(cursor.getX(), cursor.getY(), pointer);
    }
    
    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#render(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
     */
    public void render(Canvas canvas) {
        this.cursor.render(canvas);
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Renderable#update(org.myriad.core.TimeStep)
     */
    public void update(TimeStep timeStep) {        
        this.cursor.update(timeStep);
    }
}
