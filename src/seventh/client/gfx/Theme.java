/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx;


import com.badlogic.gdx.graphics.Color;

import seventh.ui.Skin;
import seventh.ui.Widget;


/**
 * Applies a consistent {@link Theme} for UI {@link Widget}s
 * 
 * @author Tony
 *
 */
public class Theme {

    public static final String DEFAULT_FONT = "Courier New";
    
    private int backgroundColor;
    private int foregroundColor;
    private int hoverColor;
    
    private String primaryFontName;
    private String primaryFontFile;
    
    private String secondaryFontName;
    private String secondaryFontFile;

    private Skin skin;

    
    /**
     * @param backgroundColor
     * @param foregroundColor
     * @param primaryFontName
     * @param primaryFontFile
     * @param secondaryFontName
     * @param secondaryFontFile
     */
    public Theme(int backgroundColor, int foregroundColor, int hoverColor,
            String primaryFontName, String primaryFontFile,
            String secondaryFontName, String secondaryFontFile) {
        super();
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.hoverColor = hoverColor;
        
        this.primaryFontName = primaryFontName;
        this.primaryFontFile = primaryFontFile;
        this.secondaryFontName = secondaryFontName;
        this.secondaryFontFile = secondaryFontFile;        
    }

    /**
     * Uses the default theme
     */
    public Theme() {        
        this(//Color.toIntBits(0, 0, 183, 255) 
                0xff4b5320
                , //0xffffffff 
               // 0xfff1f401
                Color.argb8888(1f,182f/255f, 224f/255f, 65f/255f)
                , 0xffffffff
//                , "Futurist Fixed-width"
//                , "./assets/gfx/fonts/future.ttf"
//                
//                , "Futurist Fixed-width"
//                , "./assets/gfx/fonts/future.ttf"
//                , "Bebas"                
//                , "./assets/gfx/fonts/Bebas.ttf"
//                , "Bebas"
//                , "./assets/gfx/fonts/Bebas.ttf"
                , "Napalm Vertigo"
                , "./assets/gfx/fonts/Napalm Vertigo.ttf"
                
                , "Army"
                , "./assets/gfx/fonts/Army.ttf"
                );
    }
    
    /**
     * @return the skin
     */
    public Skin getSkin() {
        if(this.skin == null) {
            this.skin = new Skin();
        }
        return skin;
    }
    
    /**
     * @return the fontFile
     */
    public String getPrimaryFontFile() {
        return primaryFontFile;
    }
    

    /**
     * @return the fontName
     */
    public String getPrimaryFontName() {
        return primaryFontName;
    }
    
    /**
     * @return the secondaryFontFile
     */
    public String getSecondaryFontFile() {
        return secondaryFontFile;
    }
    
    /**
     * @return the secondaryFontName
     */
    public String getSecondaryFontName() {
        return secondaryFontName;
    }
    
    /**
     * @return the backgroundColor
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @return the foregroundColor
     */
    public int getForegroundColor() {
        return foregroundColor;
    }
    
    /**
     * @return the hoverColor
     */
    public int getHoverColor() {
        return hoverColor;
    }
    
    /**
     * @return a new {@link Theme} based on this one
     */
    public Theme newTheme() {
        return new Theme(backgroundColor, foregroundColor, hoverColor, 
                primaryFontName, primaryFontFile, secondaryFontName, secondaryFontFile);
    }
    
    /**
     * @param backgroundColor the backgroundColor to set
     */
    public Theme setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }
    
    /**
     * @param foregroundColor the foregroundColor to set
     */
    public Theme setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }
    
    /**
     * @param hoverColor the hoverColor to set
     */
    public Theme setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
        return this;
    }
    
    /**
     * @param primaryFontFile the primaryFontFile to set
     */
    public Theme setPrimaryFontFile(String primaryFontFile) {
        this.primaryFontFile = primaryFontFile;
        return this;
    }
    
    /**
     * @param primaryFontName the primaryFontName to set
     */
    public Theme setPrimaryFontName(String primaryFontName) {
        this.primaryFontName = primaryFontName;
        return this;
    }
    
    /**
     * @param secondaryFontFile the secondaryFontFile to set
     */
    public Theme setSecondaryFontFile(String secondaryFontFile) {
        this.secondaryFontFile = secondaryFontFile;
        return this;
    }
    
    /**
     * @param secondaryFontName the secondaryFontName to set
     */
    public Theme setSecondaryFontName(String secondaryFontName) {
        this.secondaryFontName = secondaryFontName;
        return this;
    }
    

}
