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

import leola.frontend.listener.EventDispatcher;
import seventh.client.gfx.Theme;
import seventh.client.inputs.Inputs;
import seventh.client.sfx.Sounds;
import seventh.math.Rectangle;
import seventh.ui.events.ButtonEvent;
import seventh.ui.events.OnButtonClickedListener;

/**
 * A simple button for actions.
 * 
 * @author Tony
 *
 */
public class Button extends Widget {
	
	/**
	 * Text on the button if 
	 * any.
	 */
	private Label label;
		
	/**
	 * If the button is being pressed.
	 */
	private boolean isPressed;
	
	/**
	 * If we are hovering over the
	 * button
	 */
	private boolean isHovering;
	private float hoverTextSize;
	private float normalTextSize;
	
	private boolean border;
	
	/**
	 * Button event to reuse for
	 * this button.
	 */
	private ButtonEvent buttonEvent;			
	
	/**
	 * @param eventDispatcher
	 */
	public Button(EventDispatcher eventDispatcher) {
		super(eventDispatcher);
		
		this.label = new Label();
		addWidget(this.label);
		
		this.normalTextSize = 28;
		this.hoverTextSize = 34;
		this.label.setTextSize(28);
		
		this.border = true;
		
		this.buttonEvent = new ButtonEvent(this, this);
		addInputListener(new Inputs() {
			
			/* (non-Javadoc)
			 * @see seventh.client.Inputs#mouseMoved(int, int)
			 */
			@Override
			public boolean mouseMoved(int x, int y) {						
				super.mouseMoved(x, y);
				if ( ! isDisabled() ) {
					if ( getScreenBounds().contains(x,y)) {																			
						setHovering(true);		
						return false;
					}
				}
				setHovering(false);				
				return false;
			}
			
			@Override
			public boolean touchDown(int x, int y, int pointer, int button) {
				if ( ! isDisabled() ) {
					if ( getScreenBounds().contains(x,y)) {																			
						setPressed(true);
						click();
						return true;																
					}
				}
				
				setPressed(false);
				return false;
			}

			/* (non-Javadoc)
			 * @see seventh.client.Inputs#touchDown(int, int, int, int)
			 */
			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {
				setPressed(false);
				if ( ! isDisabled() ) {
					if ( getScreenBounds().contains(x,y)) {																									
						return true;																
					}
				}
								
				return false;				
			}
		});
		
	}
	
	/**
	 */
	public Button() {
		this(new EventDispatcher());
	}
	
	/**
	 * Adds a listener
	 * @param l
	 */
	public void addOnButtonClickedListener(OnButtonClickedListener l) {
		this.getEventDispatcher().addEventListener(ButtonEvent.class, l);
	}
	
	/**
	 * Removes a listener
	 * @param l
	 */
	public void removeOnButtonClickedListener(OnButtonClickedListener l) {
		this.getEventDispatcher().removeEventListener(ButtonEvent.class, l);
	}

	/**
	 * @param border the border to set
	 */
	public void setBorder(boolean border) {
		this.border = border;
	}
	
	/**
	 * @return true if there is a border
	 */
	public boolean hasBorder() {
		return this.border;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.fived.ricochet.ui.Widget#setBounds(org.myriad.shared.math.Rectangle)
	 */
	@Override
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		this.label.getBounds().width = bounds.width;
		this.label.getBounds().height = bounds.height;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ui.Widget#setTheme(seventh.client.gfx.Theme)
	 */
	@Override
	public void setTheme(Theme theme) {	
		super.setTheme(theme);
		label.setFont(theme.getPrimaryFontName());
	}
	
	/**
	 * @return the text label
	 */
	public Label getTextLabel() {
		return this.label;
	}
	
	/**
	 * @return the text
	 */
	public String getText() {
		return this.label.getText();
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.label.setText(text);
	}

	public void setTextSize(float size) {
		this.label.setTextSize(size);
		this.normalTextSize = size;
	}
	
	public void setHoverTextSize(float size) {
		this.hoverTextSize = size;
	}

	/**
	 * @return the isPressed
	 */
	public boolean isPressed() {
		return isPressed;
	}
	
	/**
	 * Set if the button was pressed.
	 * @param pressed
	 */
	private void setPressed(boolean pressed) {
		
		if ( pressed ) {
			this.label.getBounds().y = 5;
			if(!this.isPressed) {
				Sounds.playGlobalSound(Sounds.uiSelect);
			}
		}
		else {
			this.label.getBounds().y = 0;
		}
		
		this.isPressed = pressed;
	}

	/**
	 * @param isHovering the isHovering to set
	 */
	private void setHovering(boolean isHovering) {
				
		if(isHovering) {
			this.label.setTextSize(this.hoverTextSize);
			if(!this.isHovering) {
				Sounds.playGlobalSound(Sounds.uiHover);
			}
		}
		else {
			this.label.setTextSize(this.normalTextSize);
		}
		this.isHovering = isHovering;
	}
	
	/**
	 * @return the isHovering
	 */
	public boolean isHovering() {
		return isHovering;
	}
	
	/**
	 * Click the button.
	 */
	public void click() {		
		this.getEventDispatcher().sendNow(this.buttonEvent);				
	}
	

}
