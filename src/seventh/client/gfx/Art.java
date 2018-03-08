/*
 * see license.txt 
 */
package seventh.client.gfx;

import java.lang.reflect.Field;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.shared.Cons;

/**
 * Art assets
 * 
 * 
 * @author Tony
 *
 */
public class Art {
    private static final Random random = new Random();
    
    /**
     * Simple black image used to overlay
     */
    public static final TextureRegion BLACK_IMAGE = new TextureRegion();
    
    
    /*----------------------------------------------------------------
     * Art for the Game
     *----------------------------------------------------------------*/
     
    
    public static  TextureRegion shotgunImage = null;
    public static  TextureRegion rpgImage = null;            

    public static  Sprite fragGrenadeIcon = null;
    public static  TextureRegion fragGrenadeImage = null;
    
    public static  Sprite smokeGrenadeIcon = null;
    public static  TextureRegion smokeGrenadeImage = null;
    
    public static  TextureRegion springfieldImage = null;
    public static  TextureRegion thompsonImage = null;    
    public static  TextureRegion m1GarandImage = null;
    public static  TextureRegion kar98Image = null;
    public static  TextureRegion mp44Image = null;
    public static  TextureRegion mp40Image = null;
    public static  TextureRegion pistolImage = null;
    public static  TextureRegion riskerImage = null;
    public static  TextureRegion flameThrowerImage = null;
    
    public static  TextureRegion bombImage = null;    
    public static  TextureRegion radioImage = null;
    
    public static  Model alliedBodyModel = null;        
    public static  Model alliedWalkModel = null;
    public static  Model alliedSprintModel = null;
    public static  TextureRegion alliedCrouchLegs = null;
    
    public static  Model axisBodyModel = null;
    public static  Model axisWalkModel = null;
    public static  Model axisSprintModel = null;
    public static  TextureRegion axisCrouchLegs = null;    
    
    public static  TextureRegion alliedIcon = null;
    public static  TextureRegion axisIcon = null;
    
    public static  TextureRegion shotgunIcon = null;
    public static  TextureRegion rocketIcon = null;    
    public static  TextureRegion springfieldIcon = null;    
    public static  TextureRegion thompsonIcon = null;
    public static  TextureRegion m1GarandIcon = null;
    public static  TextureRegion kar98Icon = null;
    public static  TextureRegion mp44Icon = null;
    public static  TextureRegion mp40Icon = null;
    public static  TextureRegion pistolIcon = null;
    public static  TextureRegion riskerIcon = null;
    public static  TextureRegion flameThrowerIcon = null;

    public static  TextureRegion tdmIcon = null;
    public static  TextureRegion ctfIcon = null;
    public static  TextureRegion objIcon = null;
    
    public static  TextureRegion attackerIcon = null;
    public static  TextureRegion defenderIcon = null;
    
    private static TextureRegion[] explosionImage = null;
    private static TextureRegion[] rocketImage = null;
        
    public static  TextureRegion deathsImage = null;
    
    private static TextureRegion[] alliedBackDeathImage = null;        
    private static TextureRegion[] alliedBackDeath2Image = null;
    private static TextureRegion[] alliedFrontDeathImage = null;
    private static TextureRegion[] alliedFrontDeath2Image = null;
    //private static TextureRegion[] alliedExplosionDeathImage = null;
    
    private static TextureRegion[] axisBackDeathImage = null;
    private static TextureRegion[] axisBackDeath2Image = null;
    private static TextureRegion[] axisFrontDeathImage = null;
    private static TextureRegion[] axisFrontDeath2Image = null;
    //private static TextureRegion[] axisExplosionDeathImage = null;
    
    public static  TextureRegion[] bloodImages = null;
    public static  TextureRegion[] gibImages = null;
        
    public static  TextureRegion smokeImage = null;
    
//    public static  TextureRegion tankImage = loadImage("./assets/gfx/tank.png");
    
    public static  Sprite smallAssaultRifleIcon = null;
    public static  Sprite smallShotgunIcon = null;
    public static  Sprite smallRocketIcon = null;
    public static  Sprite smallSniperRifleIcon = null;
    public static  Sprite smallM1GarandIcon = null;
    public static  Sprite smallFragGrenadeIcon = null;
    public static  Sprite smallSmokeGrenadeIcon = null;
    public static  Sprite smallExplosionIcon = null;
    public static  Sprite smallkar98Icon = null;
    public static  Sprite smallmp44Icon = null;
    public static  Sprite smallmp40Icon = null;
    public static  Sprite smallPistolIcon = null;
    public static  Sprite smallRiskerIcon = null;
    public static  Sprite smallFlameThrowerIcon = null;
    
