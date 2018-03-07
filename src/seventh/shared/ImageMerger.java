/*
 * see license.txt 
 */
package seventh.shared;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

import javax.imageio.ImageIO;

import seventh.client.gfx.ImageUtil;

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
    
    static class Settings {
        int overwriteX = 11;
        int overwriteY = 0;
        int overwriteWidth = 85;
        int overwriteHeight = 109;
        
        boolean scaleRow1 = true;
        float   scaleFactor = 0.1f;
        
        int rows = 2;
        int cols = 6;
    }

    static class DeathCSettings extends Settings {        
        public DeathCSettings() {
            overwriteX = 11;
            overwriteY = 0;
            overwriteWidth = 85;
            overwriteHeight = 109;
            
            scaleRow1 = true;
            scaleFactor = 0.1f;
            
            rows = 2;
            cols = 6;
        }
    }
    
    static class DeathDSettings extends Settings {
        public DeathDSettings() {
            overwriteX = 0;
            overwriteY = 38;
            overwriteWidth = 102;
            overwriteHeight = 131;
            
            scaleRow1 = true;
            scaleFactor = 0.1f;
            
            rows = 2;
            cols = 5;
        }
    }
    
    static Settings activeSettings = new DeathDSettings();
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String imageDir = "C:\\UnityProjects\\UnitTest\\Assets\\screencaptures"; 
                //"C:\\Users\\Tony\\Desktop\\SpriteSheetPacker\\Test\\allied_sprint";
        File imageFiles = new File(imageDir);
        File[] files = imageFiles.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File pathname) {
                //return pathname.getName().startsWith("legs");
                return pathname.getName().endsWith(".png");
            }
        });
        
        Arrays.sort(files, new Comparator<File>() {           
            @Override
            public int compare(File o1, File o2) {
                return (int) (o1.lastModified() - o2.lastModified());
            } 
        });
        
        final int overwriteX = activeSettings.overwriteX;
        final int overwriteY = activeSettings.overwriteY;
        final int overwriteWidth = activeSettings.overwriteWidth;
        final int overwriteHeight = activeSettings.overwriteHeight;
        
        boolean scaleRow1 = activeSettings.scaleRow1;
        float   scaleFactor = activeSettings.scaleFactor;
        
        int scaleX = (int)(overwriteWidth * scaleFactor);
        int scaleY = (int)(overwriteHeight * scaleFactor);
        int scaleWidth = overwriteWidth - (int)(overwriteWidth * scaleFactor);
        int scaleHeight = overwriteHeight - (int)(overwriteHeight * scaleFactor); 
        
        int rows = activeSettings.rows;
        int cols = activeSettings.cols;
        BufferedImage[] images = new BufferedImage[rows * cols];
        
        int j = 0;
        for(File f: files) {
            images[j] = ImageIO.read(f).getSubimage(overwriteX, overwriteY, overwriteWidth, overwriteHeight);
            j++;            
        }
        
        int width = overwriteWidth * cols;        
        width = nextPowerOf2(width);
        
        int height = overwriteHeight * rows;        
        height = nextPowerOf2(height);
        
        BufferedImage spritesheet = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g = (Graphics2D) spritesheet.getGraphics();
        
        int x = 0;
        int y = 0;
        
        for(int i = 0; i < images.length; i++) {
            if (x >= cols * overwriteWidth) {
                x = 0;
                y += overwriteHeight;////images[i].getHeight();                
            }
            

            if(i < cols && scaleRow1) {
               images[i] = ImageUtil.resizeImage(images[i], scaleWidth, scaleHeight);
               
               x += scaleX;
               y += scaleY;
               
               System.out.println(x + "," + y);
               g.drawImage(images[i], x, y, null);
               
               x -= scaleX;
               y -= scaleY;
            }
            else {                        
                System.out.println(x + "," + y);
                g.drawImage(images[i], x, y, null);
            }
            
            
            x += overwriteWidth;//images[i].getWidth();
                        
        }
        
       // spritesheet = ImageUtil.applyMask(spritesheet, new Color(0xA10082));
        
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
