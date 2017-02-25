/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import seventh.client.gfx.Shader;

/**
 * @author Tony
 *
 */
public class LightEffectShader extends Shader {

    private static final Shader INSTANCE = new LightEffectShader();
    
    /**
     */
    public LightEffectShader() {
        super("./assets/gfx/shaders/base.vert", "./assets/gfx/shaders/light.frag");                
    }
    
    
    
    /**
     * @return
     */
    public static Shader getInstance() {
        return INSTANCE;
    }

}
