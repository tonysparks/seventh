/*
 * see license.txt 
 */
package seventh.client.gfx;


import seventh.client.ClientPlayerEntity;
import seventh.client.gfx.Art.Model;
import seventh.client.gfx.particle.Effects;
import seventh.client.weapon.ClientRocketLauncher;
import seventh.client.weapon.ClientWeapon;
import seventh.game.Entity.State;
import seventh.game.weapons.Weapon;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Renders the Player
 * 
 * @author Tony
 *
 */
public class PlayerSprite implements Renderable {

	private ClientPlayerEntity entity;
	
	private final AnimatedImage idleBody, 
						  crouchBody,
						  walkBody, 
						  runBody, 
						  sprintBody, 
						  reloadBody,
						  switchWeaponBody,
						  meleeBody;
	
		
	private final AnimatedImage idleLegsAnimation,
								crouchingLegsAnimation,
								walkLegsAnimation,
								runLegsAnimation,
								sprintLegsAnimation						  
								;
	
	private AnimatedImage activeBodyPosition;
	private AnimatedImage activeLegsAnimation;
	
	private Timer walkingCycle;
	private double bobCycle;
	private int direction;
	private float xOffset, yOffset;
	
	private boolean isReloading, 
					isSwitching, 
					isMelee, 
					isFiring;
	
	
	private Effects effects;	
	private Sprite sprite;
	
	private long flashTime;
	private boolean showFlash, toggleFlash;
	
	
	/**
	 * Debug class
	 * 
	 * @author Tony
	 *
	 */
	static class Adjustments {
		public float xOffset, yOffset;
		public float x, y;
		
		public void poll() {
			if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
				xOffset += 1;
			}
			if(Gdx.input.isKeyPressed(Keys.LEFT)) {
				xOffset -= 1;
			}
			if(Gdx.input.isKeyPressed(Keys.UP)) {
				yOffset += 1;
			}
			if(Gdx.input.isKeyPressed(Keys.DOWN)) {
				yOffset -= 1;
			}
			if(Gdx.input.isKeyPressed(Keys.PAGE_UP)) {
				y += 1;
			}
			if(Gdx.input.isKeyPressed(Keys.PAGE_DOWN)) {
				y -= 1;
			}
			if(Gdx.input.isKeyPressed(Keys.HOME)) {
				x += 1;
			}
			if(Gdx.input.isKeyPressed(Keys.END)) {
				x -= 1;
			}
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("[xOffset=").append(xOffset).append(", yOffset=").append(yOffset).append(", x=").append(x).append(", y=")
					.append(y).append("]");
			return builder.toString();
		}
		
		
	}
	
	Adjustments adjustments=new Adjustments();
	
	/**
	 * 
	 */
	public PlayerSprite(ClientPlayerEntity entity, 
						Model bodyModel, 
						Model walkLegsModel,
						TextureRegion crouchLegsModel,
						Model sprintLegsModel) {
		this.entity = entity;
		
		this.effects = new Effects();				
		
		idleBody = newAnimation(100, bodyModel.getFrame(0));
		crouchBody = newAnimation(100, bodyModel.getFrame(3));
		walkBody = newAnimation(100, bodyModel.getFrame(2));
		runBody = newAnimation(100, bodyModel.getFrame(0)); 
		sprintBody = newAnimation(100, bodyModel.getFrame(1));
		reloadBody = newAnimation(500, bodyModel.getFrame(5), bodyModel.getFrame(0));
		switchWeaponBody = newAnimation(100, bodyModel.getFrame(5));
		meleeBody = newAnimation(100, bodyModel.getFrame(1));
				
		idleLegsAnimation = newAnimation(150, walkLegsModel.getFrame(0));
		crouchingLegsAnimation = newAnimation(0, crouchLegsModel);
		walkLegsAnimation = newAnimation(150, walkLegsModel.getFrames());
		runLegsAnimation = newAnimation(100, walkLegsModel.getFrames()); 
		sprintLegsAnimation = newAnimation(130, sprintLegsModel.getFrames()); // 150
		
		activeBodyPosition = idleBody; 
		activeLegsAnimation = idleLegsAnimation;
		
		this.walkingCycle = new Timer(true, 300); // 300
		this.direction = 1;
				
		this.sprite = new Sprite();
	}
		
	private AnimatedImage newAnimation(int frameTime, TextureRegion ... frames) {
		int[] frameTimes = new int[frames.length];
		for(int i = 0; i < frameTimes.length;i++) {
			frameTimes[i] = frameTime;
		}
		 
		return Art.newAnimatedImage(frameTimes, frames);
				
	}
	
	private void resetLegMovements() {
		walkLegsAnimation.reset();
		runLegsAnimation.reset();
		sprintLegsAnimation.reset();
		
		bobCycle = 0;
		direction = 1;
		walkingCycle.pause();
	}

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		
		// TODO: delete
		adjustments.poll();
		
		this.effects.update(timeStep);
		
		/* bob cycling */
		int max = 5;				
		double swingSpeed = 2.0;
		xOffset = yOffset = 0;
		
		activeBodyPosition = idleBody; 
		activeLegsAnimation = idleLegsAnimation;		
						
		State currentState = entity.getCurrentState();
		switch(currentState) {
		case IDLE:
			activeBodyPosition = idleBody;
			activeLegsAnimation = idleLegsAnimation;
			resetLegMovements();
			break;
		case CROUCHING:
			activeBodyPosition = crouchBody;
			activeLegsAnimation = crouchingLegsAnimation;
			resetLegMovements();
			break;
		case WALKING:						
			activeBodyPosition = walkBody;
			activeLegsAnimation = walkLegsAnimation;
			
			max = 5;
			swingSpeed = 0.6f;
			walkingCycle.start();
			break;
		case RUNNING:
			activeBodyPosition = runBody;
			activeLegsAnimation = runLegsAnimation;
			
			max = 8;
			swingSpeed = 1.4f;
			walkingCycle.start();
			break;
		case SPRINTING:
			activeBodyPosition = sprintBody;
			activeLegsAnimation = sprintLegsAnimation;
			
			max = 10;
			swingSpeed = 2.0f;				
			
			//Vector2f dir = entity.getFacing();
			//xOffset += (dir.y * direction) * 1.12f;
			//yOffset += (dir.x * direction) * 1.12f;			
			walkingCycle.start();
			break;
		case DEAD:
			resetLegMovements();
			break;
		default:
			resetLegMovements();
				
		}
		
		walkingCycle.update(timeStep);

