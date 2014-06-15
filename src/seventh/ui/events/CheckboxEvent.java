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
package seventh.ui.events;

import leola.frontend.listener.Event;
import seventh.ui.Checkbox;

/**
 * A {@link Checkbox} selection was altered
 * 
 * @author Tony
 *
 */
public class CheckboxEvent extends Event {

	/**
	 * Checkbox
	 */
	private Checkbox checkbox;

	/**
	 * @param source
	 * @param checkbox
	 */
	public CheckboxEvent(Object source, Checkbox checkbox) {
		super(source);
		this.checkbox = checkbox;
	}

	/**
	 * @return the checkbox
	 */
	public Checkbox getCheckbox() {
		return checkbox;
	}			
}
