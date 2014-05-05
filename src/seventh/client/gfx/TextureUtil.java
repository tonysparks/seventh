/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx;


import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class TextureUtil {

	public static TextureRegion tex(Pixmap pixmap) {
		return new TextureRegion(new Texture(pixmap));
	}
	
	public static TextureRegion tex(Texture tex) {
		return new TextureRegion(tex);
	}
	
	/**
	 * Loads an image
	 *
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public static TextureRegion loadImage(String image) throws Exception {
		Texture texture = new Texture(Gdx.files.internal(image));
		TextureRegion region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
		region.flip(false, true);
		return region;
	}
	
	/**
	 * Loads an image
	 *
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public static TextureRegion loadImage(String image, int width, int height) throws Exception {
		Texture texture = new Texture(Gdx.files.internal(image));
		TextureRegion region = new TextureRegion(texture, 0, 0, width, height);
		region.flip(false, true);
		return region;
	}
	
	/**
	 * Loads an image
	 *
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public static Pixmap loadPixmap(String image) throws Exception {		
		return new Pixmap(Gdx.files.internal(image));		
	}
	
	/**
	 * Loads an image
	 *
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public static Pixmap loadPixmap(String image, int width, int height) throws Exception {		
		Pixmap pixmap = new Pixmap(Gdx.files.internal(image));
		Pixmap result = pixmap;
		if(pixmap.getWidth() != width || pixmap.getHeight() != height) {
			result = resizePixmap(pixmap, width, height);
			pixmap.dispose();
		}
		
		return result;
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
	public static TextureRegion subImage(TextureRegion image, int x, int y, int width, int height) {
		return new TextureRegion(image, x, y, width, height);
	}

	public static Pixmap subPixmap(Pixmap pix, int x, int y, int width, int height) {
		Pixmap sub = new Pixmap(width, height, pix.getFormat());
		sub.drawPixmap(pix, x, y, width, height, 0, 0, width, height);
		return sub;
	}
	
	/**
	 * Applying mask into image using specified masking color. Any Color in the
	 * image that matches the masking color will be converted to transparent.
	 * 
	 * @param img The source image
	 * @param keyColor Masking color
	 * @return Masked image
	 */
	public static Pixmap applyMask(Pixmap img, Color keyColor) {
		Pixmap alpha = new Pixmap(img.getWidth(), img.getHeight(), Format.RGBA8888);		
		//alpha.drawPixmap(img, 0, 0);
			
		int width = alpha.getWidth();
		int height = alpha.getHeight();
		
		int colorMask = Color.rgba8888(keyColor);
		
		//alpha.setColor(0xff00009f);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int col = img.getPixel(x, y);
				if ( col != colorMask ) {
					alpha.drawPixel(x, y, img.getPixel(x, y));
				}
			}
		}
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
	public static Pixmap resizePixmap(Pixmap image, int width, int height) {
		Pixmap pix = new Pixmap(width, height, image.getFormat());
		pix.drawPixmap(image, 0, 0, width, height, // destination
				 0, 0, image.getWidth(), image.getHeight()); // source						 
		return pix;
	}

	public static Pixmap createPixmap(int width, int height) {
		return new Pixmap(width, height, Format.RGBA8888);
	}
	
	public static Texture createImage(int width, int height) {
//		BufferedImage newImage = new BufferedImage(width, height, Transparency.TRANSLUCENT);
		Texture texture = new Texture(width, height, Format.RGBA8888);
		return texture;
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
	public static TextureRegion[] toTileSet(TextureRegion image, int tileWidth, int tileHeight, int margin, int spacing) {
		int nextX = margin;
		int nextY = margin;
		
		List<TextureRegion> images = new ArrayList<TextureRegion>();
		while (nextY + tileHeight + margin <= image.getRegionHeight()) {
			TextureRegion tile = new TextureRegion(image, nextX, nextY, tileWidth, tileHeight);
			tile.flip(false, true);
			
            images.add(tile);
            
            nextX += tileWidth + spacing;

            if (nextX + tileWidth + margin > image.getRegionWidth()) {
                nextX = margin;
                nextY += tileHeight + spacing;
            }            
		}
		
		return images.toArray(new TextureRegion[images.size()]);
	}
	
	/**
	 * Splits the image, uses the image width/height 
	 * @param image
	 * @param row
	 * @param col
	 * @return
	 */
	public static TextureRegion[] splitImage(TextureRegion image, int row, int col) {
		return splitImage(image, image.getRegionWidth(), image.getRegionHeight(), row, col);
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
	public static TextureRegion[] splitImage(TextureRegion image, int width, int height, int row, int col) {				
		int total = col * row; // total returned images
		int frame = 0; // frame counter

		int w = width / col;
		int h = height / row;

		TextureRegion[] images = new TextureRegion[total];

		for (int j = 0; j < row; j++) {
			for (int i = 0; i < col; i++) {				
				TextureRegion region = new TextureRegion(image.getTexture(), i * w, j * h, w, h);
				//region.flip(false, true);
				images[frame++] = region;
			}
		}

		return images;
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
	public static Pixmap[] splitPixmap(Pixmap image, int width, int height, int row, int col) {				
		int total = col * row; // total returned images
		int frame = 0; // frame counter

		int w = width / col;
		int h = height / row;

		Pixmap[] images = new Pixmap[total];

		for (int j = 0; j < row; j++) {
			for (int i = 0; i < col; i++) {				
				Pixmap region = new Pixmap(w, h, image.getFormat());
				region.drawPixmap(image, 0, 0, i * w, j * h, w, h);
				
				images[frame++] = region;
			}
		}

		return images;
	}
	
	/**
	 * Resizes the image
	 *
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static Sprite resizeImage(TextureRegion image, int width, int height) {
		Sprite sprite = new Sprite(image);
		sprite.setSize(width, height);		
//		TextureRegion region = new TextureRegion(image);
//		region.setRegionWidth(width);
//		region.setRegionHeight(height);
		return sprite;
	}
}
