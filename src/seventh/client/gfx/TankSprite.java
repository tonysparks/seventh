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

		
		float rx = Math.round((pos.x - cameraPos.x) - (bounds.width/1.0f));
		float ry = Math.round((pos.y - cameraPos.y) - (bounds.height/1.0f));

		TextureRegion tex = tankTracks.getCurrentImage();		
		tankTrack.setRegion(tex);
		float trackAngle = (float) (Math.toDegrees(tank.getOrientation())) - 90f;
		
		tankTrack.setRotation(trackAngle);
		tankTrack.setPosition(rx, ry);
		tankTrack.setOrigin(tex.getRegionWidth()/2f,tex.getRegionHeight()/2f);
		tankTrack.translate(-20, -25);
		canvas.drawSprite(tankTrack);

		
		float turretAngle = (float)Math.toDegrees(tank.getTurretOrientation()) + 90.0f;		
		
		tankTurret.setRotation(turretAngle);		
		tankTurret.setPosition(rx-10f,ry-10f);
		tankTurret.translate(-10f, -15f);
		tankTurret.setOrigin(tankTurret.getWidth()/2f,tankTurret.getHeight()/2f);		
		canvas.drawSprite(tankTurret);


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
			RenderFont.drawShadedString(canvas, player.getName(), (int)rx + (bounds.width), (int)ry + (bounds.height) + 100, player.getTeam().getColor() );			
		}
	}

}
