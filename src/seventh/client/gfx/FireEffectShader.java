/*
 * see license.txt 
 */
package seventh.client.gfx;


/**
 * @author Tony
 *
 */
public class FireEffectShader extends Shader {

	private static final Shader INSTANCE = new FireEffectShader();
	
	/**
	 */
	private FireEffectShader() {
		super("./assets/gfx/shaders/base.vert", "./assets/gfx/shaders/fire.frag");
	}
	
	
	/**
	 * @return
	 */
	public static Shader getInstance() {
		return INSTANCE;
	}

}
