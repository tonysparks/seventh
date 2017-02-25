/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

/**
 * @author Tony
 *
 */
public class GdxCanvas implements Canvas {

    private SpriteBatch batch;
    private Color color, tmpColor;
    private float compositeAlpha;
    
    private float fontSize;
    private float defaultFontSize;
    private String currentFontName;
    private String defaultFontName;
    
    private Map<String, FreeTypeFontGenerator> generators;
    private Map<String, BitmapFont> fonts;
    private BitmapFont font, defaultFont;
    private GlyphLayout bounds;
    
    private Matrix4 transform;
    
    private ShapeRenderer shapes;
    private OrthographicCamera camera;
    
    private boolean isBegun;
    
    private Stack<ShaderProgram> shaderStack;
    
    private FrameBuffer fbo;
    
    /**
     * 
     */
    public GdxCanvas() {
        this.batch = new SpriteBatch();
        this.color = new Color();
        this.tmpColor = new Color();
        
        this.shaderStack = new Stack<>();
        
        this.camera = new OrthographicCamera(getWidth(), getHeight());
        this.camera.setToOrtho(true, getWidth(), getHeight());
                
        this.generators = new HashMap<String, FreeTypeFontGenerator>();
        this.fonts = new HashMap<String, BitmapFont>();
        this.bounds = new GlyphLayout();
                
        this.transform = new Matrix4();
        //this.batch.setTransformMatrix(transform);
                
        //Matrix4 projection = new Matrix4();
        //projection.setToOrtho( 0, getWidth(), getHeight(), 0, -1, 1);
        //this.batch.setProjectionMatrix(projection);
        
//        this.wHeight = getHeight();
        this.batch.setProjectionMatrix(this.camera.combined);
        
        this.shapes = new ShapeRenderer();
        //this.shapes.setTransformMatrix(transform);    
//        this.shapes.setProjectionMatrix(projection);
        //this.shapes.setProjectionMatrix(camera.combined);
        
        this.fbo = new FrameBuffer(Format.RGBA8888, getWidth(), getHeight(), false);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getCamera()
     */
    @Override
    public OrthographicCamera getCamera() {    
        return this.camera;
    }
    /*
     * (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setShader(com.badlogic.gdx.graphics.glutils.ShaderProgram)
     */
    @Override
    public void setShader(ShaderProgram shader) {
        this.batch.setShader(shader);        
    }
    
    /*
     * (non-Javadoc)
     * @see seventh.client.gfx.Canvas#pushShader(com.badlogic.gdx.graphics.glutils.ShaderProgram)
     */
    @Override
    public void pushShader(ShaderProgram shader) {
        this.shaderStack.add(shader);
        this.batch.setShader(shader);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#popShader()
     */
    @Override
    public void popShader() {
        if(!this.shaderStack.isEmpty()) {
            this.shaderStack.pop();
        }
        
        if(this.shaderStack.isEmpty()) {
            this.batch.setShader(null);
        }
        else {
            this.batch.setShader(this.shaderStack.peek());
        }
        
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#enableAntialiasing(boolean)
     */
    @Override
    public void enableAntialiasing(boolean b) {        
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#isAntialiasingEnabled()
     */
    @Override
    public boolean isAntialiasingEnabled() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#bindFrameBuffer(int)
     */
    @Override
    public void bindFrameBuffer(int id) {
        fbo.getColorBufferTexture().bind(id);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getFrameBuffer()
     */
    @Override
    public Texture getFrameBuffer() {    
        return fbo.getColorBufferTexture();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#fboBegin()
     */
    @Override
    public void fboBegin() {
        fbo.begin();        
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#fboEnd()
     */
    @Override
    public void fboEnd() {
        fbo.end();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#beginSprite()
     */
    @Override
    public void begin() {
        this.batch.begin();            
        this.isBegun = true;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#endSprite()
     */
    @Override
    public void end() {
        this.batch.end();    
        this.isBegun = false;
    }
    
    /*
     * (non-Javadoc)
     * @see seventh.client.gfx.Canvas#flush()
     */
    @Override
    public void flush() {
        this.batch.flush();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setProjectionMatrix(com.badlogic.gdx.math.Matrix4)
     */
    @Override
    public void setProjectionMatrix(Matrix4 mat) {
        this.batch.setProjectionMatrix(mat);
        this.shapes.setProjectionMatrix(mat);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setTransform(com.badlogic.gdx.math.Matrix4)
     */
    @Override
    public void setTransform(Matrix4 mat) {        
        this.batch.setTransformMatrix(mat);                
        this.shapes.setTransformMatrix(mat);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setTransform()
     */
    @Override
    public void setDefaultTransforms() {
        this.batch.setProjectionMatrix(this.camera.combined);
        this.batch.setTransformMatrix(this.transform);
        
        this.shapes.setProjectionMatrix(this.camera.combined);
        this.shapes.setTransformMatrix(this.transform);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#preRender()
     */
    @Override
    public void preRender() {
        this.camera.update();
        
        setDefaultTransforms();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#postRender()
     */
    @Override
    public void postRender() {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setColor(int, java.lang.Integer)
     */
    @Override
    public void setColor(int color, Integer alpha) {
        
        if(alpha != null) {
            //color = (color << 8) | alpha;
            color = (alpha << 24) | color;
        }
        _setColor(color);
    }
    
    private void _setColor(Integer color) {        
        setColor(this.color, color);
        
    }
    
    private void setColor(Color c, Integer color) {
        if(color != null) {
            int alpha = color >>> 24;
            color = (color << 8) | alpha;
            
            c.set(color);
        }
    }
    
    private Color setTempColor(Integer color) {
        if(color != null) {
            int alpha = color >>> 24;
            color = (color << 8) | alpha;
            
            tmpColor.set(color);
            return tmpColor;
        }
        return this.color;
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setClearColor(java.lang.Integer)
     */
    @Override
    public void setClearColor(Integer color) {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getColor()
     */
    @Override
    public int getColor() {
        int c = Color.rgba8888(color);
        return (c >>> 8) | (c<<24);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#loadFont(java.lang.String)
     */
    @Override
    public void loadFont(String filename, String alias) throws IOException {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(filename));        
        this.generators.put(alias, generator);        
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setFont(java.lang.String, int)
     */
    @Override
    public void setFont(String alias, int size) {
        this.font = getFont(alias, size);                        
        this.fontSize = size;
        this.currentFontName = alias;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setDefaultFont(java.lang.String, int)
     */
    @Override
    public void setDefaultFont(String alias, int size) {
        this.defaultFont = getFont(alias, size);        
        this.defaultFontSize = size;
        this.defaultFontName = alias;
    }
    
    /**
     * Retrieve the desired font and size (may load the font
     * if not cached).
     * 
     * @param alias
     * @param size
     * @return the font
     */
    private BitmapFont getFont(String alias, int size) {
        BitmapFont font = null;
        
        String mask = alias + ":" + size;
        if(this.fonts.containsKey(mask)) {
            font = this.fonts.get(mask);
        }
        else if(this.generators.containsKey(alias)) {
            FreeTypeFontParameter params = new FreeTypeFontParameter();
            params.size = size;
            params.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
            params.flip = true;
            
            font = this.generators.get(alias).generateFont(params);
            this.fonts.put(mask, font);
        }
        
        return font;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setDefaultFont()
     */
    @Override
    public void setDefaultFont() {
        this.font = defaultFont;
        this.fontSize = this.defaultFontSize;
        this.currentFontName = this.defaultFontName;
    }


    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getGlythData(java.lang.String, int)
     */
    @Override
    public GlythData getGlythData(String alias, int size) {
        BitmapFont font = getFont(alias, size);
        return new GlythData(font, bounds);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getGlythData()
     */
    @Override
    public GlythData getGlythData() {
        return new GlythData(font, bounds);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getWidth(java.lang.String)
     */
    @Override
    public int getWidth(String str) {
        return GlythData.getWidth(font, bounds, str);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getHeight(java.lang.String)
     */
    @Override
    public int getHeight(String str) {
        return GlythData.getHeight(font, bounds, str);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#boldFont()
     */
    @Override
    public void boldFont() {    
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#italicFont()
     */
    @Override
    public void italicFont() {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#plainFont()
     */
    @Override
    public void plainFont() {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#resizeFont(float)
     */
    @Override
    public void resizeFont(float size) {
        if(font!=null) {    
            /* get the scale increase */
//            if(this.fontSize != 0 && this.fontSize != size) {
//                float delta = size - this.fontSize;
//                float scale = delta / this.fontSize;
//                this.fontSize = size;
//                font.scale(1.0f + scale);
//            }
            if(this.fontSize != 0 && this.fontSize != size) {
                setFont(currentFontName, (int)size);
            }
            
        }
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setClip(int, int, int, int)
     */
    @Override
    public void setClip(int x, int y, int width, int height) {        
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setCompositeAlpha(float)
     */
    @Override
    public void setCompositeAlpha(float a) {
        this.compositeAlpha = a;
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getCompositeAlpha()
     */
    @Override
    public float getCompositeAlpha() {
        return this.compositeAlpha;
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getWidth()
     */
    @Override
    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getHeight()
     */
    @Override
    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#toRadians(double)
     */
    @Override
    public double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#toDegrees(double)
     */
    @Override
    public double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawLine(int, int, int, int, java.lang.Integer)
     */
    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapes.setColor(c);
        
        this.shapes.begin(ShapeType.Line);
        this.shapes.line(x1, y1, x2, y2);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawLine(float, float, float, float, java.lang.Integer)
     */
    @Override
    public void drawLine(float x1, float y1, float x2, float y2, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapes.setColor(c);
        
        this.shapes.begin(ShapeType.Line);
        this.shapes.line(x1, y1, x2, y2);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawRect(int, int, int, int, java.lang.Integer)
     */
    @Override
    public void drawRect(int x, int y, int width, int height, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapes.setColor(c);
        
        this.shapes.begin(ShapeType.Line);
        this.shapes.rect(x, y, width, height);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawRect(float, float, float, float, java.lang.Integer)
     */
    @Override
    public void drawRect(float x, float y, float width, float height, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapes.setColor(c);
        
        this.shapes.begin(ShapeType.Line);
        this.shapes.rect(x, y, width, height);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#fillRect(int, int, int, int, java.lang.Integer)
     */
    @Override
    public void fillRect(int x, int y, int width, int height, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);                
        
        this.shapes.begin(ShapeType.Filled);
        this.shapes.setColor(c);
        this.shapes.rect(x, y, width, height);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#fillRect(float, float, float, float, java.lang.Integer)
     */
    @Override
    public void fillRect(float x, float y, float width, float height, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);                
        
        this.shapes.begin(ShapeType.Filled);
        this.shapes.setColor(c);
        this.shapes.rect(x, y, width, height);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawCircle(float, int, int, java.lang.Integer)
     */
    @Override
    public void drawCircle(float radius, int x, int y, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapes.setColor(c);
        
        this.shapes.begin(ShapeType.Line);
        this.shapes.circle(x+radius, y+radius, radius);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawCircle(float, float, float, java.lang.Integer)
     */
    @Override
    public void drawCircle(float radius, float x, float y, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapes.setColor(c);
        
        this.shapes.begin(ShapeType.Line);
        this.shapes.circle(x+radius, y+radius, radius);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#fillCircle(float, int, int, java.lang.Integer)
     */
    @Override
    public void fillCircle(float radius, int x, int y, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapes.setColor(c);
        
        this.shapes.begin(ShapeType.Filled);
        this.shapes.circle(x+radius, y+radius, radius);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#fillCircle(float, float, float, java.lang.Integer)
     */
    @Override
    public void fillCircle(float radius, float x, float y, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapes.setColor(c);
        
        this.shapes.begin(ShapeType.Filled);
        this.shapes.circle(x+radius, y+radius, radius);
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    @Override
    public void fillArc(float x, float y, float radius, float start, float degrees, Integer color) {
        Color c=setTempColor(color);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.shapes.setColor(c);
        
        this.shapes.begin(ShapeType.Filled);
        this.shapes.arc(x, y, radius, start, degrees);        
        this.shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawString(java.lang.String, int, int, java.lang.Integer)
     */
    @Override
    public void drawString(String text, int x, int y, Integer color) {                
        if(font!=null) {
            Color c=setTempColor(color);
            
            if(!isBegun) batch.begin(); 
            font.setColor(c);                    
            font.draw(batch, text, x, y - font.getCapHeight());

            if(!isBegun) batch.end();
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawString(java.lang.String, float, float, java.lang.Integer)
     */
    @Override
    public void drawString(String text, float x, float y, Integer color) {
        if(font!=null) {
            Color c=setTempColor(color);
            
            if(!isBegun) batch.begin(); 
            font.setColor(c);                    
            font.draw(batch, text, x, y - font.getCapHeight());

            if(!isBegun) batch.end();
        }        
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawImage(com.badlogic.gdx.graphics.g2d.TextureRegion, int, int, java.lang.Integer)
     */
    @Override
    public void drawImage(TextureRegion image, int x, int y, Integer color) {
        
        Color c= Color.WHITE;
        if (color != null) c = setTempColor(color);
        
        if(!isBegun) batch.begin(); 
        batch.setColor(c);        
        batch.draw(image, x, y);                
        if(!isBegun) batch.end();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawImage(com.badlogic.gdx.graphics.g2d.TextureRegion, float, float, java.lang.Integer)
     */
    @Override
    public void drawImage(TextureRegion image, float x, float y, Integer color) {
        Color c= Color.WHITE;
        if (color != null) c = setTempColor(color);
        
        if(!isBegun) batch.begin(); 
        batch.setColor(c);        
        batch.draw(image, x, y);                
        if(!isBegun) batch.end();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawScaledImage(com.badlogic.gdx.graphics.g2d.TextureRegion, int, int, int, int, java.lang.Integer)
     */
    @Override
    public void drawScaledImage(TextureRegion image, int x, int y, int width, int height, Integer color) {
        Color c=setTempColor(color);
                        
        if(!isBegun) batch.begin();
        batch.setColor(c);
        batch.draw(image, x, y, width, height);
        if(!isBegun) batch.end();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawScaledImage(com.badlogic.gdx.graphics.g2d.TextureRegion, float, float, int, int, java.lang.Integer)
     */
    @Override
    public void drawScaledImage(TextureRegion image, float x, float y, int width, int height, Integer color) {
        Color c=setTempColor(color);
        
        if(!isBegun) batch.begin();
        batch.setColor(c);
        batch.draw(image, x, y, width, height);
        if(!isBegun) batch.end();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawSubImage(com.badlogic.gdx.graphics.g2d.TextureRegion, int, int, int, int, int, int, java.lang.Integer)
     */
    @Override
    public void drawSubImage(TextureRegion image, int x, int y, int imageX, int imageY, int width, int height, Integer color) {
        throw new IllegalStateException("Method not supported!");
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawSubImage(com.badlogic.gdx.graphics.g2d.TextureRegion, float, float, int, int, int, int, java.lang.Integer)
     */
    @Override
    public void drawSubImage(TextureRegion image, float x, float y, int imageX, int imageY, int width, int height, Integer color) {
        throw new IllegalStateException("Method not supported!");
    }

    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawRawSprite(com.badlogic.gdx.graphics.g2d.Sprite)
     */
    @Override
    public void drawRawSprite(Sprite sprite) {
        if(!isBegun) batch.begin();            
        sprite.draw(batch);        
        if(!isBegun) batch.end();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawSprite(com.badlogic.gdx.graphics.g2d.Sprite)
     */
    @Override
    public void drawSprite(Sprite sprite) {
        if(!isBegun) batch.begin();        
        sprite.setColor(1,1,1, this.compositeAlpha);
        sprite.draw(batch);        
        if(!isBegun) batch.end();
    }
    
    @Override
    public void drawSprite(Sprite sprite, int x, int y, Integer color) {
        if(!isBegun) batch.begin();        
        if(color!=null) {
            Color c = setTempColor(color);
            sprite.setColor(c);            
        } else sprite.setColor(1,1,1, this.compositeAlpha);
        sprite.setPosition(x, y);
        sprite.draw(batch);        
        if(!isBegun) batch.end();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#drawSprite(com.badlogic.gdx.graphics.g2d.Sprite, float, float, java.lang.Integer)
     */
    @Override
    public void drawSprite(Sprite sprite, float x, float y, Integer color) {
        if(!isBegun) batch.begin();        
        if(color!=null) {
            Color c = setTempColor(color);
            sprite.setColor(c);            
        } else sprite.setColor(1,1,1, this.compositeAlpha);
        sprite.setPosition(x, y);
        sprite.draw(batch);        
        if(!isBegun) batch.end();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#rotate(double, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public void rotate(double degree, Integer x, Integer y) {
        //this.transform.rotate( x, y, 0, (float)degree);
        this.shapes.rotate( (float)x, (float)y, 0.0f, (float)degree);
//        camera.rotate( (float)Math.toRadians(degree), x, y, 0);
//        camera.rotate( (float)degree, x, y, 0);
        //batch.getTransformMatrix().rotate( x, y, 0, (float)Math.toRadians(degree));
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#rotate(double, float, float)
     */
    @Override
    public void rotate(double degree, float x, float y) {
        this.shapes.rotate( x, y, 0.0f, (float)degree);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#translate(int, int)
     */
    @Override
    public void translate(int x, int y) {
        this.transform.translate(x, y, 0);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#moveTo(double, double)
     */
    @Override
    public void moveTo(double x, double y) {
        this.transform.translate( (float)x, (float)y, 0);
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#scale(double, double)
     */
    @Override
    public void scale(double sx, double sy) {
        this.transform.scale( (float)sx, (float)sy, 1.0f);
        
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#shear(double, double)
     */
    @Override
    public void shear(double sx, double sy) {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#clearTransform()
     */
    @Override
    public void clearTransform() {
        //this.transform.idt();    
        this.shapes.identity();
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#pushZoom(double)
     */
    @Override
    public void pushZoom(double zoom) {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#popZoom()
     */
    @Override
    public void popZoom() {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#enableBlending()
     */
    @Override
    public void enableBlending() {
        this.batch.enableBlending();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#disableBlending()
     */
    @Override
    public void disableBlending() {
        this.batch.disableBlending();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#setBlendFunction(int, int)
     */
    @Override
    public void setBlendFunction(int srcFunc, int dstFunc) {
        this.batch.setBlendFunction(srcFunc, dstFunc);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getSrcBlendFunction()
     */
    @Override
    public int getSrcBlendFunction() {
        return this.batch.getBlendSrcFunc();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Canvas#getDstBlendFunction()
     */
    @Override
    public int getDstBlendFunction() {
        return this.batch.getBlendDstFunc();
    }
}
