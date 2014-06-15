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

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import leola.frontend.listener.EventDispatcher;
import seventh.client.Inputs;
import seventh.client.gfx.Theme;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * The bases for all widgets.
 * 
 * @author Tony
 *
 */
public class Widget {

	/**
	 * The global input listener
	 */
	static WidgetInputListener globalInputListener = new WidgetInputListener();
	
	/**
	 * Id Generator
	 */
	private static AtomicInteger idGenerator = new AtomicInteger();
	
	/**
	 * Contains the widget bounds
	 */
	private Rectangle bounds;
	
	/**
	 * Color
	 */
	private int backgroundColor;
	
	/**
	 * Foreground color
	 */
	private int foregroundColor;
	
	/**
	 * Alpha
	 */
	private int backgroundAlpha;
	
	/**
	 * Foreground alpha
	 */
	private int foregroundAlpha;
	
	/**
	 * If this widget can listen to user input
	 */
	private boolean focus;
	
	/**
	 * Event Dispatcher
	 */
	private EventDispatcher eventDispatcher;
	
	/**
	 * Widgets
	 */
	private List<Widget> widgets;

	/**
	 * Key Listeners
	 */
	private List<Inputs> inputListeners;
	
	
	/**
	 * Disable
	 */
	private boolean disabled;
	
	/**
	 * Can this widget be seen
	 */
	private boolean visible;
	
	/**
	 * Id
	 */
	private int id;
	
	/**
	 * Name of this widget
	 */
	private String name;
	
	/**
	 * Parent widget
	 */
	private Widget parent;
	
	/**
	 * Screen position
	 */
	private Vector2f screenPosition;
	
	/**
	 * Screen bounds
	 */
	private Rectangle screenBounds;
	
	/**
	 * enable gradiant
	 */
	private boolean enableGradiant;
	
	/**
	 * Gradiant color
	 */
	private int gradiantColor;
	
	/**
	 * @param eventDispatcher
	 */
	public Widget(EventDispatcher eventDispatcher) {		
		this.eventDispatcher = eventDispatcher;
		this.id = idGenerator.getAndIncrement();
		
		this.bounds = new Rectangle();
		this.backgroundColor = 0;
		this.backgroundAlpha = 255;
		this.foregroundColor = 0xffFFffFF;
		this.foregroundAlpha = 255;
		this.enableGradiant = true;
		this.gradiantColor = 0xff999999;// new Vector3f(0.63f,0.63f,0.63f);
		this.focus = true;
		
		
		this.screenPosition = new Vector2f();
		this.screenBounds = new Rectangle();
		
		this.widgets = new ArrayList<Widget>();
		this.inputListeners = new ArrayList<Inputs>();
				
		this.disabled = false;
		this.visible = true;
		
		this.parent = null;
		
		this.name = "";
		
		globalInputListener.addWidget(this);
	}

	public void setTheme(Theme theme) {
		setForegroundColor(theme.getForegroundColor());
		setBackgroundColor(theme.getBackgroundColor());
	}
	
	/**
	 * @param eventDispatcher
	 */
	public Widget() {
		this(new EventDispatcher());
	}
	
	/**
	 * @return the unique id for this widget
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Destroy this {@link Widget}.
	 */
	public void destroy() {
		destroyChildren();
		this.parent = null;
		
		this.inputListeners.clear();
		
		globalInputListener.removeWidget(this);
	}
	
	/**
	 * Destroys the children widgets
	 */
	protected void destroyChildren() {
		for(Widget w : this.widgets) {
			w.destroy();
		}
		
		this.widgets.clear();
	}

	/**
	 * @return
	 */
	protected EventDispatcher getEventDispatcher() {
		return this.eventDispatcher;
	}
	
	
	public void addInputListenerToFront(Inputs input) {
		this.inputListeners.add(0, input);
	}
	
	public void addInputListener(Inputs input) {
		this.inputListeners.add(input);
	}
	
	public void removeInputListener(Inputs input) {
		this.inputListeners.remove(input);
	}
	