//		if(walkingCycle.isTime()) {
//			direction *= -1;			
//		}
					
		bobCycle += direction*swingSpeed;//*0.8;		
				
		if(bobCycle>max) {
			bobCycle=max;
			direction *= -1;
		}
		else if(bobCycle<-max) {
			bobCycle=-max;
			direction *= -1;
		}
		
		
		ClientWeapon weapon = entity.getWeapon();
		if(weapon != null) {
			
			this.isReloading = weapon.getState() == seventh.game.weapons.Weapon.State.RELOADING;
			this.isSwitching = weapon.getState() == seventh.game.weapons.Weapon.State.SWITCHING;
			this.isMelee = weapon.getState() == seventh.game.weapons.Weapon.State.MELEE_ATTACK;
			this.isFiring = weapon.getState() == seventh.game.weapons.Weapon.State.FIRING;
						
			// TODO
//			this.isMelee = true;
			
			if( this.isReloading ) {
				this.activeBodyPosition = reloadBody;
			}
			else if (this.isMelee) {
				this.activeBodyPosition =  meleeBody;
			}
			else if (this.isSwitching) {
				this.activeBodyPosition = switchWeaponBody;
			}
			
//			if(weapon.isFirstFire()) {
//				Vector2f pos = entity.getCenterPos();
//				Vector2f ejectPos = entity.getFacing().createClone();
//				Vector2f.Vector2fMA(pos, ejectPos, 20, ejectPos);
//				effects.addEffect(new BrassEmitter(ejectPos, 10000, 10));
//			}
			
			AnimatedImage muzzleAnim = weapon.getMuzzleFlash();
			if(muzzleAnim!=null) {
				muzzleAnim.update(timeStep);
			}
		}
		
		if(this.isFiring) {
			this.flashTime -= timeStep.getDeltaTime();
			if(this.flashTime <= 0) {
				boolean cycleFlash = weapon != null && weapon.isAutomatic();
				if(cycleFlash) {
					this.showFlash = !this.showFlash;
					this.flashTime = 90;
				}
				else {
					if(toggleFlash) {
						this.showFlash = true;
						this.flashTime = 90;
						toggleFlash = false;
					}
					else {
						this.showFlash = false;
					}
				}
			}
		}
		else {
			this.showFlash = false;
			this.toggleFlash = true;
			this.flashTime = 90;
		}
		
		activeLegsAnimation.update(timeStep);
		activeBodyPosition.update(timeStep);		
	}

	/**
	 * Renders the entities weapon
	 * 
	 * @param canvas
	 * @param weapon
	 * @param rx
	 * @param ry
	 * @param rot
	 */
	private void renderWeapon(Canvas canvas, ClientWeapon weapon, float rx, float ry, float rot) {
		if(weapon != null && weapon.getWeaponImage()!=null && !this.isSwitching) {	
			
			renderMuzzleFlash(canvas, weapon, rx, ry, rot);
			
			TextureRegion gun = weapon.getWeaponImage();
			setTextureRegion(sprite, gun);
			
			sprite.setScale(1.0f);					
									
			switch(this.entity.getCurrentState()) {
				case SPRINTING: {
					// original
//					sprite.setRotation(rot - 60.0f);										
//					sprite.setPosition(rx-13f, ry-39f);
//					sprite.setOrigin(14f, 40f);
					
					sprite.setRegionY(-32);
					sprite.setRegionHeight(64);
					sprite.setRotation(rot - 240f);					
					sprite.setPosition(rx - 16f, ry + 11f);
					sprite.setOrigin(17f, -10f);
					
					break;
				}
				case CROUCHING: {
					sprite.setPosition(rx-9.0f, ry-30.0f);
					sprite.setOrigin(10.0f, 31.0f);
					break;				
				}
				default: {
					sprite.setPosition(rx-10.0f, ry-34.0f);
					sprite.setOrigin(12.0f, 35.0f);					
				}
			}
									
			if(this.isReloading) {
				if(reloadBody.getAnimation().getCurrentFrame() == 0) {
					sprite.setRotation(rot - 12.0f);
				}
			}
			else if (this.isMelee) {
				// TODO: fix melee weapon				
				
				sprite.setRegionY(-32);
				sprite.setRegionHeight(64);			
				
				sprite.setRotation(rot - 240f);				
				sprite.setPosition(rx - 16f, ry + 11f);
				sprite.setOrigin(17f, -10f);
				
//				sprite.setPosition(rx-adjustments.xOffset, ry-adjustments.yOffset);
//				sprite.setOrigin(adjustments.x, adjustments.y);				
			}
			
			if(weapon instanceof ClientRocketLauncher) {
//				sprite.setRotation(rot - 60.0f);			
				// TODO: Crouching
				sprite.setPosition(rx+3f, ry-31f);
				sprite.setOrigin(-2f, 33f);
				
//				sprite.setPosition(rx-adjustments.xOffset, ry-adjustments.yOffset);
//				sprite.setOrigin(adjustments.x, adjustments.y);
			}
			
			canvas.drawSprite(sprite);
			debugRenderSprite(canvas, sprite, 0xffffff00);
		}
	}
	
	/**
	 * Renders the muzzle flash
	 * @param canvas
	 * @param weapon
	 * @param rx
	 * @param ry
	 * @param rot
	 */
	private void renderMuzzleFlash(Canvas canvas, ClientWeapon weapon, float rx, float ry, float rot) {
		AnimatedImage muzzleAnim = weapon.getMuzzleFlash();
		if(muzzleAnim != null) {
			if(weapon.getState().equals(Weapon.State.FIRING)) 
			{								
				/* if the weapon is automatic, we want to show
				 * the animation
				 */
				if(weapon.isAutomatic() && muzzleAnim.isDone()) {
					muzzleAnim.reset();
				}
				
				/* if we still have some animation to go, and this isn't the 
				 * Rocket launcher, lets render the muzzle flash
				 */
				if(!muzzleAnim.isDone() ) {										
					
					/* we don't want an animation for single fire weapons */
					if(weapon.isAutomatic()||weapon.isBurstFire()) {
						setTextureRegion(sprite, muzzleAnim.getCurrentImage());
					}
					else {
						setTextureRegion(sprite, muzzleAnim.getFrame(0));
						muzzleAnim.moveToLastFrame();						
					}
					
					boolean isCrouching = entity.getCurrentState()==State.CROUCHING; 
					
					float offsetX = isCrouching ? 22f : 26f; //24 
					if(weapon.hasLongBarrel()) {
						offsetX = isCrouching ? 26f : 32f; //32.0f;
					}
					
					float offsetY = isCrouching ? -12f : -16f;
					
					sprite.setPosition(rx+offsetX, ry+offsetY);
					sprite.setOrigin(-offsetX, -offsetY); //18
										
					sprite.setRotation(rot-80f);										
					canvas.drawSprite(sprite);
//					debugRenderSprite(canvas, sprite, 0xffff00ff);
					
					sprite.setRotation(rot);										
				}
			}
			else {				
				muzzleAnim.reset();
			}			
		}
	}
	
	/**
	 * Render the players body
	 * 
	 * @param canvas
	 * @param rx
	 * @param ry
	 * @param rot
	 * @param color
	 */
	private void renderBody(Canvas canvas, float rx, float ry, float rot, int color) {
		
//		int offset = 32;//16;
		float x = rx - 32f;//offset; -32
		float y = ry - 42f;//offset; -42
		
		State currentState = this.entity.getCurrentState();
		
		// TODO: do the math to do this outside
		// in imageScaler program
		sprite.setScale(0.8f, 0.8f);
		
		/* Renders the feet
		 */
		switch(currentState) {
			case CROUCHING: {
				sprite.setRotation(rot-10f);
				sprite.setPosition(rx, ry);
								
				setTextureRegion(sprite, activeLegsAnimation.getCurrentImage());
				sprite.flip(true, false);		
				
				sprite.translate(-32f, -32f);
				canvas.drawSprite(sprite);
				sprite.translate(32f, 32f);
				
				sprite.setRotation(rot);
				sprite.setPosition(x, y);
				
				Vector2f facing = entity.getFacing();
				sprite.translate(facing.x * -6.0f, facing.y * -6.0f);
				break;
			}
			case SPRINTING: {
				sprite.setRotation(rot);
				sprite.setPosition(x, y);
				
				setTextureRegion(sprite, activeLegsAnimation.getCurrentImage());
						
				sprite.translate(-6f, -6);
				canvas.drawSprite(sprite);
				sprite.translate(6f, 6);
				
				sprite.setRotation(rot);
				sprite.setPosition(x, y);				
				break;
			}
			default: {
				sprite.setRotation(rot);
				sprite.setPosition(x, y);
								
				setTextureRegion(sprite, activeLegsAnimation.getCurrentImage());
				
				Vector2f facing = entity.getFacing();
				float fx = facing.x + -12.0f;
				float fy = facing.y - 5.0f;
				
				sprite.translate(fx, fy);
				canvas.drawSprite(sprite);
				sprite.translate(-fx, -fy);
			}
		}
				
//		debugRenderSprite(canvas, sprite, 0xff00ff00);
		
		/* Renders the body
		 */
		{

			setTextureRegion(sprite, activeBodyPosition.getCurrentImage());		
			canvas.drawSprite(sprite);				
			
			
			debugRenderSprite(canvas, sprite, 0xff00ffff);
		}
	}
	
	/**
	 * Renders the player
	 * 
	 * @param canvas
	 * @param cameraPos
	 * @param pos
	 * @param color
	 */
	private void renderPlayer(Canvas canvas, Vector2f cameraPos, Vector2f pos, int color) {
		Rectangle bounds = entity.getBounds();
		
		float rx = (pos.x - cameraPos.x) + (bounds.width/2.0f) + xOffset;
		float ry = (pos.y - cameraPos.y) + (bounds.height/2.0f) + yOffset;
				
		double angle = Math.toDegrees(entity.getOrientation()) + 90.0;
		float rot = (float)(angle + bobCycle);		
				
		entity.getMussleFlash().setOn(showFlash);
		
		
		renderBody(canvas, rx, ry, rot, color);
		
		ClientWeapon weapon = this.entity.getWeapon();
		renderWeapon(canvas, weapon, rx, ry, rot);		
	}

	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		Vector2f cameraPos = camera.getPosition();
		
		// TODO: delete
		canvas.drawString(adjustments.toString(), 350, 100, 0xffffffff);
		
		effects.render(canvas,camera, alpha);
		
