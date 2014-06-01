/*
 * see license.txt 
 */
package seventh.client.gfx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.shared.Cons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class Art {
	private static final Random random = new Random();
	
	public static final TextureRegion BLACK_IMAGE = new TextureRegion();
	static {
		Pixmap map = TextureUtil.createPixmap(128, 128);
		map.setColor(Color.BLACK);
		map.fillRectangle(0, 0, map.getWidth(), map.getHeight());
		BLACK_IMAGE.setTexture(new Texture(map));
	}
	
	public static  TextureRegion shotgunImage = null;
	public static  TextureRegion rpgImage = null;			

	public static  Sprite grenadeIcon = null;
	public static  TextureRegion grenadeImage = null;
	
	public static  TextureRegion springfieldImage = null;
	public static  TextureRegion thompsonImage = null;	
	public static  TextureRegion m1GarandImage = null;
	public static  TextureRegion kar98Image = null;
	public static  TextureRegion mp44Image = null;
	public static  TextureRegion mp40Image = null;
	public static  TextureRegion pistolImage = null;
	public static  TextureRegion riskerImage = null;
	
	public static  TextureRegion bombImage = null;
	public static TextureRegion computerImage = null;	
	
	public static  TextureRegion legsImage = null;		
	public static  Model legsModel = null;	
	public static  Model alliedCharacterModel = null;
	public static  Model axisCharacterModel = null;
		
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
	
	private static  TextureRegion[] explosionImage = null;
	private static  TextureRegion[] fireImage = null;
	private static  TextureRegion[] rocketImage = null;
	
	public static  TextureRegion deathsImage = null;
	private static  TextureRegion[] alliedBackDeathImage = null;		
	private static  TextureRegion[] alliedFrontDeathImage = null;	
	private static  TextureRegion[] axisBackDeathImage = null;
	private static  TextureRegion[] axisFrontDeathImage = null;	
	
	public static  TextureRegion[] bloodImages = null;
	public static  TextureRegion[] gibImages = null;
	
	public static  TextureRegion smokeImage = null;
	
//	public static  TextureRegion tankImage = loadImage("./seventh/gfx/tank.png");
	
	public static  Sprite smallAssaultRifleIcon = null;
	public static  Sprite smallShotgunIcon = null;
	public static  Sprite smallRocketIcon = null;
	public static  Sprite smallSniperRifleIcon = null;
	public static  Sprite smallM1GarandIcon = null;
	public static  Sprite smallGrenadeIcon = null;
	public static  Sprite smallExplosionIcon = null;
	public static  Sprite smallkar98Icon = null;
	public static  Sprite smallmp44Icon = null;
	public static  Sprite smallmp40Icon = null;
	public static  Sprite smallPistolIcon = null;
	public static  Sprite smallRiskerIcon = null;
	
	public static  TextureRegion cursorImg = null;		
	
	public static TextureRegion[] thompsonMuzzleFlash = null;
	public static TextureRegion[] m1GarandMuzzleFlash = null;
	public static TextureRegion[] springfieldMuzzleFlash = null;
	public static TextureRegion[] kar98MuzzleFlash = null;
	public static TextureRegion[] mp44MuzzleFlash = null;
	public static TextureRegion[] mp40MuzzleFlash = null;
	public static TextureRegion[] shotgunMuzzleFlash = null;
	public static TextureRegion[] rocketMuzzleFlash = null;
	public static TextureRegion[] riskerMuzzleFlash = null;
	
	public static Sprite healthIcon = null;
	public static Sprite staminaIcon = null;
	public static Sprite upArrow = null;
	public static Sprite downArrow = null;
	
	public static TextureRegion fireWeaponLight = null;
	public static TextureRegion lightMap = null;
	public static TextureRegion flashLight = null;
	
	public static TextureRegion bullet = null;
	
	
	public static TextureRegion[] tankTracks = null;
	public static TextureRegion tankTurret = null;
	
	public static int[] animationFrames = null;
	
	public static void reload() {
		destroy();
		load();
	}
	
	public static void load() {

		shotgunImage = loadImage("./seventh/gfx/weapons/m3.bmp", 0xff00ff);
		rpgImage = loadImage("./seventh/gfx/weapons/rpg.bmp", 0xff00ff);

//		Pixmap grenadePixmap = loadPixmap("./seventh/gfx/weapons/grenade.png");
//		grenadeImage = TextureUtil.tex(TextureUtil.resizePixmap(grenadePixmap, 12, 12));
//		grenadeIcon = TextureUtil.tex(grenadePixmap);

		grenadeImage = loadImage("./seventh/gfx/weapons/grenade.png");
		grenadeIcon = TextureUtil.resizeImage(grenadeImage, 12, 12);
		
		springfieldImage = loadImage("./seventh/gfx/weapons/springfield.bmp", 0xff00ff);
		thompsonImage = loadImage("./seventh/gfx/weapons/thompson.bmp", 0xff00ff);
		m1GarandImage = loadImage("./seventh/gfx/weapons/m1garand.bmp", 0xff00ff);
		kar98Image = loadImage("./seventh/gfx/weapons/kar98.bmp", 0xff00ff);
		mp44Image = loadImage("./seventh/gfx/weapons/mp44.bmp", 0xff00ff);
		mp40Image = loadImage("./seventh/gfx/weapons/mp40.bmp", 0xff00ff);
		pistolImage = loadImage("./seventh/gfx/weapons/pistol.bmp", 0xff00ff);
		riskerImage = loadImage("./seventh/gfx/weapons/risker.bmp", 0xff00ff);
		
		bombImage = loadImage("./seventh/gfx/weapons/bomb.bmp", 0xff00ff);
		computerImage = loadImage("./seventh/gfx/computer.bmp");

		legsImage = loadImage("./seventh/gfx/player/legs.bmp", 0xff00ff);
		legsModel = new Model(legsImage, 128, 64, 2, 4);

		alliedCharacterModel = new Model(loadImage("./seventh/gfx/player/allied_01.png"), 128, 128, 2, 3);
		axisCharacterModel = new Model(loadImage("./seventh/gfx/player/axis_01.png"), 128, 128, 2, 3);

		shotgunIcon = loadImage("./seventh/gfx/weapons/shotgun_icon.png");
		rocketIcon = loadImage("./seventh/gfx/weapons/rpg_icon.png");
		springfieldIcon = loadImage("./seventh/gfx/weapons/springfield_icon.png");
		thompsonIcon = loadImage("./seventh/gfx/weapons/thompson_icon.png");
		m1GarandIcon = loadImage("./seventh/gfx/weapons/m1garand_icon.png");
		kar98Icon = loadImage("./seventh/gfx/weapons/kar98_icon.png");
		mp44Icon = loadImage("./seventh/gfx/weapons/mp44_icon.png");
		mp40Icon = loadImage("./seventh/gfx/weapons/mp40_icon.png");
		pistolIcon = loadImage("./seventh/gfx/weapons/pistol_icon.png");
		riskerIcon = loadImage("./seventh/gfx/weapons/risker_icon.png");
		
		explosionImage = TextureUtil.splitImage(loadImage("./seventh/gfx/explosion.png"), 2, 2);
		TextureRegion tmp = loadImage("./seventh/gfx/fire.png");
		tmp.flip(false, false);
		fireImage = TextureUtil.splitImage(tmp, 1, 5);
		rocketImage = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/rocket.bmp", 0xff00ff), 1, 1);

		deathsImage = loadImage("./seventh/gfx/player/death_symbol.png");
		

		alliedBackDeathImage = TextureUtil.splitImage(
				TextureUtil.subImage(loadImage("./seventh/gfx/player/allied_01_death_01.png"), 0, 0, 170, 200), 2, 3);

		alliedFrontDeathImage = TextureUtil.splitImage(
				TextureUtil.subImage(loadImage("./seventh/gfx/player/allied_01_death_02.png"), 0, 0, 170, 200), 2, 3);

		axisBackDeathImage = TextureUtil.splitImage(
				TextureUtil.subImage(loadImage("./seventh/gfx/player/axis_01_death_01.png"), 0, 0, 170, 200), 2, 3);
		axisFrontDeathImage = TextureUtil.splitImage(
				TextureUtil.subImage(loadImage("./seventh/gfx/player/axis_01_death_02.png"), 0, 0, 170, 200), 2, 3);

		bloodImages = TextureUtil.splitImage(loadImage("./seventh/gfx/particles/blood.png"), 2, 4);
		gibImages = TextureUtil.splitImage(loadImage("./seventh/gfx/particles/gibs.png"), 2, 2);

		smokeImage = loadImage("./seventh/gfx/particles/smoke.png");

		// tankImage = loadImage("./seventh/gfx/tank.png");

		final int smallIconWidth = 64, smallIconHeight = 32;
		smallAssaultRifleIcon = TextureUtil.resizeImage(thompsonIcon, smallIconWidth, smallIconHeight);
		smallShotgunIcon = TextureUtil.resizeImage(shotgunIcon, smallIconWidth, smallIconHeight);
		smallRocketIcon = TextureUtil.resizeImage(rocketIcon, smallIconWidth, smallIconHeight);
		smallSniperRifleIcon = TextureUtil.resizeImage(springfieldIcon, smallIconWidth, smallIconHeight);
		smallM1GarandIcon = TextureUtil.resizeImage(m1GarandIcon, smallIconWidth, smallIconHeight);
		smallGrenadeIcon = TextureUtil.resizeImage(grenadeImage, smallIconWidth / 3, smallIconHeight / 3);
		smallExplosionIcon = TextureUtil.resizeImage(explosionImage[0], smallIconWidth / 2, smallIconHeight / 2);
		smallkar98Icon = TextureUtil.resizeImage(kar98Icon, smallIconWidth, smallIconHeight);
		smallmp44Icon = TextureUtil.resizeImage(mp44Icon, smallIconWidth, smallIconHeight);
		smallmp40Icon = TextureUtil.resizeImage(mp40Icon, smallIconWidth, smallIconHeight);
		smallPistolIcon = TextureUtil.resizeImage(pistolIcon, smallIconWidth, smallIconHeight);
		smallRiskerIcon = TextureUtil.resizeImage(riskerIcon, smallIconWidth, smallIconHeight);

		cursorImg = loadImage("./seventh/gfx/crosshair.png");
						
		thompsonMuzzleFlash = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/thompson_muzzle_flash.png"), 2, 2);
		springfieldMuzzleFlash = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/springfield_muzzle_flash.png"), 2, 2);
		m1GarandMuzzleFlash = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/m1garand_muzzle_flash.png"), 2, 2);
		kar98MuzzleFlash = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/kar98_muzzle_flash.png"), 2, 2);
		mp40MuzzleFlash = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/mp40_muzzle_flash.png"), 2, 2);
		mp44MuzzleFlash = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/mp44_muzzle_flash.png"), 2, 2);
		shotgunMuzzleFlash = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/shotgun_muzzle_flash.png"), 2, 2);
		rocketMuzzleFlash = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/rpg_muzzle_flash.png"), 2, 2);
		riskerMuzzleFlash = TextureUtil.splitImage(loadImage("./seventh/gfx/weapons/risker_muzzle_flash.png"), 2, 2);
		
		
		healthIcon = TextureUtil.resizeImage(loadImage("./seventh/gfx/health.bmp"), 12, 12);
		staminaIcon = TextureUtil.resizeImage(loadImage("./seventh/gfx/stamina.png"), 12, 12);
		
		TextureRegion navArrow = loadImage("./seventh/gfx/ui_nav_arrows.png");
		upArrow = new Sprite(navArrow);
		upArrow.flip(false, true);
		downArrow = new Sprite(navArrow);
		
		fireWeaponLight = loadImage("./seventh/gfx/weapon_fire.png");
		lightMap = loadImage("./seventh/gfx/light.png");
		flashLight = loadImage("./seventh/gfx/lightmap_flashlight.png");
		
		bullet = loadImage("./seventh/gfx/bullet.png");
		
		tankTracks = TextureUtil.splitImage(loadImage("./seventh/gfx/vehicles/tank_tracks.png"), 1, 1);
		tankTurret = loadImage("./seventh/gfx/vehicles/tank_turret.png");
	}

	public static void destroy() {

		free(shotgunImage);
		free(rpgImage);

		free(grenadeImage);
		free(grenadeIcon);

		free(springfieldImage);
		free(thompsonImage);
		free(m1GarandImage);
		free(kar98Image);
		free(mp44Image);
		free(mp40Image);
		free(pistolImage);
		free(riskerImage);

		free(bombImage);

		free(legsImage);
		free(legsModel);

		free(alliedCharacterModel);
		free(axisCharacterModel);

		free(shotgunIcon);
		free(rocketIcon);
		free(springfieldIcon);
		free(thompsonIcon);
		free(m1GarandIcon);
		free(kar98Icon);
		free(mp44Icon);
		free(mp40Icon);
		free(pistolIcon);
		free(riskerIcon);
		
		// private static final TextureRegion[] explosionImage =
		// TextureUtil.splitImage(
		// TextureUtil.resizeImage(loadImage("./seventh/gfx/explosion1.png"),
		// 128,128), 4, 4);
		free(explosionImage);
		free(fireImage);
		free(rocketImage);

		free(deathsImage);

		free(alliedBackDeathImage);

		free(alliedFrontDeathImage);

		free(axisBackDeathImage);
		free(axisFrontDeathImage);

		free(bloodImages);
		free(gibImages);

		free(smokeImage);

		free(smallAssaultRifleIcon);
		free(smallShotgunIcon);
		free(smallRocketIcon);
		free(smallSniperRifleIcon);
		free(smallM1GarandIcon);
		free(smallGrenadeIcon);
		free(smallExplosionIcon);
		free(smallkar98Icon);
		free(smallmp44Icon);
		free(smallmp40Icon);
		free(smallPistolIcon);
		free(smallRiskerIcon);
		
		free(cursorImg);		
		
		free(fireWeaponLight);
		free(flashLight);
		free(lightMap);
		
		free(bullet);
		
		free(tankTracks);
		free(tankTurret);
	}
	
	public static void free(TextureRegion region) {
		if(region != null) {
			region.getTexture().dispose();
		}
	}
	
	public static void free(TextureRegion[] region) {
		if(region != null) {
			for(TextureRegion r : region)
				free(r);
		}
	}
	
	public static void free(Model model) {
		if(model!=null) {
			model.destroy();
		}
	}
	
	
	public static TextureRegion loadImage(String image) {
		try {
			return TextureUtil.loadImage(image);
		} catch (Exception e) {
			Cons.println("*** A problem occured loading an image: " + e);
		}
		return new TextureRegion(TextureUtil.createImage(10, 10));
	}
	
	public static Pixmap loadPixmap(String image) {
		try {
			return TextureUtil.loadPixmap(image);
		} catch (Exception e) {
			Cons.println("*** A problem occured loading an image: " + e);
		}
		return new Pixmap(10, 10, Format.RGBA8888);
	}
	
	public static TextureRegion loadImage(String image, int mask) {
		try {
			Pixmap map = TextureUtil.loadPixmap(image);
			TextureRegion region = TextureUtil.tex(TextureUtil.applyMask(map, new Color( (mask<<8) | 0xff)));
			map.dispose();
			
			return region;
		} catch (Exception e) {
			Cons.println("*** A problem occured loading an image: " + e);
		}
		return new TextureRegion(TextureUtil.createImage(10, 10));
	}
	
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
		
		public TextureRegion getFrame(int i) {
			return frames[i];
		}
		
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
		Animation animation = newAnimation(new int[]{
			100, 80, 0, 0
		});						
		return new AnimatedImage(explosionImage, animation);
	}
	
	public static AnimatedImage newFireAnim() {
		Animation animation = newAnimation(new int[]{
				80, 80, 80, 80, 80
		});						
		return new AnimatedImage(fireImage, animation);
	}
	
	public static AnimatedImage newBloodAnim() {
//		final int frameTime = 150;
		Animation animation = newAnimation(new int[]{
			80, 50, 60, 40
			//frameTime, frameTime,frameTime, frameTime,frameTime, frameTime,
			//frameTime, frameTime,frameTime, frameTime,
		});						
		AnimatedImage anim = new AnimatedImage(explosionImage, animation);
		anim.loop(false);
		return anim;
	}
	
	public static AnimatedImage newDeathAnim() {
		Animation animation = newAnimation(new int[]{
			2000, 2000
		});						
		AnimatedImage anim = new AnimatedImage(new TextureRegion[] { deathsImage, deathsImage }, animation);
		anim.loop(false);
		return anim;
	}
	
	public static AnimatedImage newAlliedBackDeathAnim() {
		int frameTime = 150;
		Animation animation = newAnimation(new int[] {
			frameTime, frameTime, frameTime, frameTime, frameTime, frameTime*9,
		});
		AnimatedImage anim = new AnimatedImage(alliedBackDeathImage, animation);
		anim.loop(false);
		return anim;
	}
	
	public static AnimatedImage newAlliedFrontDeathAnim() {
		int frameTime = 150;
		Animation animation = newAnimation(new int[] {
			frameTime, frameTime, frameTime, frameTime, frameTime, frameTime*9,
		});
		AnimatedImage anim = new AnimatedImage(alliedFrontDeathImage, animation);
		anim.loop(false);
		return anim;
	}
	
	public static AnimatedImage newAxisBackDeathAnim() {
		int frameTime = 150;
		Animation animation = newAnimation(new int[] {
			frameTime, frameTime, frameTime, frameTime, frameTime, frameTime*9,
		});
		AnimatedImage anim = new AnimatedImage(axisBackDeathImage, animation);
		anim.loop(false);
		return anim;
	}
	
	public static AnimatedImage newAxisFrontDeathAnim() {
		int frameTime = 150;
		Animation animation = newAnimation(new int[] {
			frameTime, frameTime, frameTime, frameTime, frameTime, frameTime*9,
		});
		AnimatedImage anim = new AnimatedImage(axisFrontDeathImage, animation);
		anim.loop(false);
		return anim;
	}
	
	public static AnimatedImage newWalkAnim(Model model) {
		int frameTime = 80;
		Animation animation = newAnimation(new int[] {
			frameTime, frameTime, frameTime, frameTime, frameTime, frameTime,
		});
		AnimatedImage anim = new AnimatedImage(model.getFrames(), animation);
		anim.loop(true);
		return anim;
	}
	
	/**
	 * Creates a new {@link Animation}
	 * @param obj
	 * @return
	 */
	public static Animation newAnimation(int[] frameTimes) {
		Animation animation = null;
		
		List<AnimationFrame> frames = new ArrayList<AnimationFrame>(frameTimes.length);

		int frameNumber = 0;
		for(; frameNumber < frameTimes.length; frameNumber++) {
			frames.add(new AnimationFrame(frameTimes[frameNumber], frameNumber));
		}

		animation = new FramedAnimation(frames);
	

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
	
	public static AnimatedImage newTankTracks() {
		return newAnimatedImage(new int[] {100}, tankTracks);
//		if(animationFrames != null) {
//			return newAnimatedImage(animationFrames, tankTracks);
//		}
//		int frameTime = 450;
//		return newAnimatedImage(new int[] { frameTime, frameTime, frameTime, frameTime, frameTime, frameTime }, tankTracks);
	}
}
