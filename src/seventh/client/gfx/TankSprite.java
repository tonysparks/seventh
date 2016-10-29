/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.ClientPlayer;
import seventh.client.entities.ClientPlayerEntity;
import seventh.client.entities.vehicles.ClientTank;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class TankSprite implements Renderable {

	protected AnimatedImage tankTracksDamaged;
	protected Sprite tankTurretDamaged;
	
	protected AnimatedImage tankTracks;
	protected Sprite tankTurret;
	protected Sprite tankTrack;
	private float bobScale;
	private float bobTime;	
	private float bobDir;

	protected ClientTank tank;

	protected AnimatedImage railgunFlash;
	protected Sprite muzzleFlash;
	
	private boolean isDestroyed;

	/**
	 * @param tank
	 * @param tankTracks
	 * @param tankTurret
	 * @param damagedTankTracks
	 * @param damagedTankTurret
	 */
	protected TankSprite(ClientTank tank, AnimatedImage tankTracks, Sprite tankTurret, AnimatedImage damagedTankTracks, Sprite damagedTankTurret) {

		this.tank = tank;
		this.tankTracks = tankTracks;
		this.tankTurret = tankTurret;
		
		this.tankTracksDamaged = damagedTankTracks;
		this.tankTurretDamaged = damagedTankTurret;
		
		this.tankTrack = new Sprite(this.tankTracks.getCurrentImage());
		this.bobDir = 1.0f;
		this.bobScale = 1.0f;

		this.railgunFlash = Art.newThompsonMuzzleFlash();
		this.railgunFlash.loop(true);
		this.muzzleFlash = new Sprite();
		
		this.isDestroyed = false;
	}

	/**
	 * @param isDestroyed the isDestroyed to set
	 */
	public void setDestroyed(boolean isDestroyed) {
		this.isDestroyed = isDestroyed;
	}
	
	/**
	 * @return the isDestroyed
	 */
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		switch(tank.getCurrentState()) {
			case RUNNING:
			case SPRINTING:
			case WALKING:
				tankTracks.update(timeStep);

				bobTime += timeStep.getDeltaTime();//timeStep.asFraction();
				if(bobTime > 1200) {

					bobDir *= -1f;
					bobTime = 0;

//					if(bobDir < 0) {
//						bobScale = 0.98f;
//					}
//					else {
//						bobScale = 1.0f;	
//					}

				}
				else {
					bobScale += 0.001f * bobDir;
					if(bobScale < 0.98f) {
						bobScale = 0.98f;
					}
					else if(bobScale > 1.0f) {
						bobScale = 1f;
					}
				}
				break;
			default: {				
				bobScale = 1.0f;
			}
		}

		if(tank.isSecondaryWeaponFiring()) {			
			this.railgunFlash.update(timeStep);
		}
		else {
			this.railgunFlash.reset();
		}


//		bobScale = ((float)-Math.cos(bobTime)/50.0f) + 0.9f;

//		Vector2f pos = mech.getRenderPos();
//		mechTorso.setPosition(pos.x, pos.y);
	}

	protected void renderTankBase(float rx, float ry, float trackAngle, Canvas canvas, Camera camera, float alpha) {
		rx += 0f;
		ry -= 15f;
		
		TextureRegion tex = isDestroyed ? tankTracksDamaged.getCurrentImage() : tankTracks.getCurrentImage();		
		tankTrack.setRegion(tex);		
		
		tankTrack.setRotation(trackAngle);	
		tankTrack.setOrigin(tex.getRegionWidth()/2f,tex.getRegionHeight()/2f);
		tankTrack.setPosition(rx, ry);
		
		canvas.drawSprite(tankTrack);
	}
	
	/**
	 * Allow the implementing tank to adjust some of the rendering properties
	 * 
	 * @param rx
	 * @param ry
	 * @param turretAngle
	 * @param canvas
	 * @param camera
	 * @param alpha
	 */
	protected void renderTurret(float rx, float ry, float turretAngle, Canvas canvas, Camera camera, float alpha) {
		rx += 0f;
		ry -= 15f;
		
		rx += 70f;
		ry += 15f;
		float originX = 62f;
		
		Sprite turretSprite = isDestroyed ? tankTurretDamaged : tankTurret;
		float originY = turretSprite.getRegionHeight()/2f;
		
		turretSprite.setRotation(turretAngle);
		turretSprite.setOrigin(originX, originY);
		turretSprite.setPosition(rx,ry);		
		
		canvas.drawSprite(turretSprite);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {		
		Rectangle bounds = tank.getBounds();
		Vector2f pos = tank.getRenderPos(alpha); 
		Vector2f cameraPos = camera.getRenderPosition(alpha);

		float rx = Math.round((pos.x - cameraPos.x) - (bounds.width/2.0f) + WeaponConstants.TANK_WIDTH/2f);
		float ry = Math.round((pos.y - cameraPos.y) - (bounds.height/2.0f) + WeaponConstants.TANK_HEIGHT/2f);				
		
		rx = Math.round((pos.x+15f) - cameraPos.x);
		ry = Math.round((pos.y+90f) - cameraPos.y);
		
		float trackAngle = (float) (Math.toDegrees(tank.getOrientation()));
		
		renderTankBase(rx, ry, trackAngle, canvas, camera, alpha);
		
		float turretAngle = (float)Math.toDegrees(tank.getTurretOrientation());
		
		renderTurret(rx, ry, turretAngle, canvas, camera, alpha);
		
		if(tank.isSecondaryWeaponFiring()) {
			TextureRegion tx = railgunFlash.getCurrentImage();

			//int i = tank.getRandom().nextInt(3);
			//for(int i = 0; i < 3; i++) 
			{
				muzzleFlash.setRegion(tx);			

				//float xOffset = 70;
				//float yOffset = -100;

				muzzleFlash.setSize(tx.getRegionWidth()+16, tx.getRegionHeight()+16);
				muzzleFlash.setPosition(rx, ry);
				muzzleFlash.translate(0, 0);
//				muzzleFlash.setOrigin(12);
				muzzleFlash.setRotation(turretAngle+80f);

//				canvas.drawSprite(muzzleFlash);
			}
		}
		
		if(tank.hasOperator()) {
			canvas.setFont("Consola", 14);
			canvas.boldFont();
			ClientPlayerEntity ent = tank.getOperator();
			ClientPlayer player = ent.getPlayer();
			int strln = canvas.getWidth(player.getName());
			RenderFont.drawShadedString(canvas, player.getName(), (int)(rx+WeaponConstants.TANK_AABB_WIDTH/2)-strln*2, 
								(int)ry+WeaponConstants.TANK_AABB_HEIGHT-100, player.getTeam().getColor() );			
		}
	}

}
