/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.math.Vector2f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	Vector2f position=new Vector2f(0.1f,0.23f);
	float time;
	
	/**
	 * 
	 */
	public ShaderTest() {		
		ShaderProgram.pedantic = false;
		
		shader = new ShaderProgram(Gdx.files.internal("./seventh/gfx/shaders/base.vert")
								 , Gdx.files.internal("./seventh/gfx/shaders/explosion.frag"));
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
		
		
		//bind the shader, then set the uniform, then unbind the shader
//		shader.begin();
//		shader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		shader.end();
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
			shader.setUniformf("center", position.x, position.y);
			shader.setUniformf("time", time);
			shader.setUniformf("shockParams", 10.0f, 0.8f, 0.1f);		

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
		
		this.camera.update();
		batch.setProjectionMatrix(this.camera.combined);
		batch.setTransformMatrix(transform);
//		batch.enableBlending();
//		batch.setBlendFunction(Gdx.gl10.GL_NEAREST, Gdx.gl10.GL_LINEAR);		
		
		batch.setShader(shader);
		batch.begin();
		
		
//		batch.setColor(1f, 1f, 1f, 1f);
//		batch.draw(Art.computerImage, 200, 400);
//		
//		batch.setColor(1f, 1.0f, 1.0f, 1f);
		batch.draw(Art.mp44Icon, 150, 300);
		batch.end();
	}

}
