/*
 * see license.txt 
 */
package seventh.client.gfx;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.ClientPlayer;
import seventh.client.ClientPlayerEntity;
import seventh.client.gfx.Art.Model;
import seventh.client.gfx.particle.Effects;
import seventh.client.weapon.ClientRocketLauncher;
import seventh.client.weapon.ClientWeapon;
import seventh.game.entities.Entity.State;
import seventh.game.weapons.Weapon;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * Renders the Player
 * 
 * @author Tony
 *
 */
public class PlayerSprite implements Renderable {

	private static class Motion implements Updatable {
		public double value;
		public double velocity;
		public double max;
		
		public int direction;
		{
			clear();
		}
		
		@Override
		public void update(TimeStep timeStep) {
			value += direction*velocity;		
			
			if(value>max) {
				value=max;
				direction *= -1;
			}
			else if(value<-max) {
				value=-max;
				direction *= -1;
			}
		}

		
		public void set(double maxValue, double velocity) {
			this.max = maxValue;
			this.velocity = velocity;
		}
		
		public void clear() {
			this.value = 0;
			this.direction = 1;
		}
		
	}
	
	private ClientPlayerEntity entity;
	
	private final AnimatedImage idleBody, 
						  crouchBody,
						  walkBody, 
						  runBody, 
						  sprintBody, 
						  reloadBody,
						  idlePumpActionReloadBody,
						  crouchPumpActionReloadBody,
						  walkPumpActionReloadBody,
						  runPumpActionReloadBody,
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
	
		
	private Motion swayMotion, bobMotion;
	private float xOffset, yOffset, prevXOffset, prevYOffset;
	
	private boolean isReloading, 
					isSwitching, 
					isMelee, 
					isFiring,
					isPumpAction;
	
	
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

	// TEMP Remove this once we have the proper adjustments made
	Adjustments adjustments=new Adjustments();
	
	/**
	 * @param player
	 * @param bodyModel
	 * @param walkLegsModel
	 * @param crouchLegsModel
	 * @param sprintLegsModel
	 */
	public PlayerSprite(ClientPlayer player, 
						Model bodyModel, 
						Model walkLegsModel,
						TextureRegion crouchLegsModel,
						Model sprintLegsModel) {
		
		idleBody = newAnimation(100, bodyModel.getFrame(0));
		crouchBody = newAnimation(100, bodyModel.getFrame(3));
		walkBody = newAnimation(100, bodyModel.getFrame(2));
		runBody = newAnimation(100, bodyModel.getFrame(0)); 
		sprintBody = newAnimation(100, bodyModel.getFrame(1));
		reloadBody = newAnimation(500, bodyModel.getFrame(5), bodyModel.getFrame(0));		
		switchWeaponBody = newAnimation(100, bodyModel.getFrame(5));
		meleeBody = newAnimation(100, bodyModel.getFrame(1));
		
		idlePumpActionReloadBody = newAnimation(200, bodyModel.getFrame(6));
		crouchPumpActionReloadBody = newAnimation(200, bodyModel.getFrame(7));
		walkPumpActionReloadBody = newAnimation(200, bodyModel.getFrame(8));
		runPumpActionReloadBody = newAnimation(200, bodyModel.getFrame(6));
				
		idleLegsAnimation = newAnimation(150, walkLegsModel.getFrame(0));
		crouchingLegsAnimation = newAnimation(0, crouchLegsModel);
		walkLegsAnimation = newAnimation(150, walkLegsModel.getFrames());
		runLegsAnimation = newAnimation(100, walkLegsModel.getFrames()); 
		sprintLegsAnimation = newAnimation(130, sprintLegsModel.getFrames()); // 150
		
		activeBodyPosition = idleBody; 
		activeLegsAnimation = idleLegsAnimation;
				
		bobMotion = new Motion();
		swayMotion = new Motion();
		
		effects = new Effects();
		sprite = new Sprite();
		
		reset(player);
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
				
		bobMotion.clear();
		swayMotion.clear();
	}
	
	/**
	 * Resets this {@link PlayerSprite} for reuse by another
	 * {@link ClientPlayerEntity}.  Call this when the player is respawned.
	 * 
	 * @param player
	 */
	public void reset(ClientPlayer player) {
		entity = player.getEntity();
		
		activeBodyPosition = idleBody; 
		activeLegsAnimation = idleLegsAnimation;
		
		resetLegMovements();
		effects.clearEffects();
	}

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		
		// TODO: delete
		adjustments.poll();
		
		effects.update(timeStep);
		
		prevXOffset = xOffset;
		prevYOffset = yOffset;
		xOffset = yOffset = 0;
		
		this.isPumpAction = false;
		
		float weaponWeight = 1.0f;
		ClientWeapon weapon = entity.getWeapon();		
		if(weapon != null) {
			weaponWeight = 100.0f - weapon.getWeaponWeight();
			weaponWeight *= 0.01f;
			
			
			if(weapon.isPumpAction()) {
				this.isPumpAction = weapon.isSpecialReloadingAction();
			}			
		}
		
		activeBodyPosition = idleBody; 
		activeLegsAnimation = idleLegsAnimation;
		
		Vector2f dir = entity.getFacing();
		State currentState = entity.getCurrentState();
		
