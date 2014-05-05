/*
 *	leola-live 
 *  see license.txt
 */
package seventh.client.gfx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Simple image utility functions
 * 
 * @author Tony
 *
 */
public class ImageUtil {
	
//	public static final void main(String [] args) throws Exception {
//		BufferedImage img = loadImage("./seventh/gfx/lightmap_flashlight.png");
//		System.out.println(img);
//		
//		BufferedImage alpha = new BufferedImage(img.getWidth(), img.getHeight(), Transparency.BITMASK);
//
//		Graphics2D g = alpha.createGraphics();
//		g.setComposite(AlphaComposite.Src);
//		g.drawImage(img, 0, 0, null);
//		g.dispose();
//		
//		for (int y = 0; y < alpha.getHeight(); y++) {
//			for (int x = 0; x < alpha.getWidth(); x++) {
//				int col = alpha.getRGB(x, y);
//				if ( (col<<8) == 0x0) {
//					alpha.setRGB(x, y, col & 0x00ffffff);
//				}
//				else if ( ((col<<8)>>8) < 0x090909)
//				{
//					// make transparent
//					alpha.setRGB(x, y, col & 0x00ffffff);
//				}
//			}
//		}
//		alpha.flush();
//		
//		ImageIO.write(alpha, "png", new File("./test.png"));
//		
//	}
	
	/**
	 * Loads an image
	 *
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public static BufferedImage loadImage(String image) throws Exception {
		return ImageIO.read(new File(image));
	}

	/**
	 * Gets a subImage.
	 *
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage subImage(BufferedImage image, int x, int y, int width, int height) {
		return image.getSubimage(x, y, width, height);
	}

	/**
	 * Applying mask into image using specified masking color. Any Color in the
	 * image that matches the masking color will be converted to transparent.
	 * 
	 * @param img The source image
	 * @param keyColor Masking color
	 * @return Masked image
	 */
	public static BufferedImage applyMask(BufferedImage img, Color keyColor) {
		BufferedImage alpha = new BufferedImage(img.getWidth(), img.getHeight(), Transparency.BITMASK);

		Graphics2D g = alpha.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		for (int y = 0; y < alpha.getHeight(); y++) {
			for (int x = 0; x < alpha.getWidth(); x++) {
				int col = alpha.getRGB(x, y);
				if (col == keyColor.getRGB()) {
					// make transparent
					alpha.setRGB(x, y, col & 0x00ffffff);
				}
			}
		}
		alpha.flush();
		
		return alpha;
	}
	
	/**
	 * Resizes the image
	 *
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
		BufferedImage newImage = new BufferedImage(width, height, image.getColorModel().getTransparency());

		Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, width, height, // destination
                0, 0, image.getWidth(), image.getHeight(), // source
                null);
        g.dispose();

		return newImage;
	}

	public static BufferedImage createImage(int width, int height) {
		BufferedImage newImage = new BufferedImage(width, height, Transparency.TRANSLUCENT);
		return newImage;
	}

	/**
	 * Creates a tileset from an image
	 * 
	 * @param image
	 * @param tileWidth
	 * @param tileHeight
	 * @param margin
	 * @param spacing
	 * @return
	 */
	public static BufferedImage[] toTileSet(BufferedImage image, int tileWidth, int tileHeight, int margin, int spacing) {
		int nextX = margin;
		int nextY = margin;
		
		List<BufferedImage> images = new ArrayList<BufferedImage>();
		while (nextY + tileHeight + margin <= image.getHeight()) {
            BufferedImage tile =
                image.getSubimage(nextX, nextY, tileWidth, tileHeight);
            images.add(tile);
            
            nextX += tileWidth + spacing;

            if (nextX + tileWidth + margin > image.getWidth()) {
                nextX = margin;
                nextY += tileHeight + spacing;
            }            
		}
		
		return images.toArray(new BufferedImage[images.size()]);
	}
	
	/**
	 * Splits the image, uses the image width/height 
	 * @param image
	 * @param row
	 * @param col
	 * @return
	 */
	public static BufferedImage[] splitImage(BufferedImage image, int row, int col) {
		return splitImage(image, image.getWidth(), image.getHeight(), row, col);
	}
	
	/**
	 * Splits the image 
	 * @param image
	 * @param width
	 * @param height
	 * @param row
	 * @param col
	 * @return
	 */
	public static BufferedImage[] splitImage(BufferedImage image, int width, int height, int row, int col) {
		int total = col * row; // total returned images
		int frame = 0; // frame counter

		int w = width / col;
		int h = height / row;

		BufferedImage[] images = new BufferedImage[total];

		for (int j = 0; j < row; j++) {
			for (int i = 0; i < col; i++) {
				BufferedImage tmp = image.getSubimage(i * w, j * h, w, h);
				images[frame++] = tmp;
			}
		}

		return images;
	}


	/**
	 * A structure which removes the duplicate sub-images and contains an mapping to
	 * how to render the full image.
	 *
	 * @author Tony
	 *
	 */
	public static class OptimizedImage {
		public BufferedImage[] images;
		public int[] order;

		public final int width;
		public final int height;

		public final int row;
		public final int col;

		public OptimizedImage(BufferedImage[] images
							, int[] order
							, int width
							, int height
							, int row
							, int col) {
			this.images = images;
			this.order = order;
			this.width = width;
			this.height = height;
			this.row = row;
			this.col = col;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

	}


	public static OptimizedImage optimizeImage(BufferedImage image, int row, int col) {
		BufferedImage[] images = splitImage(image, row, col);
		int[] order = new int[images.length];
		for(int i = 0; i < order.length; i++) order[i] = i;

		images = removeDuplicates(images, order);

		int width = image.getWidth();
		int height = image.getHeight();

		return new OptimizedImage(images, order, width, height, row, col);
	}

	public static BufferedImage[] removeDuplicates(BufferedImage[] image, int[] order) {
		Arrays.fill(order, -1);

		// Assumes all sub-images are equal size
		int w = image[0].getWidth();
		int h = image[0].getHeight();

		for(int i = 0; i < image.length; i++) {
			if(order[i] > -1) {
				image[i] = null; /* remove the image */
				continue;
			}

			BufferedImage left = image[i];
			scanForDuplicates(left, image, i, order, w, h);
			order[i] = i;
		}

		return image;
	}

	private static void scanForDuplicates(BufferedImage leftImage, BufferedImage[] images, int i, int[] order, int width, int height) {

		for(int j = i+1; j < images.length; j++) {
			BufferedImage right = images[j];
			if( right != null ) {
				if(isDataEqual(leftImage, right, width, height)) {
					order[j] = i;
				}
			}
		}
	}

	/**
	 * Tests to see if the data is equal
	 *
	 * @param left
	 * @param right
	 * @param width
	 * @param height
	 * @return
	 */
	private static boolean isDataEqual(BufferedImage left, BufferedImage right, int width, int height) {

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(left.getRGB(x, y) != right.getRGB(x, y)) {
					return false;
				}
			}
		}
		return true;
	}
}

