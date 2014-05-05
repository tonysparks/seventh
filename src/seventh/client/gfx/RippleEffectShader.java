/*
 * see license.txt 
 */
package seventh.client.gfx;


/**
 * @author Tony
 *
 */
public class RippleEffectShader extends Shader {

	private static final Shader INSTANCE = new RippleEffectShader();
	
	/**
	 */
	private RippleEffectShader() {
		super("./seventh/gfx/shaders/base.vert", "./seventh/gfx/shaders/ripple.frag");				
	}
	
	
	/**
	 * @return
	 */
	public static Shader getInstance() {
		return INSTANCE;
	}

}