    public static  TextureRegion cursorImg = null;
    public static  TextureRegion reticleImg = null;
    
    public static  TextureRegion[] thompsonMuzzleFlash = null;
    public static  TextureRegion[] m1GarandMuzzleFlash = null;
    public static  TextureRegion[] springfieldMuzzleFlash = null;
    public static  TextureRegion[] kar98MuzzleFlash = null;
    public static  TextureRegion[] mp44MuzzleFlash = null;
    public static  TextureRegion[] mp40MuzzleFlash = null;
    public static  TextureRegion[] shotgunMuzzleFlash = null;
    public static  TextureRegion[] rocketMuzzleFlash = null;
    public static  TextureRegion[] riskerMuzzleFlash = null;
    
    public static  Sprite healthPack = null;
    public static  Sprite healthIcon = null;
    public static  Sprite staminaIcon = null;
    public static  Sprite upArrow = null;
    public static  Sprite downArrow = null;
    
    public static  TextureRegion fireWeaponLight = null;
    public static  TextureRegion lightMap = null;
    public static  TextureRegion flashLight = null;
            
    public static  TextureRegion bulletShell = null;
    
    public static  TextureRegion tankTrackMarks = null;
    
    public static  TextureRegion shermanTankImage = null;
    public static  TextureRegion shermanTankBase = null;
    public static  TextureRegion shermanTankTurret = null;
    public static  TextureRegion shermanTankBaseDamaged = null;
    public static  TextureRegion shermanTankTurretDamaged = null;
    
    public static  TextureRegion panzerTankImage = null;
    public static  TextureRegion panzerTankBase = null;
    public static  TextureRegion panzerTankTurret = null;
    public static  TextureRegion panzerTankBaseDamaged = null;
    public static  TextureRegion panzerTankTurretDamaged = null;
    
    public static  TextureRegion alliedFlagImg = null;
    public static  TextureRegion axisFlagImg = null;
    
    public static  TextureRegion doorImg = null;
    
    public static  TextureRegion firstBloodIcon = null;
    public static  TextureRegion killRollIcon = null;
    
