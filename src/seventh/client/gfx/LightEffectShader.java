/*
 * see license.txt 
 */
package seventh.client.gfx;


/**
 * @author Tony
 *
 */
public class LightEffectShader extends Shader {

	private static final Shader INSTANCE = new LightEffectShader();
	
	/**
	 */
	public LightEffectShader() {
		super("./seventh/gfx/shaders/base.vert", "./seventh/gfx/shaders/light.frag");				
	}
	
	
	
	/**
	 * @return
	 */
	public static Shader getInstance() {
		return INSTANCE;
	}

}
