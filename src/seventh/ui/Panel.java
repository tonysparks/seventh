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


/**
 * A {@link Panel} is just a means for grouping widgets together.
 * @author Tony
 *
 */
public class Panel extends Widget {

    
    private int borderColor;
    private int borderWidth;
    
    /**
     */
    public Panel() {
        this.borderColor = 0;
        this.borderWidth = 0;
    }
    
    /**
     * @return the borderColor
     */
    public int getBorderColor() {
        return borderColor;
    }
    
    /**
     * @param borderColor the borderColor to set
     */
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }
    
    /**
     * @return the borderWidth
     */
    public int getBorderWidth() {
        return borderWidth;
    }
    
    /**
     * @param borderWidth the borderWidth to set
     */
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

}