    /**
     * Reloads the graphics
     */
    public static void reload() {
       // destroy();
        load();
    }
    
    
    /**
     * Loads the graphics
     */
    public static void load() {        
        {
            Pixmap map = TextureUtil.createPixmap(128, 128);
            map.setColor(Color.BLACK);
            map.fillRectangle(0, 0, map.getWidth(), map.getHeight());
            BLACK_IMAGE.setTexture(new Texture(map));
        }
        

        shotgunImage = loadImage("./assets/gfx/weapons/m3.bmp", 0xff00ff);
        rpgImage = loadImage("./assets/gfx/weapons/rpg.bmp", 0xff00ff);

//        Pixmap grenadePixmap = loadPixmap("./assets/gfx/weapons/grenade.png");
//        grenadeImage = TextureUtil.tex(TextureUtil.resizePixmap(grenadePixmap, 12, 12));
//        grenadeIcon = TextureUtil.tex(grenadePixmap);

        fragGrenadeImage = loadImage("./assets/gfx/weapons/frag_grenade.png");
        fragGrenadeIcon = TextureUtil.resizeImage(fragGrenadeImage, 12, 12);
        
        smokeGrenadeImage = loadImage("./assets/gfx/weapons/smoke_grenade.png");
        smokeGrenadeIcon = TextureUtil.resizeImage(smokeGrenadeImage, 12, 12);
        
        springfieldImage = loadImage("./assets/gfx/weapons/springfield.bmp", 0xff00ff);
        thompsonImage = loadImage("./assets/gfx/weapons/thompson.bmp", 0xff00ff);
        m1GarandImage = loadImage("./assets/gfx/weapons/m1garand.bmp", 0xff00ff);
        kar98Image = loadImage("./assets/gfx/weapons/kar98.bmp", 0xff00ff);
        mp44Image = loadImage("./assets/gfx/weapons/mp44.bmp", 0xff00ff);
        mp40Image = loadImage("./assets/gfx/weapons/mp40.bmp", 0xff00ff);
        pistolImage = loadImage("./assets/gfx/weapons/pistol.bmp", 0xff00ff);
        riskerImage = loadImage("./assets/gfx/weapons/risker.bmp", 0xff00ff);
        flameThrowerImage = loadImage("./assets/gfx/weapons/flame_thrower.bmp", 0xff00ff); 
        
        bombImage = loadImage("./assets/gfx/weapons/bomb.bmp", 0xff00ff);
        bombImage.flip(false, true);        
        radioImage = loadImage("./assets/gfx/entities/radio.png");

        alliedBodyModel = new Model(loadImage("./assets/gfx/player/allied_positions.png"), 201, 256, 3, 3);    
        alliedWalkModel = new Model(loadImage("./assets/gfx/player/allied_legs_walk.png"), 372, 196, 2, 4);
        alliedSprintModel = new Model(loadImage("./assets/gfx/player/allied_legs_sprint.png"), 256, 190, 2, 3);
        alliedCrouchLegs = loadImage("./assets/gfx/player/allied_crouch_legs.png");
                        
        axisBodyModel = new Model(loadImage("./assets/gfx/player/axis_positions.png"), 201, 256, 3, 3);
        axisWalkModel = new Model(loadImage("./assets/gfx/player/axis_legs_walk.png"), 372, 196, 2, 4);
        //axisSprintModel = new Model(loadImage("./assets/gfx/player/axis_legs_sprint.png"), 256, 190, 2, 3);
        axisSprintModel = new Model(loadImage("./assets/gfx/player/axis_legs_sprint.png"), 160, 140, 2, 3);
        axisCrouchLegs = loadImage("./assets/gfx/player/axis_crouch_legs.png");
        
        
        shotgunIcon = loadImage("./assets/gfx/weapons/shotgun_icon.png");
        rocketIcon = loadImage("./assets/gfx/weapons/rpg_icon.png");
        springfieldIcon = loadImage("./assets/gfx/weapons/springfield_icon.png");
        thompsonIcon = loadImage("./assets/gfx/weapons/thompson_icon.png");
        m1GarandIcon = loadImage("./assets/gfx/weapons/m1garand_icon.png");
        kar98Icon = loadImage("./assets/gfx/weapons/kar98_icon.png");
        mp44Icon = loadImage("./assets/gfx/weapons/mp44_icon.png");
        mp40Icon = loadImage("./assets/gfx/weapons/mp40_icon.png");
        pistolIcon = loadImage("./assets/gfx/weapons/pistol_icon.png");
        riskerIcon = loadImage("./assets/gfx/weapons/risker_icon.png");
        flameThrowerIcon = loadImage("./assets/gfx/weapons/flame_thrower_icon.png"); 
        
        explosionImage = TextureUtil.splitImage(loadImage("./assets/gfx/particles/explosion.png"), 4, 4);
        rocketImage = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/rocket.bmp", 0xff00ff), 1, 1);

        deathsImage = loadImage("./assets/gfx/player/death_symbol.png");

//        alliedBackDeathImage = TextureUtil.splitImage(
//                TextureUtil.subImage(loadImage("./assets/gfx/player/allied_death_01.png"), 0, 0, 300, 210), 2, 4);
//        alliedBackDeath2Image = TextureUtil.splitImage(
//                TextureUtil.subImage(loadImage("./assets/gfx/player/allied_death_03.png"), 0, 0, 260, 290), 2, 3);
//        alliedFrontDeathImage = TextureUtil.splitImage(
//                TextureUtil.subImage(loadImage("./assets/gfx/player/allied_death_02.png"), 0, 0, 330, 190), 2, 6);        
//        alliedExplosionDeathImage = TextureUtil.splitImage(
//                TextureUtil.subImage(loadImage("./assets/gfx/player/allied_death_04.png"), 0, 0, 330, 290), 2, 4);
        
        alliedBackDeathImage = TextureUtil.splitImage(
                TextureUtil.subImage(loadImage("./assets/gfx/player/allied_death_01.png"), 0, 0, 450, 200), 2, 6);
        alliedBackDeath2Image = TextureUtil.splitImage(
                TextureUtil.subImage(loadImage("./assets/gfx/player/allied_death_02.png"), 0, 0, 420, 175), 2, 6);
        alliedFrontDeathImage = TextureUtil.splitImage(
                TextureUtil.subImage(loadImage("./assets/gfx/player/allied_death_03.png"), 0, 0, 510, 218), 2, 6);
        alliedFrontDeath2Image = TextureUtil.splitImage(
                TextureUtil.subImage(loadImage("./assets/gfx/player/allied_death_04.png"), 0, 0, 510, 262), 2, 5);
        
//        axisBackDeathImage = TextureUtil.splitImage(
//                TextureUtil.subImage(loadImage("./assets/gfx/player/axis_death_01.png"), 0, 0, 300, 210), 2, 4);        
//        axisBackDeath2Image = TextureUtil.splitImage(
//                TextureUtil.subImage(loadImage("./assets/gfx/player/axis_death_03.png"), 0, 0, 260, 290), 2, 3);        
//        axisFrontDeathImage = TextureUtil.splitImage(
//                TextureUtil.subImage(loadImage("./assets/gfx/player/axis_death_02.png"), 0, 0, 330, 190), 2, 6);
//        axisExplosionDeathImage = TextureUtil.splitImage(
//                TextureUtil.subImage(loadImage("./assets/gfx/player/axis_death_04.png"), 0, 0, 330, 290), 2, 4);
        
        axisBackDeathImage = TextureUtil.splitImage(
                TextureUtil.subImage(loadImage("./assets/gfx/player/axis_death_01.png"), 0, 0, 450, 200), 2, 6);
        axisBackDeath2Image = TextureUtil.splitImage(
                TextureUtil.subImage(loadImage("./assets/gfx/player/axis_death_02.png"), 0, 0, 420, 175), 2, 6);
        axisFrontDeathImage = TextureUtil.splitImage(
                TextureUtil.subImage(loadImage("./assets/gfx/player/axis_death_03.png"), 0, 0, 510, 218), 2, 6);
        axisFrontDeath2Image = TextureUtil.splitImage(
                TextureUtil.subImage(loadImage("./assets/gfx/player/axis_death_04.png"), 0, 0, 510, 262), 2, 5);
//        bloodImages = TextureUtil.splitImage(                
//                TextureUtil.subImage(loadImage("./assets/gfx/particles/blood.png"), 0, 0, 128, 32), 1, 4);
        bloodImages = TextureUtil.splitImage(loadImage("./assets/gfx/particles/blood.png"), 2, 3);
        gibImages = TextureUtil.splitImage(loadImage("./assets/gfx/particles/gibs.png"), 2, 2);

        smokeImage = loadImage("./assets/gfx/particles/smoke.png");

        // tankImage = loadImage("./assets/gfx/tank.png");

        final int smallIconWidth = 64, smallIconHeight = 32;
        smallAssaultRifleIcon = TextureUtil.resizeImage(thompsonIcon, smallIconWidth, smallIconHeight);
        smallShotgunIcon = TextureUtil.resizeImage(shotgunIcon, smallIconWidth, smallIconHeight);
        smallRocketIcon = TextureUtil.resizeImage(rocketIcon, smallIconWidth, smallIconHeight);
        smallSniperRifleIcon = TextureUtil.resizeImage(springfieldIcon, smallIconWidth, smallIconHeight);
        smallM1GarandIcon = TextureUtil.resizeImage(m1GarandIcon, smallIconWidth, smallIconHeight);
        smallFragGrenadeIcon = TextureUtil.resizeImage(fragGrenadeImage, smallIconWidth / 3, smallIconHeight / 3);
        smallSmokeGrenadeIcon = TextureUtil.resizeImage(fragGrenadeImage, smallIconWidth / 3, smallIconHeight / 3);
        smallExplosionIcon = TextureUtil.resizeImage(explosionImage[0], smallIconWidth / 2, smallIconHeight / 2);
        smallkar98Icon = TextureUtil.resizeImage(kar98Icon, smallIconWidth, smallIconHeight);
        smallmp44Icon = TextureUtil.resizeImage(mp44Icon, smallIconWidth, smallIconHeight);
        smallmp40Icon = TextureUtil.resizeImage(mp40Icon, smallIconWidth, smallIconHeight);
        smallPistolIcon = TextureUtil.resizeImage(pistolIcon, smallIconWidth, smallIconHeight);
        smallRiskerIcon = TextureUtil.resizeImage(riskerIcon, smallIconWidth, smallIconHeight);
        smallFlameThrowerIcon = TextureUtil.resizeImage(flameThrowerIcon, smallIconWidth, smallIconHeight);

        cursorImg = loadImage("./assets/gfx/ui/menu_cursor.png");
        reticleImg = loadImage("./assets/gfx/ui/reticle.png");
                        
        thompsonMuzzleFlash = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/thompson_muzzle_flash.png"), 2, 2);
        springfieldMuzzleFlash = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/springfield_muzzle_flash.png"), 2, 2);
        m1GarandMuzzleFlash = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/m1garand_muzzle_flash.png"), 2, 2);
        kar98MuzzleFlash = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/kar98_muzzle_flash.png"), 2, 2);
        mp40MuzzleFlash = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/mp40_muzzle_flash.png"), 2, 2);
        mp44MuzzleFlash = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/mp44_muzzle_flash.png"), 2, 2);
        shotgunMuzzleFlash = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/shotgun_muzzle_flash.png"), 2, 2);
        rocketMuzzleFlash = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/rpg_muzzle_flash.png"), 2, 2);
        riskerMuzzleFlash = TextureUtil.splitImage(loadImage("./assets/gfx/weapons/risker_muzzle_flash.png"), 2, 2);
        
        healthPack = TextureUtil.resizeImage(loadImage("./assets/gfx/entities/healthpack.png"), 16, 16);
        healthIcon = healthPack; 
                //TextureUtil.resizeImage(loadImage("./assets/gfx/ui/health.bmp"), 12, 12);
        staminaIcon = TextureUtil.resizeImage(loadImage("./assets/gfx/ui/stamina.png"), 16, 16);
        
        TextureRegion navArrow = loadImage("./assets/gfx/ui/ui_nav_arrows.png");
        upArrow = new Sprite(navArrow);
        upArrow.flip(false, true);
        downArrow = new Sprite(navArrow);
        
        fireWeaponLight = loadImage("./assets/gfx/weapon_fire.png");
        lightMap = loadImage("./assets/gfx/entities/light.png");
        flashLight = loadImage("./assets/gfx/lightmap_flashlight.png");
        
        bulletShell = loadImage("./assets/gfx/particles/bullet_shell.png");
                
        tankTrackMarks = loadImage("./assets/gfx/vehicles/tank_track_mark.png");
        
        shermanTankImage = loadImage("./assets/gfx/vehicles/tanks/sherman/sherman_tank.png");
        shermanTankBase = TextureUtil.subImage(shermanTankImage, 15, 180, 272, 149);
        shermanTankTurret = TextureUtil.subImage(shermanTankImage, 304, 187, 197, 108);
        shermanTankBaseDamaged = TextureUtil.subImage(shermanTankImage, 15, 8, 272, 155);
        shermanTankTurretDamaged = TextureUtil.subImage(shermanTankImage, 304, 22, 187, 108);
        
        panzerTankImage = loadImage("./assets/gfx/vehicles/tanks/panzer/panzer_tank.png");
        panzerTankBase = TextureUtil.subImage(panzerTankImage, 0, 310, 257, 195);
        panzerTankTurret = TextureUtil.subImage(panzerTankImage, 35, 20, 273, 125);
        panzerTankBaseDamaged = TextureUtil.subImage(panzerTankImage, 254, 310, 265, 195);        
        panzerTankTurretDamaged = TextureUtil.subImage(panzerTankImage, 35, 168, 273, 125);
        
        alliedFlagImg = loadImage("./assets/gfx/entities/allied_flag.png");
        axisFlagImg = loadImage("./assets/gfx/entities/axis_flag.png");
        
        alliedIcon = loadImage("./assets/gfx/ui/allied_icon.png");
        axisIcon = loadImage("./assets/gfx/ui/axis_icon.png");
        
        tdmIcon = loadImage("./assets/gfx/ui/tdm_icon.png");
        ctfIcon = loadImage("./assets/gfx/ui/ctf_icon.png");
        objIcon = loadImage("./assets/gfx/ui/obj_icon.png");
        
        attackerIcon = loadImage("./assets/gfx/ui/attacker_icon.png");
        defenderIcon = loadImage("./assets/gfx/ui/defender_icon.png");
        
        
        doorImg = loadImage("./assets/gfx/entities/door.png");
        
        firstBloodIcon = loadImage("./assets/gfx/ui/first_blood_icon.png");
        killRollIcon = loadImage("./assets/gfx/ui/kill_roll_icon.png");
    }

    
    /**
     * Releases all the textures
     */
    public static void destroy() {
        try {
            
            /*
             * Iterate through all of the static fields,
             * free the Sprite's, TextureRegion's, and Model's
             */            
            Field[] fields = Art.class.getFields();
            for(Field field : fields) {
                field.setAccessible(true);
                
                Class<?> type = field.getType();
                Object value = field.get(null);
                
                if(value != null) {
                    if(type.equals(Sprite.class)) {
                        free( (Sprite)value );
                    }
                    else if(type.equals(TextureRegion.class)) {
                        free( (TextureRegion)value );
                    }
                    else if(type.equals(TextureRegion[].class)) {
                        free( (TextureRegion[])value );
                    }
                    else if(type.equals(Model.class)) {
                        free( (Model)value );
                    }
                }
            }
        }
        catch(Exception e) {
            Cons.println("Problem freeing the textures: " + e);
        }
    }
    
    
    /**
     * Frees the texture
     * 
     * @param region
     */
    public static void free(TextureRegion region) {
        if(region != null) {
            region.getTexture().dispose();
        }
    }
    
    
    /**
     * Frees the textures
     * 
     * @param region
     */
    public static void free(TextureRegion[] region) {
        if(region != null) {
            for(TextureRegion r : region)
                free(r);
        }
    }
    
    
    /**
     * Frees the memory associated with the model
     * 
     * @param model
     */
    public static void free(Model model) {
        if(model!=null) {
            model.destroy();
        }
    }
    
    
    
