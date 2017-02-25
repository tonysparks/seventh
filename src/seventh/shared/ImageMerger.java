/*
 * see license.txt 
 */
package seventh.shared;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;

import javax.imageio.ImageIO;

/**
 * @author Tony
 *
 */
public class ImageMerger {

    /**
     * 
     */
    public ImageMerger() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String imageDir = "C:\\Users\\Tony\\Desktop\\SpriteSheetPacker\\Test\\allied_sprint";
        File imageFiles = new File(imageDir);
        File[] files = imageFiles.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("legs");
            }
        });
        
        int rows = 2;
        int cols = 3;
        BufferedImage[] images = new BufferedImage[rows * cols];
        
        int j = 0;
        for(File f: files) {
            images[j] = ImageIO.read(f);
            j++;
        }
        
        int width = 0;
        for(int i = 0; i < cols; i++) {
            width += images[i].getWidth();
        }
        width = nextPowerOf2(width);
        
        int height = 0;
        for(int i = 0; i < rows; i++) {
            height += images[i].getHeight();
        }
        height = nextPowerOf2(height);
        
        BufferedImage spritesheet = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g = (Graphics2D) spritesheet.getGraphics();
        
        int x = 0;
        int y = 0;
        boolean newRow = false;
        for(int i = 0; i < images.length; i++) {
            System.out.println(x + "," + y);
            g.drawImage(images[i], x, y, null);
            if (i >= rows && !newRow) {
                x = 0;
                y += images[i].getHeight();
                newRow = true;
            }
            else {
                x += images[i].getWidth();
            }            
        }
        
        ImageIO.write(spritesheet, "png", new File(imageFiles, "/output.png"));
    }

    private static int nextPowerOf2(int n) {        
        int k = 1;
        while (k < n) {
            k *= 2;
        }
        return k;        
    }
}
