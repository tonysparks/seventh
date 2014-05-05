/*
 * see license.txt 
 */
package seventh.client.gfx;


/**
 * @author Tony
 *
 */
public class BlurEffectShader extends Shader {

	private static final Shader INSTANCE = new BlurEffectShader();
	
	/**
	 */
	private BlurEffectShader() {
		super("./seventh/gfx/shaders/base.vert", "./seventh/gfx/shaders/blur.frag");				
	}
	
	
	/**
	 * @return
	 */
	public static Shader getInstance() {
		return INSTANCE;
	}

}