    /**
     * Loads an image from the file system
     * 
     * @param image
     * @return the texture
     */
    public static TextureRegion loadImage(String image) {
        try {
            return TextureUtil.loadImage(image);
        } 
        catch (Exception e) {
            Cons.println("*** A problem occured loading an image: " + e);
        }
        
        return new TextureRegion(TextureUtil.createImage(10, 10));
    }
    
    
    /**
     * Loads a pixel map from the file system.
     * 
     * @param image
     * @return the Pixmap
     */
    public static Pixmap loadPixmap(String image) {
        try {
            return TextureUtil.loadPixmap(image);
        } 
        catch (Exception e) {
            Cons.println("*** A problem occured loading an image: " + e);
        }
        
        return new Pixmap(10, 10, Format.RGBA8888);
    }
    
    
    
    /**
     * Loads the image from the file system, with a supplied color mask
     * @param image
     * @param mask the color to change to transparent
     * @return the texture
     */
    public static TextureRegion loadImage(String image, int mask) {
        try {
            Pixmap map = TextureUtil.loadPixmap(image);
            TextureRegion region = TextureUtil.tex(TextureUtil.applyMask(map, new Color( (mask<<8) | 0xff)));
            map.dispose();
            
            return region;
        } 
        catch (Exception e) {
            Cons.println("*** A problem occured loading an image: " + e);
        }
        
        return new TextureRegion(TextureUtil.createImage(10, 10));
    }
    
    
    /**
     * Model consists of multiple frames
     * 
     * @author Tony
     *
     */
    public static class Model {
        private TextureRegion[] frames;
        public Model(TextureRegion image, int width, int height, int row, int col) {            
            this.frames = TextureUtil.splitImage(image, width,  height, row, col);
        }
        
