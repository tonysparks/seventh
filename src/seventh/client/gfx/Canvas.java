/*
 *	leola-live 
 *  see license.txt
 */
package seventh.client.gfx;

import java.io.IOException;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

/**
 * @author Tony
 *
 */
public interface Canvas {

	public void enableAntialiasing(boolean b);
	public boolean isAntialiasingEnabled();
	
	public void preRender();

	public void postRender();
	
	public void fboBegin();
	public void fboEnd();
	public void bindFrameBuffer(int id);
	public Texture getFrameBuffer();
	
	public OrthographicCamera getCamera();
	
	public void begin();
	public void end();
	public void flush();
	
	
	public void setShader(ShaderProgram shader);
	public void pushShader(ShaderProgram shader);
	public void popShader();
	
//	public void beginShape();
//	public void endShape();

	public void setColor(int color, Integer alpha);
	public void setClearColor(Integer color);
	public int getColor();
	
	/**
	 * Loads the font and registers it
	 * @param filename
	 * @throws IOException
	 */
	public void loadFont(String filename, String alias) throws IOException;
	
	public void setFont(String fontName, int size);
	public void setDefaultFont();
	public void setDefaultFont(String fontName, int size);
	public GlythData getGlythData(String font, int size);
	public GlythData getGlythData();
	/**
	 * @param str
	 * @return the width in pixels that the supplied str takes up
	 */
	public int getWidth(String str);
	
	/**
	 * @param str
	 * @return the height in pixels that the supplied str takes up
	 */
	public int getHeight(String str);

	public void boldFont();

	public void italicFont();

	public void plainFont();

	public void resizeFont(float size);

	public void setClip(int x, int y, int width, int height);
	
	public void setCompositeAlpha(float a);
	public float getCompositeAlpha();

	/**
	 * @return the width
	 */
	public int getWidth();

	/**
	 * @return the height
	 */
	public int getHeight();

	public double toRadians(double degrees);

	public double toDegrees(double radians);

	public void drawLine(int x1, int y1, int x2, int y2, Integer color);
	public void drawLine(float x1, float y1, float x2, float y2, Integer color);

	public void drawRect(int x, int y, int width, int height, Integer color);
	public void drawRect(float x, float y, float width, float height, Integer color);

	public void fillRect(int x, int y, int width, int height, Integer color);
	public void fillRect(float x, float y, float width, float height, Integer color);

	public void drawCircle(float radius, int x, int y, Integer color);
	public void drawCircle(float radius, float x, float y, Integer color);

	public void fillCircle(float radius, int x, int y, Integer color);
	public void fillCircle(float radius, float x, float y, Integer color);

	public void fillArc(float x, float y, float radius, float start, float degrees, Integer color);
	
	public void drawString(String text, int x, int y, Integer color);
	public void drawString(String text, float x, float y, Integer color);

	public void drawImage(TextureRegion image, int x, int y, Integer color);
	public void drawImage(TextureRegion image, float x, float y, Integer color);

	public void drawScaledImage(TextureRegion image, int x, int y, int width,
			int height, Integer color);
	public void drawScaledImage(TextureRegion image, float x, float y, int width,
			int height, Integer color);

	public void drawSubImage(TextureRegion image, int x, int y, int imageX,
			int imageY, int width, int height, Integer color);
	public void drawSubImage(TextureRegion image, float x, float y, int imageX,
			int imageY, int width, int height, Integer color);	

	public void drawRawSprite(Sprite sprite);
	public void drawSprite(Sprite sprite);
	public void drawSprite(Sprite sprite, int x, int y, Integer color);
	public void drawSprite(Sprite sprite, float x, float y, Integer color);
		
	public void rotate(double degree, Integer x, Integer y);
	public void rotate(double degree, float x, float y);

	public void translate(int x, int y);

	public void moveTo(double x, double y);

	public void scale(double sx, double sy);

	public void shear(double sx, double sy);

	public void clearTransform();
	
	public void pushZoom(double zoom);
	public void popZoom();

	public void setDefaultTransforms();
	public void setTransform(Matrix4 mat);
	public void setProjectionMatrix(Matrix4 mat);
	
	public void enableBlending();
	public void disableBlending();
	public void setBlendFunction(int srcFunc, int dstFunc);
	public int getSrcBlendFunction();
	public int getDstBlendFunction();
}