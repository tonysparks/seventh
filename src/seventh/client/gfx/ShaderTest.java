/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.math.Vector2f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;

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
	
	/**
	 * 
	 */
	public ShaderTest() {		
		ShaderProgram.pedantic = false;
		
		shader = new ShaderProgram(Gdx.files.internal("./seventh/gfx/shaders/base.vert")
								 , Gdx.files.internal("./seventh/gfx/shaders/inprint.frag"));
		if(!shader.isCompiled()) {
			System.out.println("Not compiled!");
		}
		
		//Good idea to log any warnings if they exist
		if (shader.getLog().length()!=0) {
			System.out.println(shader.getLog());
		}
				
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		camera.setToOrtho(false);
		transform = new Matrix4();
		camera.update();
		batch = new SpriteBatch();//1024, shader);
		batch.setShader(shader);
		batch.setProjectionMatrix(camera.combined);
		batch.setTransformMatrix(transform);
		
		this.buffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);		
	}

	public void destroy() {
		this.shader.dispose();
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
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
//		texture.bind();
//		shader.begin();
//		shader.setUniformMatrix("u_worldView", matrix);
//		shader.setUniformi("u_texture", 0);
//		mesh.render(shader, GL10.GL_TRIANGLES);
//		shader.end();
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);		
		
//		buffer.begin();
//		{
//			batch.setProjectionMatrix(this.camera.combined);
//			batch.setShader(null);
//			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//			
//			batch.begin();
//			batch.draw(Art.tankTrackMarks, 150, 300);		
//			batch.end();
//		}
//		buffer.end();
		
		batch.setShader(null);
		
		this.camera.update();
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(this.camera.combined);
		batch.setTransformMatrix(transform);
		//batch.enableBlending();
//		batch.setBlendFunction(Gdx.gl10.GL_NEAREST, Gdx.gl10.GL_LINEAR);		
		//batch.setBlendFunction(Gdx.gl10.GL_SRC_ALPHA, Gdx.gl10.GL_ONE_MINUS_SRC_ALPHA);
		
		
		batch.begin();
		{
			
			Sprite tracks = new Sprite(Art.tankTrackMarks);
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
			
		}
		batch.end();
		batch.setShader(null);
//		batch.begin();		
//		batch.draw(buffer.getColorBufferTexture(), 0, 0);
//		batch.end();
	}

}
