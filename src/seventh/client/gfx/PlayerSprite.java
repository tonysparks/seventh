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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class PlayerSprite implements Renderable {

	private ClientPlayerEntity entity;
		
//	private AnimatedImage walkingAnimation, 
//						  bodyAnimation, 
//						  bodyReloadAnimation,
//						  bodyWeaponSwitch;
	
	private final AnimatedImage idleBody, 
						  crouchBody,
						  walkBody, 
						  runBody, 
						  sprintBody, 
						  reloadBody;
	
		
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
	
	//private Sprite legsSprite, bodySprite, weaponSprite;
	private Sprite sprite;
	
	private boolean isReloading, isSwitching, isMelee, isFiring;	
	private Effects effects;	

	private long flashTime;
	private boolean showFlash, toggleFlash;
	
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
	public PlayerSprite(ClientPlayerEntity entity, Model bodyModel, Model legsModel) {
		this.entity = entity;
		
		this.effects = new Effects();				
		
		Model alliedBody = Art.alliedPositionModel;
		idleBody = newAnimation(100, alliedBody.getFrame(0));
		crouchBody = newAnimation(100, alliedBody.getFrame(3));
		walkBody = newAnimation(100, alliedBody.getFrame(2));
		runBody = newAnimation(100, alliedBody.getFrame(0)); 
		sprintBody = newAnimation(100, alliedBody.getFrame(1));
		reloadBody = newAnimation(100, alliedBody.getFrame(5));
		
		Model alliedWalkLegs = Art.alliedWalkModel;
		idleLegsAnimation = newAnimation(150, alliedWalkLegs.getFrame(0));
		crouchingLegsAnimation = newAnimation(150, Art.alliedCrouchLegs);
		walkLegsAnimation = newAnimation(150, alliedWalkLegs.getFrames());
		runLegsAnimation = newAnimation(100, alliedWalkLegs.getFrames()); 
		sprintLegsAnimation = newAnimation(190, Art.alliedSprintModel.getFrames()); // 150
		
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
		Vector2f dir = entity.getFacing();
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
			
			if(!this.isReloading) {
//				this.bodyReloadAnimation.reset();
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
//		bodyAnimation.update(timeStep);
//		bodyReloadAnimation.update(timeStep);	
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
			
			sprite.setScale(1.0f);
			
			switch(this.entity.getCurrentState()) {
				case SPRINTING: {
					sprite.setPosition(rx-12.0f, ry-43.0f);
					sprite.setOrigin(12.0f, 43.0f);
					sprite.setRotation(rot - 60.0f);
					
					sprite.setPosition(rx-adjustments.xOffset, ry-adjustments.yOffset);
					sprite.setOrigin(adjustments.x, adjustments.y);
					
					canvas.drawString(adjustments.toString(), 350, 100, 0xffffffff);
					
					
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

					if(this.isReloading) {
						sprite.setRotation(rot - 12.0f);
					}
					else if (this.isMelee) {
						sprite.scale(-0.1f);	
						sprite.setRotation(rot + 15.0f);
					}
				}
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
		float x = rx - 32f;//offset;
		float y = ry - 42f;//offset;
		
		State currentState = this.entity.getCurrentState();
		
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
				
				/* set the scale for the body */
	//			Vector2f m = entity.getMovementDir();
	//			double angle = Vector2f.Vector2fAngle(m, Geom.RIGHT_VECTOR);
				
				sprite.setScale(0.8f, 0.8f);
				Vector2f facing = entity.getFacing();
				sprite.translate(facing.x * 6.0f, facing.y * 6.0f);
				break;
			}
			case SPRINTING: {
				sprite.setRotation(rot);
				sprite.setPosition(x, y);
				//sprite.setScale(.72f, .72f);
				
				setTextureRegion(sprite, activeLegsAnimation.getCurrentImage());
						
				sprite.translate(-6f, -6);
				canvas.drawSprite(sprite);
				sprite.translate(6f, 6);
				
				sprite.setRotation(rot);
				sprite.setPosition(x, y);
				
				/* set the scale for the body */
				sprite.setScale(.8f, .8f);
				break;
			}
			default: {
				sprite.setRotation(rot);
				sprite.setPosition(x, y);
								
				setTextureRegion(sprite, activeLegsAnimation.getCurrentImage());
						
				sprite.translate(-12f, -5);
				canvas.drawSprite(sprite);
				sprite.translate(12f, 5);
				
				/* set the scale for the body */
				sprite.setScale(.8f, .8f);
			}
		}
		
		/* Renders the body
		 */
		{
//			if( this.isReloading || this.isMelee ) {
//				setTextureRegion(sprite, bodyReloadAnimation.getCurrentImage());
//			}
//			else if (this.isSwitching) {
//				setTextureRegion(sprite, bodyWeaponSwitch.getCurrentImage());
//			}
//			else {
//				setTextureRegion(sprite, bodyAnimation.getCurrentImage());			
//			}
			
			setTextureRegion(sprite, activeBodyPosition.getCurrentImage());
			
			if(color < 1) {
				/* solely for debug information */
				canvas.drawSprite(sprite, sprite.getX(), sprite.getY(), 0x5f00aa00);
			}
			else { 
				//sprite.translateX(5f);
				canvas.drawSprite(sprite);
				//sprite.translateX(-5f);
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
	private void renderPlayer(Canvas canvas, Vector2f cameraPos, Vector2f pos, int color) {
		Rectangle bounds = entity.getBounds();
		
		float rx = (pos.x - cameraPos.x) + (bounds.width/2.0f) + xOffset;
		float ry = (pos.y - cameraPos.y) + (bounds.height/2.0f) + yOffset;
				
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
