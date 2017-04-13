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

import seventh.client.gfx.Theme;

/**
 * A Simple text label.
 * 
 * @author Tony
 *
 */
public class Label extends Widget {

    /**
     * Text alignment.
     * 
     * @author Tony
     *
     */
    public enum TextAlignment {
        CENTER
        , LEFT
        , RIGHT
        , TOP
        , BOTTOM
    }
    
    /**
     * Text alignment
     */
    private TextAlignment horizontalTextAlignment;
    private TextAlignment verticalTextAlignment;
    
    /**
     * The text on the label
     */
    private String text;
    private String font;
    
    /**
     * Text size
     */
    private float textSize;
    
    /**
     * Ignore carriage return
     */
    private boolean ignoreCR;
    private boolean shadow;
    
    /**
     * @param text
     */
    public Label(String text) {
        this.text = text;
        this.textSize = 12;
        this.ignoreCR = true;
        this.shadow = true;
        
        this.horizontalTextAlignment = TextAlignment.CENTER;
        this.verticalTextAlignment = TextAlignment.CENTER;
        this.font = Theme.DEFAULT_FONT;
    }
    
    /**
     */
    public Label() {
        this("");
    }

    
    /**
     * @return the font
     */
    public String getFont() {
        return font;
    }
    
    /**
     * @param font the font to set
     */
    public void setFont(String font) {
        this.font = font;
    }
    
    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the textSize
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * @param textSize the textSize to set
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }
    
    /**
     * @return the shadow
     */
    public boolean isShadowed() {
        return shadow;
    }
    
    /**
     * @param shadow the shadow to set
     */
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    /**
     * @return the ignoreCR
     */
    public boolean ignoreCR() {
        return ignoreCR;
    }

    /**
     * @param ignoreCR the ignoreCR to set
     */
    public void setIgnoreCR(boolean ignoreCR) {
        this.ignoreCR = ignoreCR;
    }        
    
    /**
     * @param horizontalTextAlignment the horizontalTextAlignment to set
     */
    public void setHorizontalTextAlignment(TextAlignment textAlignment) {
        this.horizontalTextAlignment = textAlignment;
    }
    
    /**
     * @return the horizontalTextAlignment
     */
    public TextAlignment getHorizontalTextAlignment() {
        return horizontalTextAlignment;
    }
    
    /**
     * @param verticalTextAlignment the verticalTextAlignment to set
     */
    public void setVerticalTextAlignment(TextAlignment verticalTextAlignment) {
        this.verticalTextAlignment = verticalTextAlignment;
    }
    
    /**
     * @return the verticalTextAlignment
     */
    public TextAlignment getVerticalTextAlignment() {
        return verticalTextAlignment;
    }
}
