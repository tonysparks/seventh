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
import seventh.shared.TimeStep;

/**
 * Renders a group of elements
 * 
 * @author Tony
 *
 */
public class PanelView<T extends Renderable> implements Renderable {

	/**
	 * Elements
	 */
	private List<T> uiElements;
	
	/**
	 * 
	 */
	public PanelView() {
		this.uiElements = new ArrayList<T>();
	}
	
	public void clear() {
		this.uiElements.clear();
	}
	
	/**
	 * Adds an element
	 * @param element
	 */
	public void addElement(T element) {
		this.uiElements.add(element);
	}
	
	/**
	 * @return the uiElements
	 */
	public List<T> getUiElements() {
		return uiElements;
	}
	
	/* (non-Javadoc)
	 * @see org.myriad.render.Renderable#render(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
	 */
	@Override
	public void render(Canvas renderer, Camera camera, long alpha) {
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
