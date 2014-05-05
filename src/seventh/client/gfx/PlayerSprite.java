/*
 * see license.txt 
 */
package seventh.client.gfx;


import seventh.client.ClientPlayerEntity;
import seventh.client.gfx.Art.Model;
import seventh.client.gfx.particle.Effects;
import seventh.client.weapon.ClientWeapon;
import seventh.game.Entity.State;
import seventh.game.weapons.Weapon;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class PlayerSprite implements Renderable {

	private ClientPlayerEntity entity;
		
	private AnimatedImage walkingAnimation, 
						  bodyAnimation, 
						  bodyReloadAnimation,
						  bodyWeaponSwitch;
	
	private Timer walkingCycle;
	private double bobCycle;
	private int direction;
	
	//private Sprite legsSprite, bodySprite, weaponSprite;
	private Sprite sprite;
	
	private boolean isReloading, isSwitching, isMelee, isFiring;	
	private Effects effects;	

	private long flashTime;
	private boolean showFlash, toggleFlash;
	/**
	 * 
	 */
	public PlayerSprite(ClientPlayerEntity entity, Model bodyModel, Model legsModel) {
		this.entity = entity;
		
		this.effects = new Effects();				
		this.walkingAnimation = newAnimation(legsModel.getFrames());
//		this.bodyAnimation = newAnimation(bodyModel.getFrame(4));
//		this.bodyAnimation = Art.newWalkAnim(bodyModel);
		this.bodyAnimation = newAnimation(bodyModel.getFrame(1));
		this.bodyWeaponSwitch = newAnimation(bodyModel.getFrame(4));
		this.bodyReloadAnimation = //newAnimation(bodyModel.getFrame(0), bodyModel.getFrame(3), bodyModel.getFrame(0));
					Art.newAnimatedImage(new int[] { 850, 650 }, new TextureRegion[] {bodyModel.getFrame(4), bodyModel.getFrame(1)});
		
		this.walkingCycle = new Timer(true, 300);
		this.direction = 1;
				
		this.sprite = new Sprite();
	}
	
	private AnimatedImage newAnimation(TextureRegion ... frames) {
		int[] frameTimes = new int[frames.length];
		for(int i = 0; i < frameTimes.length;i++) {
			frameTimes[i] = 100; // default to 100 msec
		}
		 
		return Art.newAnimatedImage(frameTimes, frames);
				
	}

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.effects.update(timeStep);
		
		State currentState = entity.getCurrentState();
		if(currentState == State.WALKING || 
		   currentState == State.RUNNING ||
		   currentState == State.SPRINTING ) {
			walkingAnimation.update(timeStep);
			walkingCycle.start();
		}
		else {
			//bodyAnimation.reset();
			if(currentState != State.CROUCHING) {
				walkingAnimation.reset();			
			}
			else {
				walkingAnimation.getAnimation().setCurrentFrame(2);			
			}
			
			bobCycle = 0;
			direction = 1;
			walkingCycle.pause();
		}
		
		walkingCycle.update(timeStep);

		if(walkingCycle.isTime()) {
			direction *= -1;			
		}
				
		int max = 5;		
		switch(currentState) {
			case WALKING:
				max = 3;
				break;
			case RUNNING:
				max = 12;
				break;
			case SPRINTING:
				max = 18;
				break;
			default:
				break;
		}
		
		bobCycle += direction*0.8;		
				
		if(bobCycle>max) {
			bobCycle=max;
		}
		else if(bobCycle<-max) {
			bobCycle=-max;
		}
		
		
		ClientWeapon weapon = entity.getWeapon();
		if(weapon != null) {
			this.isReloading = weapon.getState() == seventh.game.weapons.Weapon.State.RELOADING;
			this.isSwitching = weapon.getState() == seventh.game.weapons.Weapon.State.SWITCHING;
			this.isMelee = weapon.getState() == seventh.game.weapons.Weapon.State.MELEE_ATTACK;
			this.isFiring = weapon.getState() == seventh.game.weapons.Weapon.State.FIRING;
			
			if(!this.isReloading) {
				this.bodyReloadAnimation.reset();
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
		
		
		bodyAnimation.update(timeStep);
		bodyReloadAnimation.update(timeStep);	
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
			
			//
			// TODO - clean up magic numbers
			//
						
			TextureRegion gun = weapon.getWeaponImage();
			setTextureRegion(sprite, gun);
			
			sprite.setPosition(rx-12.0f, ry-43.0f);
			sprite.setOrigin(12.0f, 43.0f);			
			if(this.isReloading) {
				sprite.setRotation(rot - 12.0f);
			}
			else if (this.isMelee) {
				//setTextureRegion(sprite, weapon.getWeaponIcon());
				sprite.scale(-0.1f);	
				sprite.setRotation(rot + 15.0f);
			}
			
			canvas.drawSprite(sprite);			
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
					
					float offsetX = 24.0f;
					if(weapon.hasLongBarrel()) {
						offsetX = 32.0f;
					}
										
					sprite.setPosition(rx+offsetX, ry-18f);
					sprite.setOrigin(-offsetX, 18f);		
					sprite.setRotation(rot-80f);
										
					canvas.drawSprite(sprite);
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
		float x = rx - 20f;//offset;
		float y = ry - 32f;//offset;
		
		
		/* Renders the feet
		 */
		{
			sprite.setRotation(rot);
			sprite.setPosition(x, y);
											
			setTextureRegion(sprite, walkingAnimation.getCurrentImage());
					
			sprite.translateY(20f);
			canvas.drawSprite(sprite);
			sprite.translateY(-20f);
		}	
		
		/* scales the body down if crouching
		 */
		if(entity.getCurrentState() == State.CROUCHING) {
			sprite.setScale(0.9f, 0.9f);
		}				
		else {
			sprite.setScale(1f, 1f);
		}
		
		
		/* Renders the body
		 */
		{
			if( this.isReloading || this.isMelee ) {
				setTextureRegion(sprite, bodyReloadAnimation.getCurrentImage());
			}
			else if (this.isSwitching) {
				setTextureRegion(sprite, bodyWeaponSwitch.getCurrentImage());
			}
			else {
				setTextureRegion(sprite, bodyAnimation.getCurrentImage());			
			}
			
			if(color < 1) {
				/* solely for debug information */
				canvas.drawSprite(sprite, sprite.getX(), sprite.getY(), 0x5f00aa00);
			}
			else { 
				canvas.drawSprite(sprite);
			}
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
	static float time = 0.0f;
	private void renderPlayer(Canvas canvas, Vector2f cameraPos, Vector2f pos, int color) {
		Rectangle bounds = entity.getBounds();
		
		float rx = (pos.x - cameraPos.x) + (bounds.width/2.0f);
		float ry = (pos.y - cameraPos.y) + (bounds.height/2.0f);
				
		double angle = Math.toDegrees(entity.getOrientation()) + 90.0;		
		float rot = (float)(angle + bobCycle);				
		
//		showFlash=false;
		if(showFlash) {
			/*canvas.setShader(FireEffectShader.getInstance().
					begin().
					setParam("time", time+=Gdx.graphics.getDeltaTime()).
					end().getShader());*/
			entity.getMussleFlash().setOn(true);
		}
		else {
			entity.getMussleFlash().setOn(false);
		}
		
		
		renderBody(canvas, rx, ry, rot, color);
		
		ClientWeapon weapon = this.entity.getWeapon();
		renderWeapon(canvas, weapon, rx, ry, rot);
		
		if(showFlash) {
			//canvas.setShader(null);
		}
	}

	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		Vector2f cameraPos = camera.getPosition();
		
		
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

}
