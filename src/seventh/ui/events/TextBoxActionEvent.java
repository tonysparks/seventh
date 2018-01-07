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

import seventh.shared.Event;
import seventh.ui.TextBox;

/**
 * A {@link TextBox} had enter pressed
 * 
 * @author Tony
 *
 */
public class TextBoxActionEvent extends Event {

    /**
     * TextBox
     */
    private TextBox textBox;

    /**
     * @param source
     * @param textBox
     */
    public TextBoxActionEvent(Object source, TextBox textBox) {
        super(source);
        this.textBox = textBox;
    }

    /**
     * @return the TextBox
     */
    public TextBox getTextBox() {
        return textBox;
    }

    /**
     * @param TextBox the TextBox to set
     */
    public void setTextBox(TextBox textBox) {
        this.textBox = textBox;
    }
        
}