        /**
         * @return the frames
         */
        public TextureRegion[] getFrames() {
            return frames;
        }
        
        /**
         * @param i
         * @return the frame at the supplied index
         */
        public TextureRegion getFrame(int i) {
            return frames[i];
        }
        
        
        /**
         * Destroys all images 
         */
        public void destroy() {
            if(frames!=null) {
                for(int i = 0; i < frames.length;i++) {
                    frames[i].getTexture().dispose();
                }
            }
        }
    }
    
    public static AnimatedImage newMissileAnim() {
        Animation animation = newAnimation(new int[]{
                200
        });                        
        return new AnimatedImage(rocketImage, animation);
    }
    
    public static AnimatedImage newExplosionAnim() {
        int v = 50;
        Animation animation = newAnimation(new int[]{
            /*90, 90, 90, 90,
            90, 90, 90, 90,
            90, 90, 90, 90,
            90, 90, 90, 10
            */
            v, v, v, v,
            v, v, v, v,
            v, v, v, v,
            10, 10, 10, 0            
        });                        
        animation.loop(false);
        return new AnimatedImage(explosionImage, animation);
    }
            
    public static AnimatedImage newAlliedBackDeathAnim() {
        int frameTime = 75;
        Animation animation = newAnimation(new int[] {
            frameTime, frameTime, frameTime, frameTime, frameTime, frameTime,
            frameTime, frameTime, frameTime, frameTime, frameTime, frameTime*9,            
        });
        AnimatedImage anim = new AnimatedImage(alliedBackDeathImage, animation);
        anim.loop(false);
        return anim;
    }
    