	/**
	 * Fire a {@link KeyEvent}
	 * @param keyEvent
	 * @param isPressed
	 * @return true if the event was consumed
	 */
	protected boolean fireKeyTypedEvent(char keyEvent) {
		int size = this.inputListeners.size();
		
		boolean isConsumed = false;
		for(int i = 0; i < size; i++ ) {
			Inputs l = this.inputListeners.get(i);
			
			isConsumed = l.keyTyped(keyEvent);
			
			/* is the event done with? */
			if ( isConsumed ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Fire a {@link KeyEvent}
	 * @param keyEvent
	 * @param isPressed
	 * @return true if the event was consumed
	 */
	protected boolean fireKeyEvent(int keyEvent, boolean isPressed) {
		int size = this.inputListeners.size();
		
		boolean isConsumed = false;
		for(int i = 0; i < size; i++ ) {
			Inputs l = this.inputListeners.get(i);
			
			if ( isPressed ) {
				isConsumed = l.keyDown(keyEvent);
			}
			else {
				isConsumed = l.keyUp(keyEvent);
			}
			
			/* is the event done with? */
			if ( isConsumed ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Fire a {@link MouseEvent}
	 * @param mouseEvent
	 * @param isPressed
	 * @return true if the event was consumed
	 */
	protected boolean fireMouseEvent(int mx, int my, int pointer, int mouseEvent, boolean isPressed) {
		int size = this.inputListeners.size();
		boolean isConsumed = false;
		
		for(int i = 0; i < size; i++ ) {
			Inputs l = this.inputListeners.get(i);				
			if ( isPressed ) {
				isConsumed = l.touchDown(mx, my, pointer, mouseEvent);
			}
			else {
				isConsumed = l.touchUp(mx, my, pointer, mouseEvent);
			}
			
			/* is the event done with? */
			if ( isConsumed ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Fire a {@link MouseEvent}
	 * @param mouseEvent
	 * @param isPressed
	 * @return true if the event was consumed
	 */
	protected boolean fireMouseMotionEvent(int mx, int my) {
		int size = this.inputListeners.size();
		for(int i = 0; i < size; i++ ) {
			Inputs l = this.inputListeners.get(i);
						
			/* is the event done with? */
			if ( l.mouseMoved(mx, my) ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Fire a mouse scrolled event
	 * @param amount
	 * @return true if the event was consumed
	 */
	protected boolean fireMouseScrolledEvent(int amount) {
		int size = this.inputListeners.size();
		for(int i = 0; i < size; i++ ) {
			Inputs l = this.inputListeners.get(i);
						
			/* is the event done with? */
			if ( l.scrolled(amount) ) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Fire a touch dragged event
	 * 
	 * @param mouseEvent
	 * @param isPressed
	 * @return true if the event was consumed
	 */
	protected boolean fireTouchDraggedEvent(int mx, int my, int btn) {
		int size = this.inputListeners.size();
		for(int i = 0; i < size; i++ ) {
			Inputs l = this.inputListeners.get(i);
						
			/* is the event done with? */
			if ( l.touchDragged(mx, my, btn) ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the parent
	 */
	public Widget getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Widget parent) {
		this.parent = parent;
	}
	
	/**
	 * @return the focus
	 */
	public boolean hasFocus() {
		return focus;
	}

	/**
	 * @param focus the focus to set
	 */
	public void setFocus(boolean focus) {
		this.focus = focus;
	}

	/**
	 * @return the bounds - the bounds position is relative to the
	 * parent widgets bounds.
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	/**
	 * Get the screen coordinates.
	 * @return the screen position of this widget.
	 */
	public Vector2f getScreenPosition() {
		
		/* Get the position of this widget */
		this.screenPosition.set(this.bounds.x, this.bounds.y);
		
		/* now get the position of the parent widgets */
		for(Widget p = this.parent; p != null; p = p.getParent()) {
			this.screenPosition.x += p.bounds.x;
			this.screenPosition.y += p.bounds.y;
		}
		
		return this.screenPosition;
	}

	/**
	 * Get the screen coordinates with width and height of the Widget.
	 * @return
	 */
	public Rectangle getScreenBounds() {
		this.screenBounds.set(getScreenPosition(), this.bounds.width, this.bounds.height);
		return this.screenBounds;
	}
	
	/**
	 * @param bounds the bounds to set
	 */
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return the color
	 */
	public int getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param color the color to set
	 */
	public void setBackgroundColor(int color) {
		this.backgroundColor = color;
	}

	/**
	 * @return the alpha
	 */
	public int getBackgroundAlpha() {
		return backgroundAlpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setBackgroundAlpha(int alpha) {
		this.backgroundAlpha = alpha;
	}
	
	
	
	/**
	 * @return the foregroundColor
	 */
	public int getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * @param foregroundColor the foregroundColor to set
	 */
	public void setForegroundColor(int foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	/**
	 * @return the foregroundAlpha
	 */
	public int getForegroundAlpha() {
		return foregroundAlpha;
	}

	/**
	 * @param foregroundAlpha the foregroundAlpha to set
	 */
	public void setForegroundAlpha(int foregroundAlpha) {
		this.foregroundAlpha = foregroundAlpha;
	}

	
	
	/**
	 * @return the enableGradiant
	 */
	public boolean gradiantEnabled() {
		return enableGradiant;
	}

	/**
	 * @param enableGradiant the enableGradiant to set
	 */
	public void setEnableGradiant(boolean enableGradiant) {
		this.enableGradiant = enableGradiant;
	}

	/**
	 * @return the gradiantColor
	 */
	public int getGradiantColor() {
		return gradiantColor;
	}

	/**
	 * @param gradiantColor the gradiantColor to set
	 */
	public void setGradiantColor(int gradiantColor) {
		this.gradiantColor = gradiantColor;
	}

	/**
	 * @param w
	 */
	public void addWidget(Widget w) {
		if ( w != null ) {
			w.setParent(this);
			this.widgets.add(w);
		}
		
	}
	
	/**
	 * @param w
	 */
	public void removeWidget(Widget w) {
		if ( w != null ) {
			if ( this.widgets.remove(w) ) {
				w.setParent(null);
			}
		}
	}
	
	/**
	 * @return
	 */
	public List<Widget> getWidgets() {
		return this.widgets;
	}
	
	
	
	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}



	/**
	 * @return the visable
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visable the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Hide the button, disabling it.
	 */
	public void hide() {
		setDisabled(true);
		setVisible(false);
		
		int size = this.widgets.size();
		for(int i = 0; i < size; i++) {
			this.widgets.get(i).hide();
		}
	}
	
	/**
	 * Show the button, enabling it.
	 */
	public void show() {
		setDisabled(false);
		setVisible(true);
		
		int size = this.widgets.size();
		for(int i = 0; i < size; i++) {
			this.widgets.get(i).show();
		}
	}

	/**
	 * Global Widget listener.  This gets bound to the Myriad {@link InputSystem}
	 * @author Tony
	 *
	 */
	static class WidgetInputListener extends Inputs {

		/**
		 * Widgets
		 */
		private List<Widget> globalWidgets;
		
		/**
		 * Are we destroying right now?
		 */
		private boolean isDestroying;
		
		/**
		 */
		public WidgetInputListener() {
			this.globalWidgets = new ArrayList<Widget>();
			this.isDestroying = false;
		}
		
		/**
		 * Destroys the widgets
		 */
		public void destroy() {
			this.isDestroying = true;
			for(Widget w: this.globalWidgets) {
				w.destroy();
			}			
			this.globalWidgets.clear();
			this.isDestroying = false;
		}
		
		/**
		 * @param w
		 */
		public void addWidget(Widget w) {
			this.globalWidgets.add(w);
		}
		
		/**
		 * @param w
		 */
		public void removeWidget(Widget w) {
			// don't remove from this list if 
			// we are destroying all Widgets - avoids
			// concurrent modification exception
			if ( ! this.isDestroying ) {
				this.globalWidgets.remove(w);
			}
		}
		
		
		/* (non-Javadoc)
		 * @see seventh.client.Inputs#keyTyped(char)
		 */
		@Override
		public boolean keyTyped(char key) {		
			int size = globalWidgets.size();
			for(int i = 0; i < size; i++) {
				Widget widget = this.globalWidgets.get(i);
				if ( widget.hasFocus() && ! widget.isDisabled() ) {
					if ( widget.fireKeyTypedEvent(key) ) {
						return true;
					}
				}
			}
			
			return false;
		}
		
		/*
		 * (non-Javadoc)
		 * @see seventh.client.Inputs#keyDown(int)
		 */
		public boolean keyDown(int event) {		
			int size = globalWidgets.size();
			for(int i = 0; i < size; i++) {
				Widget widget = this.globalWidgets.get(i);
				if ( widget.hasFocus() && ! widget.isDisabled() ) {
					if ( widget.fireKeyEvent(event, true) ) {
						return true;
					}
				}
			}
			
			return false;
		}
	
		/* (non-Javadoc)
		 * @see org.myriad.input.KeyListener#keyReleased(org.myriad.input.KeyEvent)
		 */
		public boolean keyUp(int event) {
			int size = globalWidgets.size();
			for(int i = 0; i < size; i++) {
				Widget widget = this.globalWidgets.get(i);
				if ( widget.hasFocus() && ! widget.isDisabled() ) {
					if ( widget.fireKeyEvent(event, false) ) {
						return true;
					}
				}
			}
			return false;
		}
	
		/* (non-Javadoc)
		 * @see seventh.client.Inputs#touchDown(int, int, int, int)
		 */
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			int size = globalWidgets.size();
			for(int i = 0; i < size; i++) {
				Widget widget = this.globalWidgets.get(i);
				if ( /*widget.hasFocus() &&*/ ! widget.isDisabled() ) {
					if ( widget.fireMouseEvent(x, y, pointer, button, true) ) {
						return true;
					}
				}
			}
			return false;
		}
	
		/* (non-Javadoc)
		 * @see org.myriad.input.MouseListener#mouseButtonReleased(org.myriad.input.MouseEvent)
		 */
		public boolean touchUp(int x, int y, int pointer, int button) {
			int size = globalWidgets.size();
			for(int i = 0; i < size; i++) {
				Widget widget = this.globalWidgets.get(i);
				if ( /*widget.hasFocus() &&*/ ! widget.isDisabled() ) {
					if ( widget.fireMouseEvent(x, y, pointer, button, false) ) {
						return true;
					}
				}
			}
			return false;
		}
		
		/* (non-Javadoc)
		 * @see seventh.client.Inputs#touchDragged(int, int, int)
		 */
		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			int size = globalWidgets.size();
			for(int i = 0; i < size; i++) {
				Widget widget = this.globalWidgets.get(i);
				if ( /*widget.hasFocus() &&*/ ! widget.isDisabled() ) {
					if ( widget.fireTouchDraggedEvent(x, y, pointer) ) {
						return true;
					}
				}
			}
			return false;
		}
		
		/* (non-Javadoc)
		 * @see seventh.client.Inputs#mouseMoved(int, int)
		 */
		@Override
		public boolean mouseMoved(int x, int y) {
			int size = globalWidgets.size();
			for(int i = 0; i < size; i++) {
				Widget widget = this.globalWidgets.get(i);
				if ( /*widget.hasFocus() &&*/ ! widget.isDisabled() ) {
					if ( widget.fireMouseMotionEvent(x,y) ) {
						return true;
					}
				}
			}
			return false;
		}
		
		/* (non-Javadoc)
		 * @see seventh.client.Inputs#scrolled(int)
		 */
		@Override
		public boolean scrolled(int amount) {
			int size = globalWidgets.size();
			for(int i = 0; i < size; i++) {
				Widget widget = this.globalWidgets.get(i);
				if ( /*widget.hasFocus() &&*/ ! widget.isDisabled() ) {
					if ( widget.fireMouseScrolledEvent(amount) ) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
