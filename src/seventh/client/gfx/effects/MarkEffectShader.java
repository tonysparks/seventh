/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import seventh.client.gfx.Shader;

/**
 * @author Tony
 *
 */
public class MarkEffectShader extends Shader {

	private static final Shader INSTANCE = new MarkEffectShader();
	
	/**
	 */
	private MarkEffectShader() {
		super("./assets/gfx/shaders/base.vert", "./assets/gfx/shaders/inprint.frag");
	}
	
	
	/**
	 * @return
	 */
	public static Shader getInstance() {
		return INSTANCE;
	}

}
