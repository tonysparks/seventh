/*
 * see license.txt 
 */
package seventh.client.gfx;


/**
 * @author Tony
 *
 */
public class ExplosionEffectShader extends Shader {

	private static final Shader INSTANCE = new ExplosionEffectShader();
	
	/**
	 */
	private ExplosionEffectShader() {
		super("./seventh/gfx/shaders/base.vert", "./seventh/gfx/shaders/explosion.frag");
	}
	
	
	/**
	 * @return
	 */
	public static Shader getInstance() {
		return INSTANCE;
	}

}
