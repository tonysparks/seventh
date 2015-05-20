/*
 * see license.txt 
 */
package seventh.shared;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * Replaces the images color palate
 * 
 * @author Tony
 *
 */
public class ImageColorReplacer {

	/**
	 * 
	 */
	public ImageColorReplacer() {
	}

	static BufferedImage drawImage(Map<Integer, Integer> palette, BufferedImage image) {
		BufferedImage replacedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int color = image.getRGB(x, y);
				replacedImage.setRGB(x, y, palette.get(color));
			}
		}
		
		return replacedImage;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		File imageFile = new File("C:\\Users\\Tony\\git\\seventh\\seventh\\gfx\\player\\axis_positions.png");
		File replacedFile = new File(imageFile.getParentFile(), imageFile.getName() + ".replaced.png");
		BufferedImage image = ImageIO.read(imageFile);
		
		Map<Integer, Integer> colors = new HashMap<>();
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int col = image.getRGB(x, y);
				if(!colors.containsKey(col)) {
					
					Color c = new Color( (col & 0x00ff0000) >> 16,
										 (col & 0x0000ff00) >> 8,
										 (col & 0x000000ff) >> 0);
					int newColor = col;
					/*
					int rgb = col & 0x00FFFFFF;
					String sColor = Integer.toHexString(rgb);
					if(sColor.startsWith("a") || sColor.startsWith("e") ||
					   sColor.startsWith("c") || sColor.startsWith("1") ||
					   sColor.startsWith("b8") || sColor.startsWith("76") ) {
						newColor = (col & 0xff000000); 
						newColor = newColor | ((col & 0x00ff0000) >> 24); 
						newColor = newColor | ((col & 0x0000ff00) >> 0);
						newColor = newColor | ((col & 0x000000ff) << 16);
						
						newColor = 0x00;
					}*/
					
					int fuzzy = 20;
					if( Math.abs(c.getRed() - c.getBlue()) < fuzzy &&
					    Math.abs(c.getRed() - c.getGreen()) < fuzzy &&
					    Math.abs(c.getBlue() - c.getGreen()) < fuzzy ) {
						
						newColor = (col & 0xff000000); 
						newColor = newColor | ((Math.min(c.getRed() + 15, 255)) << 16); 
						newColor = newColor | ((Math.min(c.getGreen() + 25, 255)) << 8);
						newColor = newColor | ((Math.max(c.getBlue() - 30, 0)) << 0);
						//newColor = 0x00;
					}
					
					
					fuzzy = 30;
					if( c.getRed() - c.getBlue() > fuzzy &&
					    c.getRed() - c.getGreen() > fuzzy) {
						
						newColor = (col & 0xff000000); 
						newColor = newColor | ((Math.max(c.getRed() - 110, 0)) << 16); 
						newColor = newColor | ((Math.min(c.getGreen() + 55, 255)) << 8);
						newColor = newColor | ((Math.min(c.getBlue() + 20, 255)) << 0);
						//newColor = 0x00;
					}
					
					
					fuzzy = 30;
					if( c.getGreen() - c.getRed() > 9 &&
						c.getGreen() - c.getRed() < 25 &&
						
						c.getGreen() - c.getBlue() > 25 &&
						c.getGreen() - c.getBlue() < 40 &&
						
					    c.getRed() - c.getBlue() >= 15) {
						
						newColor = (col & 0xff000000); 
						newColor = newColor | ((Math.max(c.getRed() - 40, 0)) << 16); 
						newColor = newColor | ((Math.max(c.getGreen() - 55, 0)) << 8);
						newColor = newColor | ((Math.max(c.getBlue() - 65, 0)) << 0);
						//newColor = 0x00;
					}
					
					colors.put(col, newColor);
				}
			}
		}
		
		List<String> cStrs = new ArrayList<>();
		
		Set<String> ss = new HashSet<>();
		for(Integer color : colors.keySet()) {
			int rgb = color & 0x00FFFFFF;
			//cStrs.add(Integer.toHexString(rgb));
			ss.add(Integer.toHexString(rgb));
			//System.out.println(Integer.toHexString(rgb)); 
		//Integer.toString(color, 16));
		}
		
		cStrs.addAll(ss);
		Collections.sort(cStrs);
		
		for(String s : cStrs) {
			System.out.println(s + " : " + Integer.valueOf(s, 16));
		}
		
		ImageIO.write(drawImage(colors, image), "png", replacedFile);		
	}

}
