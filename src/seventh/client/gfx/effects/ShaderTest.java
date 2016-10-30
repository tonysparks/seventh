/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;

import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
@SuppressWarnings("all")
public class ShaderTest implements Renderable {

	private ShaderProgram shader;
	private Mesh mesh;
	private Texture texture;
	private Matrix3 matrix;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Matrix4 transform;
	
	private FrameBuffer buffer;
	Vector2f position=new Vector2f(0.1f,0.23f);
	float time;
	
	private FrameBuffer fboPing, fboPong;
	private ShaderProgram vertBlur, horBlur, light;
	
	
	private ShaderProgram loadShader(String fragFile) {
		ShaderProgram shader = new ShaderProgram(Gdx.files.internal("./assets/gfx/shaders/base.vert")
				 							   , Gdx.files.internal("./assets/gfx/shaders/" + fragFile));
		if(!shader.isCompiled()) {
			System.out.println("Not compiled!");
		}
		
		//Good idea to log any warnings if they exist
		if (shader.getLog().length()!=0) {
			System.out.println(shader.getLog());
		}
		
		return shader;
	}
	
	/**
	 * 
	 */
	public ShaderTest() {		
		ShaderProgram.pedantic = false;
		
		vertBlur = loadShader("blurv.frag");
		horBlur = loadShader("blurh.frag");
		light = loadShader("light.frag");
		
		shader = loadShader("inprint.frag");
		
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		camera.setToOrtho(false);
		transform = new Matrix4();
		camera.update();
		batch = new SpriteBatch();//1024, shader);
		batch.setShader(null);
		batch.setProjectionMatrix(camera.combined);
		batch.setTransformMatrix(transform);
		
		this.buffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);	
		
		this.fboPing = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		this.fboPong = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	public void destroy() {
		this.shader.dispose();
		this.vertBlur.dispose();
		this.horBlur.dispose();
	}
	
	/**
	 * @return the shader
	 */
	public ShaderProgram getShader() {
		return shader;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		shader.begin();
		{
	
			time += timeStep.asFraction();
			if(time > 1.0f) {
				time = 0f;
			}
			
			//shader.setUniformi("u_texture", 0);
//			shader.setUniformf("center", position.x, position.y);
//			shader.setUniformf("time", time);
//			shader.setUniformf("shockParams", 10.0f, 0.8f, 0.1f);		
			
			
			//shader.setUniformi("u_texture", 0);
			shader.setUniformi("mark", 1);
			shader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		}
		shader.end();	
		
		light.begin();
		light.setUniformf("ambientColor", 0.8f, 0.8f, 0.51f, 0.4f);
		light.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		light.end();
		
		horBlur.begin();
		horBlur.setUniformf("blurRadius", 0.0315125f);
		horBlur.end();
		
		vertBlur.begin();
		vertBlur.setUniformf("blurRadius", 0.0181264f);
		vertBlur.end();
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		batch.setShader(null);
		
		this.camera.update();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(this.camera.combined);
		batch.setTransformMatrix(transform);
		//batch.enableBlending();
//		batch.setBlendFunction(Gdx.gl10.GL_NEAREST, Gdx.gl10.GL_LINEAR);		
		//batch.setBlendFunction(Gdx.gl10.GL_SRC_ALPHA, Gdx.gl10.GL_ONE_MINUS_SRC_ALPHA);
		
/*
		batch.begin();
		fboPing.begin();				
			batch.draw(Art.lightMap, -30, -20);
		fboPing.end();
		batch.end();
		
		batch.begin();
		batch.setShader(light);
		fboPing.getColorBufferTexture().bind(1);
		Art.lightMap.getTexture().bind(0);
		batch.end();
		*/
		batch.enableBlending();
        
		//batch.begin();
		{						
			fboPing.begin();
			batch.begin();
			    Gdx.gl.glClearColor(0, 0.5f, 0, 1);
			    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				batch.setShader(null);
				batch.draw(Art.alliedBodyModel.getFrame(0), 120, 200);
				
				batch.setShader(horBlur);
				batch.draw(Art.bulletImage, 200, 200);
			batch.end();
			fboPing.end();
			
			//batch.end();
			batch.flush();
	//		batch.disableBlending();
			batch.enableBlending();
			
			
			//batch.flush();
			//batch.enableBlending();
			//batch.setBlendFunction(Gdx.gl10.GL_NEAREST, Gdx.gl10.GL_LINEAR);
			//batch.setBlendFunction(Gdx.gl10.GL_SRC_ALPHA, Gdx.gl10.GL_ONE_MINUS_SRC_ALPHA);
			
			fboPong.begin();
			batch.begin();
    			//Gdx.gl.glClearColor(0,0.5f,0, 0);
                //Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                
                batch.enableBlending();
    			//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    			//Gdx.gl.glColorMask(true, true, true, true);
    //			fboPing.getColorBufferTexture().bind(1);
    			batch.setShader(null);
    			//batch.setColor(1, 1, 1, 1f);
    			batch.draw(fboPing.getColorBufferTexture(), 0, 0);
    			batch.draw(Art.bulletImage, 300, 200);
    			batch.setShader(vertBlur);
    			//batch.enableBlending();
    			batch.disableBlending();
    			//batch.draw(Art.bulletImage, 200, 200);
    			int y = fboPing.getHeight()-(200+Art.bulletImage.getRegionHeight());
    			batch.draw(fboPing.getColorBufferTexture(), 200, 300, 200, y, Art.bulletImage.getRegionWidth(), Art.bulletImage.getRegionHeight());
    
    			//batch.draw(fboPing.getColorBufferTexture(), 0, 0);
    			batch.setShader(null);
    			batch.end();
			fboPong.end();
			
			batch.flush();
			
		}
		

        //batch.setShader(null);
        //batch.draw(fboPong.getColorBufferTexture(), 0, 0);
		//batch.end();
		
		
		batch.setShader(null);
		batch.begin();		
		batch.draw(fboPong.getColorBufferTexture(), 0, 0);
		batch.end();
	}

	/*
	 * 	Sprite tracks = new Sprite(Art.tankTrackMarks);
			tracks.setColor(0.82f, 0.83f, 0.82f, 0.5f);
			
			//buffer.getColorBufferTexture().bind(1);
			
			//batch.setColor(1f, 1f, 1f, 1f);
			batch.draw(Art.tankTurret, 140,290);
			
			Art.tankTurret.getTexture().bind(0);
			Art.tankTrackMarks.getTexture().bind(1);
			batch.setShader(shader);
			batch.draw(Art.tankTrackMarks, 190, 310);
			batch.draw(Art.tankTrackMarks, 190, 310+Art.tankTrackMarks.getRegionHeight());
			//batch.draw(Art.bulletImage, 140, 290);
			//batch.setColor(0.2f, 0.3f, 0.2f, 0.5f);
			//batch.draw(tracks, 150, 300);
			//batch.draw(buffer.getColorBufferTexture(), 0, 0);
			batch.flush();
			
			//Art.tankTurret.getTexture().bind(1);
			Art.tankTrackMarks.getTexture().bind(0);
		
	 */
}
