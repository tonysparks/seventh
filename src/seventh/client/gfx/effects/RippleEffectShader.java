/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import seventh.client.gfx.Shader;

/**
 * @author Tony
 *
 */
public class RippleEffectShader extends Shader {

    private static final Shader INSTANCE = new RippleEffectShader();
    
    /**
     */
    private RippleEffectShader() {
        super("./assets/gfx/shaders/base.vert", "./assets/gfx/shaders/ripple.frag");                
    }
    
    
    /**
     * @return
     */
    public static Shader getInstance() {
        return INSTANCE;
    }

}
