/*
 * see license.txt 
 */
package seventh.shared;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * @author Tony
 *
 */
public class ImageScaler {

	/**
	 * 
	 */
	public ImageScaler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		File imageFile = new File("C:\\Users\\Tony\\Desktop\\SpriteSheetPacker\\Test\\allied_sprint\\output.png");
//		File imageFile = new File("C:\\Users\\Tony\\git\\seventh\\seventh\\gfx\\player\\allied_positions.png");
		BufferedImage image = ImageIO.read(imageFile);
//		BufferedImage scaledImage = new BufferedImage(image.getWidth(), 256, BufferedImage.TRANSLUCENT);
		BufferedImage scaledImage = new BufferedImage(256, 256, BufferedImage.TRANSLUCENT);
		Graphics2D g = (Graphics2D) scaledImage.getGraphics();
		
		float scale = 0.77f;
		g.scale(scale, scale);
		g.drawImage(image, -5, -5, null);
		
		ImageIO.write(scaledImage, "png", new File(imageFile.getParentFile(), "output2.png"));
		//ImageIO.write(scaledImage, "png", imageFile);
	}

}
