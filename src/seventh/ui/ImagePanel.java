/*
 * see license.txt 
 */
package seventh.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class ImagePanel extends Panel {

    private TextureRegion image;
    
    public ImagePanel(TextureRegion image) {
        this.image = image;
    }
    
    /**
     * @return the image
     */
    public TextureRegion getImage() {
        return image;
    }
    
    /**
     * @param image the image to set
     */
    public void setImage(TextureRegion image) {
        this.image = image;
    }

}
