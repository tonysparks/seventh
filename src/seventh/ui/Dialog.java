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

import seventh.math.Rectangle;


/**
 * A simple dialog box
 * 
 * @author Tony
 *
 */
public class Dialog extends Widget {

    /**
     * The text message
     */
    private Label text;
    
    

    /**
     * @param eventDispatcher
     * @param text
     */
    public Dialog(String text) {        
        this.text = new Label(text);
        addWidget(this.text);
    }

    /**
     * 
     */
    public Dialog() {
        this("");
    }
    
    /*
     * (non-Javadoc)
     * @see com.fived.ricochet.ui.Widget#setBounds(org.myriad.shared.math.Rectangle)
     */
    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
        this.text.getBounds().width = bounds.width;
        this.text.getBounds().height = bounds.height;
    }
    
    /**
     * 
     * @return the text label
     */
    public Label getTextLabel() {
        return this.text;
    }
    
    /**
     * @return the text
     */
    public String getText() {
        return text.getText();
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text.setText(text);
    }
    
    
}
