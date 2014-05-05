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
import seventh.ui.ListBox.ItemListener;

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
	
	/**
	 * 
	 */
	public ListBoxView(ListBox box) {
		this.buttonViews = new ArrayList<ButtonView>();
		
		this.box = box;
		this.box.setItemListener(new ItemListener() {
			
			@Override
			public void onItemRemove(Button button) {
				int removeIndex = -1;
				int i = 0;
				for(ButtonView view : buttonViews) {
					if(view.getButton()==button) {
						removeIndex = i;
						break;
					}
					i++;
				}
				
				if(removeIndex > -1) {
					buttonViews.remove(removeIndex);
				}
			}
			
			@Override
			public void onItemAdded(Button button) {
				buttonViews.add(new ButtonView(button));
			}
		});
		
		for(Button btn : box.getItems()) {
			buttonViews.add(new ButtonView(btn));
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
	public void render(Canvas renderer, Camera camera, long alpha) {
		
		Rectangle bounds = box.getBounds();
		
		renderer.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, box.getBackgroundColor());
		renderer.drawRect(bounds.x-1, bounds.y, bounds.width+1, bounds.height+1, 0xff000000);
		
		bounds = box.getScreenBounds();
		int y = 30;
		
		int size = buttonViews.size();
		for(int i = box.getIndex(); i < size; i++) {
			ButtonView view = this.buttonViews.get(i);
			Button btn = view.getButton();
			btn.getBounds().y = y;
			Rectangle rect = btn.getScreenBounds();
			//rect.y = y;
			if(bounds.contains(rect)) 
			{
				if(btn.isHovering()) {
					renderer.fillRect(rect.x-10, rect.y-5, rect.width+40, rect.height, 0x0fffffff);
				}
				view.render(renderer, camera, alpha);
			}
			y += 30;
		}
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
	}

}
