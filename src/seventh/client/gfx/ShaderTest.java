/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.shared.TimeStep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
	/**
	 * 
	 */
	public ShaderTest() {
		String vertexShader = 
//				"attribute vec4 a_position;    \n" + 
//                "attribute vec4 a_color;\n" +
//                "attribute vec2 a_texCoord0;\n" + 
//                "uniform mat4 u_worldView;\n" + 
//                "varying vec4 v_color;" + 
//                "varying vec2 v_texCoords;" + 
//                "void main()                  \n" + 
//                "{                            \n" + 
//                "   v_color = vec4(1, 1, 1, 1); \n" + 
//                "   v_texCoords = a_texCoord0; \n" + 
//                "   gl_Position =  u_worldView * a_position;  \n"      +
//					"	vec4 a = gl_Vertex; \n"      +
////					"   a.x = a.x * 0.5; \n"      +
////					"   a.y = a.y * 0.5; \n"      +										
////					" gl_Position = gl_ModelViewProjectionMatrix * a; \n"      +
//
//                "}                            \n" ;
				
//				"//incoming Position attribute from our SpriteBatch\r\n" + 
//				"attribute vec2 a_position;\r\n" + 
//				"\r\n" + 
//				"//the transformation matrix of our SpriteBatch\r\n" + 
////				"uniform mat4 u_projView;\r\n" + 
//				"uniform mat4 u_projTrans;\n" + 
//				" \r\n" + 
//				"void main() {\r\n" + 
//				"	//transform our 2D screen space position into 3D world space\r\n" + 
//				"	gl_Position = u_projTrans * vec4(a_position, 0.0, 1.0);\r\n" + 
//				"}"
				
				"attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
				"attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
				"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
				
				"uniform mat4 u_projTrans;\n" + 
				" \n" + 
				"varying vec4 vColor;\n" +
				"varying vec2 vTexCoord;\n" +
				
				"void main() {\n" +  
				"	vColor = "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
				"	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
				"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
				"}"
				;
				
		String fragmentShader = 
//				  "#ifdef GL_ES\n" +
//                  "precision mediump float;\n" + 
//                  "#endif\n" + 
//                  "varying vec4 v_color;\n" + 
//                  "varying vec2 v_texCoords;\n" + 
//                  "uniform sampler2D u_texture;\n" + 
//                  "void main()                                  \n" + 
//                  "{                                            \n" + 
//                  "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
////					" gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0); \n" + 
//                  "}";
//				"void main() {\r\n" + 
//				"	//final color: return opaque red\r\n" + 
//				"	gl_FragColor = vec4(1.0, 0, 1.0, 1.0);\r\n" + 
//				"}"
				 
				//GL ES specific stuff
				  "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" + //
				"//texture 0\n" + 
				"uniform sampler2D u_texture;\n" + 
				"\n" + 
				"//our screen resolution, set from Java whenever the display is resized\n" + 
				"uniform vec2 resolution;\n" + 
				"\n" + 
				"//\"in\" attributes from our vertex shader\n" + 
				"varying LOWP vec4 vColor;\n" +
				"varying vec2 vTexCoord;\n" + 
				"\n" + 
				"//RADIUS of our vignette, where 0.5 results in a circle fitting the screen\n" + 
				"const float RADIUS = 0.75;\n" + 
				"\n" + 
				"//softness of our vignette, between 0.0 and 1.0\n" + 
				"const float SOFTNESS = 0.45;\n" + 
				"\n" + 
				"//sepia colour, adjust to taste\n" + 
				"const vec3 SEPIA = vec3(1.2, 1.0, 0.8); \n" + 
				"\n" + 
				"void main() {\n" + 
				"	//sample our texture\n" + 
				"	vec4 texColor = texture2D(u_texture, vTexCoord);\n" + 
				"		\n" + 
				"	//1. VIGNETTE\n" + 
				"	\n" + 
				"	//determine center position\n" + 
				"	vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);\n" + 
				"	\n" + 
				"	//determine the vector length of the center position\n" + 
				"	float len = length(position);\n" + 
				"	\n" + 
				"	//use smoothstep to create a smooth vignette\n" + 
				"	float vignette = smoothstep(RADIUS, RADIUS-SOFTNESS, len);\n" + 
				"	\n" + 
				"	//apply the vignette with 50% opacity\n" + 
				"	texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 0.5);\n" + 
				"		\n" + 
				"	//2. GRAYSCALE\n" + 
				"	\n" + 
				"	//convert to grayscale using NTSC conversion weights\n" + 
				"	float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));\n" + 
				"	\n" + 
				"	//3. SEPIA\n" + 
				"	\n" + 
				"	//create our sepia tone from some constant value\n" + 
				"	vec3 sepiaColor = vec3(gray) * SEPIA;\n" + 
				"		\n" + 
				"	//again we'll use mix so that the sepia effect is at 75%\n" + 
				"	texColor.rgb = mix(texColor.rgb, sepiaColor, 0.75);\n" + 
				"		\n" + 
				"	//final colour, multiplied by vertex colour\n" + 
				"	gl_FragColor = texColor * vColor;\n" + 
				"}";
				;
		
		ShaderProgram.pedantic = false;
		String str = ShaderProgram.POSITION_ATTRIBUTE;
		
//		shader = new ShaderProgram(vertexShader, fragmentShader);
		shader = new ShaderProgram(Gdx.files.internal("./seventh/gfx/shaders/base.vert")
								 , Gdx.files.internal("./seventh/gfx/shaders/test.frag"));
		if(!shader.isCompiled()) {
			System.out.println("Not compiled!");
		}
		
		//Good idea to log any warnings if they exist
		if (shader.getLog().length()!=0) {
			System.out.println(shader.getLog());
		}
		
		/*
		mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.  ColorUnpacked(), VertexAttribute.TexCoords(0));
		mesh.setVertices(new float[] 
		{-0.5f, -0.5f, 0, 1, 1, 1, 1, 0, 1,
		0.5f, -0.5f, 0, 1, 1, 1, 1, 1, 1,
		0.5f, 0.5f, 0, 1, 1, 1, 1, 1, 0,
		-0.5f, 0.5f, 0, 1, 1, 1, 1, 0, 0});
		mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});
		texture = Art.deathsImage.getTexture();
		matrix = new Matrix3();
		matrix = matrix.idt();*/
		
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
		shader.begin();
		shader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shader.end();
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
//		shader.begin();
//		shader.setUniformf("time", (float)timeStep.asFraction());
//		shader.end();
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
		batch.begin();
		
		batch.setColor(1f, 1f, 1f, 1f);
		batch.draw(Art.computerImage, 200, 400);
		
		batch.setColor(1f, 1.0f, 1.0f, 1f);
		batch.draw(Art.mp44Icon, 150, 100);
		batch.end();
	}

}