    public static AnimatedImage newAlliedBackDeath2Anim() {
        int frameTime = 75;
        Animation animation = newAnimation(new int[] {
                frameTime, frameTime, frameTime, frameTime, frameTime, frameTime,
                frameTime, frameTime, frameTime, frameTime, frameTime, frameTime*9,                
        });
        AnimatedImage anim = new AnimatedImage(alliedBackDeath2Image, animation);
        anim.loop(false);
        return anim;
    }
    
    public static AnimatedImage newAlliedExplosionDeathAnim() {
        // TODO
        return newAlliedFrontDeath2Anim();
    }
    
    
    public static AnimatedImage newAlliedFrontDeathAnim() {
        int frameTime = 75;
        Animation animation = newAnimation(new int[] {
            frameTime, frameTime, frameTime, frameTime, 
            frameTime, frameTime, frameTime, frameTime,
            frameTime, frameTime, frameTime, frameTime*9,
        });
        AnimatedImage anim = new AnimatedImage(alliedFrontDeathImage, animation);
        anim.loop(false);
        return anim;
    }
    
    public static AnimatedImage newAlliedFrontDeath2Anim() {
        int frameTime = 50;
        Animation animation = newAnimation(new int[] {
            frameTime, frameTime, frameTime, frameTime, frameTime,              
            frameTime, frameTime, frameTime, frameTime, frameTime*9,
        });
        AnimatedImage anim = new AnimatedImage(alliedFrontDeath2Image, animation);
        anim.loop(false);
        return anim;
    }
    
