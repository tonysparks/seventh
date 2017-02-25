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
 * UI Styling.
 * 
 * @author Tony
 *
 */
public interface Styling {
    
    public static final int BLACK = 0xFF000000;
    public static final int WHITE = 0xFFffFFff;
    public static final int RED   = 0xFFff0000;
    
    /**
     * Styles a button.
     * 
     * @param button
     */
    public void styleButton(Button button);
    
    /**
     * Styles a Dialog
     * 
     * @param dialog
     */
    public void styleDialog(Dialog dialog);
}
