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
public class TankSprite implements Renderable {

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
	public TankSprite(ClientTank tank) {

		this.tank = tank;
//		this.mechLegs = Art.newAnimatedImage(new int[] {0, 600, 1600, 600, 600, 1600 }, Art.mechLegs);
//		this.mechLegs = Art.newAnimatedImage(new int[] {580, 580 }, new TextureRegion[] { Art.mechLegs[0], Art.mechLegs[1] });
		this.tankTracks = Art.newTankTracks();
		this.tankTurret = new Sprite(Art.tankTurret);
		this.tankTurret.flip(true, true);
		
		this.tankTrack = new Sprite(this.tankTracks.getCurrentImage());
//		this.mechTorso.flip(false, true);
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
	public void render(Canvas canvas, Camera camera, long alpha) {
		Rectangle bounds = tank.getBounds();
		Vector2f pos = tank.getRenderPos(); 
		Vector2f cameraPos = camera.getPosition();

		
		float rx = Math.round((pos.x - cameraPos.x) - (bounds.width/2.0f) + WeaponConstants.TANK_WIDTH/2f);
		float ry = Math.round((pos.y - cameraPos.y) - (bounds.height/2.0f) + WeaponConstants.TANK_HEIGHT/2f);
		
		TextureRegion tex = tankTracks.getCurrentImage();		
		tankTrack.setRegion(tex);
		float trackAngle = (float) (Math.toDegrees(tank.getOrientation())) - 90f;
		
		
		rx = Math.round((pos.x + WeaponConstants.TANK_AABB_WIDTH/4f)  - cameraPos.x);
		ry = Math.round((pos.y + WeaponConstants.TANK_AABB_HEIGHT/4f) - cameraPos.y);
		
		rx -= 5;
		ry -= 25;
		
//		rx = Math.round((pos.x + WeaponConstants.TANK_WIDTH/2f)  - cameraPos.x);
//		ry = Math.round((pos.y + WeaponConstants.TANK_HEIGHT/2f) - cameraPos.y);
		
		
		tankTrack.setRotation(trackAngle);	
		tankTrack.setOrigin(tex.getRegionWidth()/2f,tex.getRegionHeight()/2f);
		tankTrack.setPosition(rx, ry);
						
		canvas.drawSprite(tankTrack);
//		canvas.fillRect( (int)(rx+tex.getRegionWidth()/2f), (int)(ry+tex.getRegionHeight()/2f), 5, 5, 0xff00ff00);
		
		
		float turretAngle = (float)Math.toDegrees(tank.getTurretOrientation()) + 90.0f;		
		
		rx += 0f;
		ry -= 40f;
		
		float originX = 55;
		float originY = 114;
		
		tankTurret.setRotation(turretAngle);
		tankTurret.setOrigin(originX, originY);
		tankTurret.setPosition(rx,ry);
		
	//	canvas.fillRect( (int)rx, (int)ry, (int)tankTurret.getRegionWidth(), (int)tankTurret.getRegionHeight(), 0xffff0000);
		canvas.drawSprite(tankTurret);
		
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