//		Vector2f pos = entity.getPos();		
//		renderPlayer(canvas, cameraPos, pos, 0);
		
//		Vector2f predictedPos = entity.getPredictedPos();
//		renderPlayer(canvas, cameraPos, predictedPos, 1);
		
		Vector2f renderPos = entity.getRenderPos();
		renderPlayer(canvas, cameraPos, renderPos, 1);
	}
	
	private void setTextureRegion(Sprite sprite, TextureRegion region) {
		sprite.setTexture(region.getTexture());
		sprite.setRegion(region);		
		sprite.setSize(region.getRegionWidth(), region.getRegionHeight());
		sprite.setOrigin(region.getRegionWidth()/2f, region.getRegionHeight()/2f);
		sprite.flip(false, true);		
	}
			
	public static void debugRenderSprite(Canvas canvas, Sprite sprite, int color) {		
//		canvas.drawSprite(sprite, sprite.getX(), sprite.getY(), 0x5f00aa00);				
//		canvas.drawRect( (int)sprite.getX(), (int)sprite.getY(), sprite.getRegionWidth(), sprite.getRegionHeight(), 0x5fff0000);
		
		if(false) 
		{
			canvas.drawSprite(sprite, sprite.getX(), sprite.getY(), Colors.setAlpha(color, 0x5f));				
			canvas.drawRect( (int)sprite.getX(), (int)sprite.getY(), sprite.getRegionWidth(), sprite.getRegionHeight(), Colors.setAlpha(color, 0x5f));
			canvas.fillRect( (int)(sprite.getX() + sprite.getOriginX()), (int)(sprite.getY() + sprite.getOriginY()), 5, 5, Colors.setAlpha(color, 0x5f));//0xff0000ff);
		}
	}

}