    public static AnimatedImage newAxisBackDeathAnim() {
        int frameTime = 75;
        Animation animation = newAnimation(new int[] {
            frameTime, frameTime, frameTime, frameTime, frameTime, frameTime, 
            frameTime, frameTime, frameTime, frameTime, frameTime, frameTime*9,
        });
        AnimatedImage anim = new AnimatedImage(axisBackDeathImage, animation);
        anim.loop(false);
        return anim;
    }
    
    public static AnimatedImage newAxisBackDeath2Anim() {
        int frameTime = 75;
        Animation animation = newAnimation(new int[] {
                frameTime, frameTime, frameTime, frameTime, frameTime, frameTime, 
                frameTime, frameTime, frameTime, frameTime, frameTime, frameTime*9,            
        });
        AnimatedImage anim = new AnimatedImage(axisBackDeath2Image, animation);
        anim.loop(false);
        return anim;
    }
    
    public static AnimatedImage newAxisFrontDeathAnim() {
        int frameTime = 75;
        Animation animation = newAnimation(new int[] {
            frameTime, frameTime, frameTime, frameTime,
            frameTime, frameTime, frameTime, frameTime, 
            frameTime, frameTime, frameTime, frameTime*9,
        });
        AnimatedImage anim = new AnimatedImage(axisFrontDeathImage, animation);
        anim.loop(false);
        return anim;
    }
    
    public static AnimatedImage newAxisFrontDeath2Anim() {
        int frameTime = 50;
        Animation animation = newAnimation(new int[] {
            frameTime, frameTime, frameTime, frameTime, frameTime,             
            frameTime, frameTime, frameTime, frameTime, frameTime*9,
        });
        AnimatedImage anim = new AnimatedImage(axisFrontDeath2Image, animation);
        anim.loop(false);
        return anim;
    }
    
    public static AnimatedImage newAxisExplosionDeathAnim() {
        // TODO
        return newAxisFrontDeath2Anim();
    }
    
