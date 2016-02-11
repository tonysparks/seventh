/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.client.ClientPlayer;
import seventh.client.ClientPlayerEntity;
import seventh.client.ClientTank;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class PanzerTankSprite implements Renderable {

	private AnimatedImage tankTracksDamaged;
	private Sprite tankTurretDamaged;
	
	private AnimatedImage tankTracks;
	private Sprite tankTurret;
	private Sprite tankTrack;
	private float bobScale;
	private float bobTime;	
	private float bobDir;

	private ClientTank tank;

	private AnimatedImage railgunFlash;
	private Sprite muzzleFlash;

	/**
	 * 
	 */
	public PanzerTankSprite(ClientTank tank) {

		this.tank = tank;
//		this.tankTracks = Art.newTankTracks();
//		this.tankTurret = new Sprite(Art.tankTurret);
		this.tankTracks = Art.newPanzerTankTracks();
		this.tankTurret = new Sprite(Art.panzerTankTurret);
		
		this.tankTracksDamaged = Art.newPanzerTankTracksDamaged();
		this.tankTurretDamaged = new Sprite(Art.panzerTankTurretDamaged);
		
		this.tankTrack = new Sprite(this.tankTracks.getCurrentImage());
		this.bobDir = 1.0f;
		this.bobScale = 1.0f;

		this.railgunFlash = Art.newThompsonMuzzleFlash();
		this.railgunFlash.loop(true);
		this.muzzleFlash = new Sprite();
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

	private float px, py;
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {		
		Rectangle bounds = tank.getBounds();
		Vector2f pos = tank.getRenderPos(alpha); 
		Vector2f cameraPos = camera.getRenderPosition(alpha);

		boolean isDamaged = true;
		float rx = Math.round((pos.x - cameraPos.x) - (bounds.width/2.0f) + WeaponConstants.TANK_WIDTH/2f);
		float ry = Math.round((pos.y - cameraPos.y) - (bounds.height/2.0f) + WeaponConstants.TANK_HEIGHT/2f);
		
		TextureRegion tex = isDamaged ? tankTracks.getCurrentImage() : tankTracksDamaged.getCurrentImage();		
		tankTrack.setRegion(tex);		
		float trackAngle = (float) (Math.toDegrees(tank.getOrientation()));
		
		
		rx = Math.round((pos.x-40f) - cameraPos.x);
		ry = Math.round((pos.y+40f) - cameraPos.y);
		/*
		rx = Math.round((pos.x + WeaponConstants.TANK_AABB_WIDTH/4f)  - cameraPos.x);
		ry = Math.round((pos.y + WeaponConstants.TANK_AABB_HEIGHT/4f) - cameraPos.y);
		
		rx -= 5;
		ry -= 25;*/
		
//		rx = Math.round((pos.x + WeaponConstants.TANK_WIDTH/2f)  - cameraPos.x);
//		ry = Math.round((pos.y + WeaponConstants.TANK_HEIGHT/2f) - cameraPos.y);
		
		
		tankTrack.setRotation(trackAngle);	
		tankTrack.setOrigin(tex.getRegionWidth()/2f,tex.getRegionHeight()/2f);
		tankTrack.setPosition(rx, ry);
		
		canvas.drawSprite(tankTrack);
//		canvas.fillRect( (int)(rx+tex.getRegionWidth()/2f), (int)(ry+tex.getRegionHeight()/2f), 5, 5, 0xff00ff00);
		
		
		float turretAngle = (float)Math.toDegrees(tank.getTurretOrientation());// + 90.0f;		
		
		/*rx += 0f;
		ry -= 40f;
		
		float originX = 55;
		float originY = 114;*/
		
		rx += 35f;
		ry += 25f;
		float originX = 95f;
		Sprite turretSprite = isDamaged ? tankTurret : tankTurretDamaged;
		float originY = turretSprite.getRegionHeight()/2f;
		
		turretSprite.setRotation(turretAngle);
		turretSprite.setOrigin(originX, originY);
		turretSprite.setPosition(rx,ry);		
		
	//	canvas.fillRect( (int)rx, (int)ry, (int)tankTurret.getRegionWidth(), (int)tankTurret.getRegionHeight(), 0xffff0000);
		canvas.drawSprite(turretSprite);
		
	//	canvas.fillRect( (int)rx, (int)ry, 5, 5, 0xff00ff00);
//		canvas.fillRect( (int)(rx+originX), (int)(ry+originY), 5, 5, 0xff00ff00);

		if(tank.isSecondaryWeaponFiring()) {
			TextureRegion tx = railgunFlash.getCurrentImage();

			int i = tank.getRandom().nextInt(3);
			//for(int i = 0; i < 3; i++) 
			{
				muzzleFlash.setRegion(tx);			

				float xOffset = 70;
				float yOffset = -100;

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
								(int)ry+WeaponConstants.TANK_AABB_HEIGHT+30, player.getTeam().getColor() );			
		}
	}

}
