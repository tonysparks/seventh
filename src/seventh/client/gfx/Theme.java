/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx;

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
	private String primaryFontName;
	private String primaryFontFile;
	
	private String secondaryFontName;
	private String secondaryFontFile;


	
	/**
	 * @param backgroundColor
	 * @param foregroundColor
	 * @param primaryFontName
	 * @param primaryFontFile
	 * @param secondaryFontName
	 * @param secondaryFontFile
	 */
	public Theme(int backgroundColor, int foregroundColor,
			String primaryFontName, String primaryFontFile,
			String secondaryFontName, String secondaryFontFile) {
		super();
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
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
				0xfff1f401
//				, "Futurist Fixed-width"
//				, "./seventh/gfx/fonts/future.ttf"
//				
//				, "Futurist Fixed-width"
//				, "./seventh/gfx/fonts/future.ttf"
//				, "Bebas"				
//				, "./seventh/gfx/fonts/Bebas.ttf"
//				, "Bebas"
//				, "./seventh/gfx/fonts/Bebas.ttf"
				, "Napalm Vertigo"
				, "./seventh/gfx/fonts/Napalm Vertigo.ttf"
				
				, "Army"
				, "./seventh/gfx/fonts/Army.ttf"
				);
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

}
