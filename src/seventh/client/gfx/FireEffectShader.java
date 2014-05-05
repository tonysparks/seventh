/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * @author Tony
 *
 */
public class FireEffectShader extends Shader {

	private static final Shader INSTANCE = new FireEffectShader();
	
	/**
	 */
	private FireEffectShader() {
		super("./seventh/gfx/shaders/base.vert", // "./seventh/gfx/shaders/blur.frag");
				"./seventh/gfx/shaders/fire.frag");
		
//		ShaderProgram shader = getShader();
//		shader.begin();
//		shader.setUniformi("u_texture", Art.fireWeaponLight.getTexture().getTextureObjectHandle());
//		shader.setUniformi("u_mask", Art.fireWeaponLight.getTexture().getTextureObjectHandle());
//		shader.end();
	}
	
	
	/**
	 * @return
	 */
	public static Shader getInstance() {
		return INSTANCE;
	}

}
