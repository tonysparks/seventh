/*
 * see license.txt 
 */
package seventh.ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.gfx.Art;
import seventh.client.gfx.TextureUtil;

/**
 * @author Tony
 *
 */
public class Skin {

    /**
     * 
     */
    public Skin() {
        TextureRegion buttonTex = Art.loadImage("./assets/gfx/ui/button_no_border.png");
        this.buttonBackground = TextureUtil.subImage(buttonTex, 162, 51, 148, 148);
        this.buttonOnClick = TextureUtil.subImage(buttonTex, 337, 56, 114, 114);
        
        TextureRegion buttonBorderTex = Art.loadImage("./assets/gfx/ui/button_border.png");
        this.settingsIcon = TextureUtil.subImage(buttonBorderTex, 12, 16, 68, 68);
        this.saveIcon = TextureUtil.subImage(buttonBorderTex, 83, 18, 63, 60);
        this.cancelIcon = TextureUtil.subImage(buttonBorderTex, 147, 10, 75, 77);
        this.reloadIcon = TextureUtil.subImage(buttonBorderTex, 12, 91, 65, 68);
        this.okIcon = TextureUtil.subImage(buttonBorderTex, 80, 94, 68, 58);
        
        this.buttonIconBackground = TextureUtil.subImage(buttonBorderTex, 229, 13, 128, 128);
        this.buttonIconOnClick = TextureUtil.subImage(buttonTex, 337, 56, 114, 114);
        
       // this.buttonIconBackgroundNinePatch = new NinePatch(TextureUtil.subImage(buttonBorderTex, 226, 11, 135, 134), 
        this.buttonIconBackgroundNinePatch = new NinePatch(this.buttonIconBackground,
                54,55,51,55);
        
        this.buttonIconOnClickNinePatch = new NinePatch(this.buttonIconOnClick,
                38,38,38,38);
    }
    
    public TextureRegion buttonBackground;
    public TextureRegion buttonOnClick;

    public TextureRegion buttonIconBackground;
    public TextureRegion buttonIconOnClick;
    
    public NinePatch buttonIconBackgroundNinePatch;
    public NinePatch buttonIconOnClickNinePatch;
    
    public TextureRegion buttonRedIconBackgroundNinePatch;
    public TextureRegion buttonRedIconOnClickNinePatch;
    
    public TextureRegion settingsIcon;
    public TextureRegion saveIcon;
    public TextureRegion cancelIcon;
    public TextureRegion reloadIcon;
    public TextureRegion okIcon;
    public TextureRegion downIcon;
    public TextureRegion leftIcon;
    public TextureRegion rightIcon;
    public TextureRegion upIcon;
    
    public TextureRegion closeIcon;
}
