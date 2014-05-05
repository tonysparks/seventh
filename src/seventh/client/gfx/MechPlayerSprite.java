/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.client.ClientMechPlayerEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class MechPlayerSprite implements Renderable {

	private AnimatedImage mechLegs;
	private Sprite mechTorso;
	private float bobScale;
	private float bobTime;	
	private float bobDir;
	
	private ClientMechPlayerEntity mech;
	
	private AnimatedImage railgunFlash;
	private Sprite muzzleFlash;
	
	/**
	 * 
	 */
	public MechPlayerSprite(ClientMechPlayerEntity mech) {
				
		this.mech = mech;
//		this.mechLegs = Art.newAnimatedImage(new int[] {0, 600, 1600, 600, 600, 1600 }, Art.mechLegs);
//		this.mechLegs = Art.newAnimatedImage(new int[] {580, 580 }, new TextureRegion[] { Art.mechLegs[0], Art.mechLegs[1] });
		this.mechLegs = Art.newMechLegs();
		this.mechTorso = new Sprite(Art.mechTorso);		
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
		switch(mech.getCurrentState()) {
			case RUNNING:
			case SPRINTING:
			case WALKING:
				mechLegs.update(timeStep);
				
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
		
		if(mech.isRailgunFiring()) {			
			this.railgunFlash.update(timeStep);
		}
		else {
			this.railgunFlash.reset();
		}
		
		
//		bobScale = ((float)-Math.cos(bobTime)/50.0f) + 0.9f;
		
//		Vector2f pos = mech.getRenderPos();
//		mechTorso.setPosition(pos.x, pos.y);
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		Rectangle bounds = mech.getBounds();
		Vector2f pos = mech.getRenderPos(); 
		Vector2f cameraPos = camera.getPosition();
		
		float rx = (pos.x - cameraPos.x) + (bounds.width/2.0f);
		float ry = (pos.y - cameraPos.y) + (bounds.height/2.0f);
				
		double angle = Math.toDegrees(mech.getOrientation()) + 90.0;		
		float rot = (float)(angle/* + bobCycle*/);
		
		mechTorso.setRotation(rot);
		mechTorso.setPosition(rx-128, ry-128);
		mechTorso.translate(-5, -20);
		mechTorso.setOrigin(132, 148);		
									
//		TextureRegion tex = mechLegs.getCurrentImage();		
//		canvas.drawImage(tex, rx-92, ry-65, 0xffffffff);
		
		TextureRegion tex = mechLegs.getCurrentImage();		
		canvas.drawScaledImage(tex, rx-102, ry-75, tex.getRegionWidth() + 15, tex.getRegionHeight() + 15, 0xffffffff);
		
//		canvas.drawString("Frame: " + mechLegs.getAnimation().getCurrentFrame(), (int)rx, (int)rx, 0xff00ff00);
		
		mechTorso.setScale(bobScale);
		canvas.drawSprite(mechTorso);

		
		if(mech.isRailgunFiring()) {
			TextureRegion tx = railgunFlash.getCurrentImage();
			
			int i = mech.getRandom().nextInt(3);
			//for(int i = 0; i < 3; i++) 
			{
				muzzleFlash.setRegion(tx);			

				float xOffset = -85;
				float yOffset = -106 - (i*7);
				
				muzzleFlash.setSize(tx.getRegionWidth()+16, tx.getRegionHeight()+16);
				muzzleFlash.setPosition(rx, ry);
				muzzleFlash.translate(xOffset, yOffset);
				muzzleFlash.setOrigin( -xOffset, -yOffset);
				muzzleFlash.setRotation(rot+80f);
				
				canvas.drawSprite(muzzleFlash);
			}
		}
	}

}
