/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 * 
 */
public class ShadeTiles {

	private final int startAlpha;
	private final int tileWidth, tileHeight;
	
	private int decayRate = 5;
	
	/**
	 * @param startAlpha
	 * @param tileWidth
	 * @param tileHeight
	 */
	public ShadeTiles(int startAlpha, int tileWidth, int tileHeight) {
		super();
//		this.startAlpha = startAlpha-20;
		this.startAlpha = startAlpha;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	/**
	 * @return the shaded tiles
	 */
	public TextureRegion[] createShadeTiles() {
		Pixmap bigImage = TextureUtil.createPixmap(tileWidth * 4, tileHeight * 4);
		Pixmap[] tiles = TextureUtil.splitPixmap(bigImage, bigImage.getWidth(), bigImage.getHeight(), 4, 4);

		//decayRate = 7;
		decayRate = 7;
		
		// 1's
		drawNorth(tiles[0]);
		drawEast(tiles[1]);
		drawSouth(tiles[2]);
		drawWest(tiles[3]);

		// 2's
		Pixmap image = tiles[4];
		drawNorth(image); drawEast(image);
		image = tiles[5];
		drawNorth(image); drawWest(image);
		image = tiles[6];
		drawSouth(image); drawEast(image);
		image = tiles[7];
		drawSouth(image); drawWest(image);
		
		// 3's
		image = tiles[8];
		drawNorth(image); drawWest(image); drawEast(image);
		
		image = tiles[9];
		drawSouth(image); drawWest(image); drawEast(image);
		
		image = tiles[10];
		drawNorth(image); drawSouth(image); drawWest(image);
		
		image = tiles[11];
		drawNorth(image); drawSouth(image); drawEast(image);

		
		// 2's separated
		image = tiles[12];
		drawNorth(image); drawSouth(image); 
		
		image = tiles[13];
		drawWest(image); drawEast(image);
		
		
		// 4's
		image = tiles[14];
		drawNorth(image); drawSouth(image); drawWest(image); drawEast(image); 
		
		
		TextureRegion[] regions = new TextureRegion[tiles.length];
		for(int i = 0; i < regions.length; i++) {
			regions[i] = TextureUtil.tex(tiles[i]);
			tiles[i].dispose();
		}
		
		return regions;
	}

	private void drawSouth(Pixmap image) {
		int currentAlpha = startAlpha;
		for (int i = 0; i < tileHeight; i++) {
			int color = (currentAlpha << 24);			
			drawLine(image, 0, i, tileWidth-1, i, color);
			currentAlpha -= decayRate;
			if (currentAlpha < 0) {
				break;
			}
		}
	}
	
	private void drawEast(Pixmap image) {
		int currentAlpha = startAlpha;
		for (int i = 0; i < tileWidth; i++) {
			int color = (currentAlpha << 24);
			
			drawLine(image, tileWidth - i - 1, 0, tileWidth - i - 1, tileHeight - 1, color);
			currentAlpha -= decayRate;
			if (currentAlpha < 0) {
				break;
			}
		}
	}
			
	private void drawNorth(Pixmap image) {
		int currentAlpha = startAlpha;
		for (int i = 0; i < tileHeight; i++) {
			int color = (currentAlpha << 24);			
			drawLine(image, 0, tileHeight - 1 - i, tileWidth-1, tileHeight - 1 - i, color);
			currentAlpha -= decayRate;  
			if (currentAlpha < 0) { 
				break;
			}
		}
	}
	
	private void drawWest(Pixmap image) {

		int currentAlpha = startAlpha;
		for (int i = 0; i < tileWidth; i++) {
			int color = (currentAlpha << 24);			
			drawLine(image, i, 0, i, tileHeight - 1, color);
			currentAlpha -= decayRate;
			if (currentAlpha < 0) {
				break;
			}
		}
	}
	
	private void drawLine(Pixmap image, int x0, int y0, int x1, int y1, int color) {

		if(color == 0) {
			return;
		}
		
		color = (color << 8) | (color >>> 24);
		//color = 0xff00ffff;
		// Uses the Bresenham Line Algorithm
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		int sx = 0;
		int sy = 0;

		if (x0 < x1)
			sx = 1;
		else
			sx = -1;
		if (y0 < y1)
			sy = 1;
		else
			sy = -1;

		int err = dx - dy;

		do {
			int pixel = image.getPixel(x0, y0);
			if(pixel==0) {				
				image.drawPixel(x0, y0, color);
			}
			else {
				//int alpha = ((color>>24)+(pixel>>24));///2;				
				//image.drawPixel(x0, y0, alpha<<24);
				
				int alpha = Math.min((pixel+color)/2, this.startAlpha);
				//int alpha = this.startAlpha/2;
				image.drawPixel(x0, y0, alpha);				
			}

			if (x0 == x1 && y0 == y1) {
				break;
			}

			int e2 = err * 2;

			if (e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}

			if (x0 == x1 && y0 == y1) {
				pixel = image.getPixel(x0, y0);
				if(pixel==0) {
					image.drawPixel(x0, y0, color);	
				}
				else {
					//int alpha = ((color>>24)+(pixel>>24));///2;				
					//image.drawPixel(x0, y0, alpha<<24);
					
					int alpha = Math.min( (pixel+color)/2, this.startAlpha);
					//int alpha = this.startAlpha/2;
					image.drawPixel(x0, y0, alpha);
				}
				break;
			}

			if (e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
		} while (true);
	}
}