    /**
     * Creates a new {@link Animation}
     * @param obj
     * @return
     */
    public static Animation newAnimation(int[] frameTimes) {
        
        AnimationFrame[] frames = new AnimationFrame[frameTimes.length];
        
        int frameNumber = 0;
        for(; frameNumber < frameTimes.length; frameNumber++) {
            frames[frameNumber] = new AnimationFrame(frameTimes[frameNumber], frameNumber);
        }

        Animation animation = new FramedAnimation(frames);
        return animation;
    }
    
    public static AnimatedImage newAnimatedImage(int[] frameTimes, TextureRegion[] frames) {        
        Animation animation = newAnimation(frameTimes);                        
        return new AnimatedImage(frames, animation);
    }

    public static AnimatedImage newAnimatedSplitImage(int[] frameTimes, TextureRegion image, int row, int col) {                
        Animation animation = newAnimation(frameTimes);        
        TextureRegion[] images = TextureUtil.splitImage(image, row, col);
        
        return new AnimatedImage(images, animation);
    }
    
    public static AnimatedImage newAnimatedSplitImage(int[] frameTimes, Pixmap image, int row, int col, Integer mask) {        
        if(mask!=null) {
            Pixmap oldimage = image;
            image = TextureUtil.applyMask(image, new Color(mask));
            oldimage.dispose();
        }
        
        Animation animation = newAnimation(frameTimes);        
        TextureRegion[] images = TextureUtil.splitImage(TextureUtil.tex(image), row, col);
        
        return new AnimatedImage(images, animation);
    }
    
    public static TextureRegion randomBloodspat() {
        return bloodImages[ random.nextInt(4) ];
    }
    
    public static TextureRegion randomGib() {
        return gibImages[ random.nextInt(4) ];
    }
    
    
    public static AnimatedImage newThompsonMuzzleFlash() {
        return newAnimatedImage(new int[] { 20, 20, 10, 20 }, thompsonMuzzleFlash).loop(false);
    }
    
    public static AnimatedImage newM1GarandMuzzleFlash() {
        return newAnimatedImage(new int[] { 20, 20, 10, 20 }, m1GarandMuzzleFlash).loop(false);
    }
    
    public static AnimatedImage newSpringfieldMuzzleFlash() {
        return newAnimatedImage(new int[] { 20, 20, 10, 20 }, springfieldMuzzleFlash).loop(false);
    }
    
    public static AnimatedImage newKar98MuzzleFlash() {
        return newAnimatedImage(new int[] { 20, 20, 10, 20 }, kar98MuzzleFlash).loop(false);
    }
    public static AnimatedImage newMP40MuzzleFlash() {
        return newAnimatedImage(new int[] { 20, 20, 10, 20 }, mp40MuzzleFlash).loop(false);
    }
    
    public static AnimatedImage newMP44MuzzleFlash() {
        return newAnimatedImage(new int[] { 50, 150, 120, 50 }, mp44MuzzleFlash).loop(false);
    }
    
    public static AnimatedImage newShotgunMuzzleFlash() {
        return newAnimatedImage(new int[] { 20, 20, 10, 20 }, shotgunMuzzleFlash).loop(false);
    }
    public static AnimatedImage newRocketMuzzleFlash() {
        return newAnimatedImage(new int[] { 20, 20, 10, 20 }, rocketMuzzleFlash).loop(false);
    }
    
    public static AnimatedImage newRiskerMuzzleFlash() {
        return newAnimatedImage(new int[] { 20, 20, 10, 20 }, riskerMuzzleFlash).loop(false);
    }
        
    public static AnimatedImage newShermanTankTracks() {
        return newAnimatedImage(new int[] {100}, new TextureRegion[] {shermanTankBase});
    }
    public static AnimatedImage newShermanTankTracksDamaged() {
        return newAnimatedImage(new int[] {100}, new TextureRegion[] {shermanTankBaseDamaged});
    }
    
    public static AnimatedImage newPanzerTankTracks() {
        return newAnimatedImage(new int[] {100}, new TextureRegion[] {panzerTankBase});
    }
    public static AnimatedImage newPanzerTankTracksDamaged() {        
        return newAnimatedImage(new int[] {100}, new TextureRegion[] {panzerTankBaseDamaged});
    }
}