		switch(currentState) {
		case IDLE:
			activeBodyPosition = isPumpAction ? idlePumpActionReloadBody : idleBody;
			activeLegsAnimation = idleLegsAnimation;
			resetLegMovements();
			break;
		case CROUCHING:
			activeBodyPosition = isPumpAction ? crouchPumpActionReloadBody : crouchBody;
			activeLegsAnimation = crouchingLegsAnimation;
			resetLegMovements();
			break;
		case WALKING: {						
			activeBodyPosition = isPumpAction ? walkPumpActionReloadBody : walkBody;
			activeLegsAnimation = walkLegsAnimation;
			
			bobMotion.set(5, 0.6);
			swayMotion.set(4, 1.55*weaponWeight);
			
			xOffset += (dir.y * swayMotion.direction) * 0.815f;
			yOffset += (dir.x * swayMotion.direction) * 0.815f;
			
		} break;
		case RUNNING: {
			activeBodyPosition = isPumpAction ? runPumpActionReloadBody : runBody;
			activeLegsAnimation = runLegsAnimation;
						
			bobMotion.set(12*weaponWeight,2.4*weaponWeight); //(8, 1.4);
			swayMotion.set(4, 2.5*weaponWeight);			
			xOffset += (dir.y * swayMotion.direction) * 0.755f;
			yOffset += (dir.x * swayMotion.direction) * 0.755f;
		} break;
		case SPRINTING:
			activeBodyPosition = sprintBody;
			activeLegsAnimation = sprintLegsAnimation;

			bobMotion.set(0, 0);
			swayMotion.set(4, 3.25*weaponWeight);
			
			xOffset += (dir.y * swayMotion.direction) * 1.25f;
			yOffset += (dir.x * swayMotion.direction) * 1.25f;
			break;
		case DEAD:
			resetLegMovements();
			break;
		default:
			resetLegMovements();				
		}

		bobMotion.update(timeStep);
		swayMotion.update(timeStep);
		
		if(weapon != null) {
			
			this.isReloading = weapon.getState() == seventh.game.weapons.Weapon.State.RELOADING;
			this.isSwitching = weapon.getState() == seventh.game.weapons.Weapon.State.SWITCHING;
			this.isMelee = weapon.getState() == seventh.game.weapons.Weapon.State.MELEE_ATTACK;
			this.isFiring = weapon.getState() == seventh.game.weapons.Weapon.State.FIRING;
			
			if(weapon.isBoltAction()) {
				if(!this.isReloading && weapon.isSpecialReloadingAction()) {
					this.isReloading = true;
				}
			}
						
			if( this.isReloading /*&& !this.isPumpAction*/) {
				this.activeBodyPosition = reloadBody;
			}
			else if (this.isMelee) {
				this.activeBodyPosition =  meleeBody;
			}
			else if (this.isSwitching) {
				this.activeBodyPosition = switchWeaponBody;
			}
						
			if(weapon instanceof ClientRocketLauncher && !this.isMelee) {
				activeBodyPosition = crouchBody;
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
		
		if( entity.isAlive() && (entity.getLastUpdate()+500) > timeStep.getGameClock()) {
			entity.getMussleFlash().setOn(showFlash);
		}
		else {
			entity.getMussleFlash().setOn(false);
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
			
			boolean isRocketLauncher = weapon instanceof ClientRocketLauncher;
			
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
			
			if(isRocketLauncher) {
				// TODO: Crouching
				sprite.setRotation(rot - 7);
				//sprite.setPosition(rx+3f, ry-31f);
				//sprite.setOrigin(-2f, 33f);
				sprite.setPosition(rx+0f, ry-28f);
				sprite.setOrigin(-0f, 28f);
				
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
				sprite.setScale(0.87f, 0.87f);
				sprite.setRotation(rot);
				sprite.setPosition(x, y);
				
				setTextureRegion(sprite, activeLegsAnimation.getCurrentImage());
										
				Vector2f facing = entity.getFacing();				
				float fx = (facing.y-9.0f);
				float fy = -(facing.x+5);
				
				sprite.translate(fx, fy);
				canvas.drawSprite(sprite);
				sprite.translate(-fx, -fy);
				sprite.setScale(0.8f, 0.8f);
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
				
		/* Renders the body
		 */
		{

			setTextureRegion(sprite, activeBodyPosition.getCurrentImage());		
			canvas.drawRawSprite(sprite);				
			
			
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
	private void renderPlayer(Canvas canvas, Vector2f cameraPos, Vector2f pos, int color, float alpha) {
		Rectangle bounds = entity.getBounds();
		//xOffset=yOffset=0;
		float nx = xOffset + ((prevXOffset - xOffset) * alpha);
		float ny = yOffset + ((prevYOffset - yOffset) * alpha);
		
		float rx = (pos.x - cameraPos.x) + (bounds.width/2.0f) + nx;
		float ry = (pos.y - cameraPos.y) + (bounds.height/2.0f) + ny;
				
		double angle = Math.toDegrees(entity.getOrientation()) + 90.0;
		float rot = (float)(angle + bobMotion.value);		
				
		renderBody(canvas, rx, ry, rot, color);
		
		ClientWeapon weapon = this.entity.getWeapon();
		renderWeapon(canvas, weapon, rx, ry, rot);		
	}

	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		
		// TODO: delete
		//canvas.drawString(adjustments.toString(), 350, 100, 0xffffffff);
		
		effects.render(canvas,camera, alpha);
				
		Vector2f renderPos = entity.getRenderPos(alpha);
		renderPlayer(canvas, cameraPos, renderPos, 0xffffffff, alpha);		
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
		boolean drawDebug=false;
		if(drawDebug) 
		{
			canvas.drawSprite(sprite, sprite.getX(), sprite.getY(), Colors.setAlpha(color, 0x5f));				
			canvas.drawRect( (int)sprite.getX(), (int)sprite.getY(), sprite.getRegionWidth(), sprite.getRegionHeight(), Colors.setAlpha(color, 0x5f));
			canvas.fillRect( (int)(sprite.getX() + sprite.getOriginX()), (int)(sprite.getY() + sprite.getOriginY()), 5, 5, Colors.setAlpha(color, 0x5f));//0xff0000ff);
		}
	}

}
