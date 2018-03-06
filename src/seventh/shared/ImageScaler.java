/*
 * see license.txt 
 */
package seventh.shared;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * @author Tony
 *
 */
public class ImageScaler {

    /**
     * 
     */
    public ImageScaler() {
        // TODO Auto-generated constructor stub
    }

    static enum ScaleType {
        AXIS_POSITION,
        AXIS_LEGS,
        AXIS_DEATH1,
        AXIS_DEATH2,
        
        ALLIED_POSITION,
        ALLIED_LEGS,
        ALLIED_DEATH1,
        ALLIED_DEATH2,
        
        ;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        
        ScaleType type = ScaleType.AXIS_POSITION;
        
        
        File imageFile = null;
        File destFile = null;
        float scale = 1.0f;
        int xOffset = 0;
        int yOffset = 0;
        int width = 0;
        int height = 0;
        
        switch(type) {
            case AXIS_DEATH1: {
                imageFile = new File("C:\\Users\\Tony\\git\\seventh\\assets\\gfx\\player\\axis_death_01.png");
                scale = 0.60f;
                width = 512;
                height = 256;
                break;
            }
            case AXIS_DEATH2: {
                imageFile = new File("C:\\Users\\Tony\\git\\seventh\\assets\\gfx\\player\\axis_death_02.png");
                scale = 0.60f;
                width = 512;
                height = 256;
                break;
            }
            case AXIS_POSITION: {
                imageFile = new File("C:\\Users\\Tony\\git\\seventh\\assets\\gfx\\player\\axis_positions.png");
                scale = 0.77f; // TODO: 0.616f take ((77% * 256) * 80%) 
                xOffset = -5;
                yOffset = -5;
                width = 256;
                height = 256;
                break;
            }
            case AXIS_LEGS: {
                imageFile = new File("C:\\Users\\Tony\\Desktop\\SpriteSheetPacker\\Test\\axis_walk\\axis_legs_walk.png");
                destFile =  new File("C:\\Users\\Tony\\Desktop\\SpriteSheetPacker\\Test\\axis_walk\\axis_legs_walk_scaled.png");
                scale = 0.77f; 
                xOffset = 0;
                yOffset = 0;
                width = 512;
                height = 256;
                break;
            }
            case ALLIED_DEATH1: {
                imageFile = new File("C:\\Users\\Tony\\git\\seventh\\assets\\gfx\\player\\allied_death_01.png");
                scale = 0.60f;
                width = 512;
                height = 256;
                break;
            }
            case ALLIED_DEATH2: {
                imageFile = new File("C:\\Users\\Tony\\git\\seventh\\assets\\gfx\\player\\allied_death_02.png");
                scale = 0.60f;
                width = 512;
                height = 256;
                break;
            }
            case ALLIED_POSITION: {
                imageFile = new File("C:\\Users\\Tony\\git\\seventh\\assets\\gfx\\player\\allied_positions.png");
                scale = 0.77f;
                xOffset = -5;
                yOffset = -5;
                width = 256;
                height = 256;
                break;
            }
            case ALLIED_LEGS: {
                break;
            }    
        }
                
        BufferedImage image = ImageIO.read(imageFile);
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g = (Graphics2D) scaledImage.getGraphics();
                
        g.scale(scale, scale);
        g.drawImage(image, xOffset, yOffset, null);
        
        ImageIO.write(scaledImage, "png", destFile == null ? imageFile:destFile);        
    }

}
