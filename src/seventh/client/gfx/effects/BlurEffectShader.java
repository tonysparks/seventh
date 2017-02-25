/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import seventh.client.gfx.Shader;

/**
 * @author Tony
 *
 */
public class BlurEffectShader extends Shader {

    private static final Shader INSTANCE = new BlurEffectShader();
    
    /**
     */
    private BlurEffectShader() {
        super("./assets/gfx/shaders/base.vert", "./assets/gfx/shaders/blur.frag");                
    }
    
    
    /**
     * @return
     */
    public static Shader getInstance() {
        return INSTANCE;
    }

}
