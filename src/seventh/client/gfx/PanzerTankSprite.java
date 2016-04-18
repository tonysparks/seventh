/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.ClientTank;

/**
 * @author Tony
 *
 */
public class PanzerTankSprite extends TankSprite {


	/**
	 * @param tank
	 */
	public PanzerTankSprite(ClientTank tank) {
		super(tank, Art.newPanzerTankTracks(), new Sprite(Art.panzerTankTurret), 
				    Art.newPanzerTankTracksDamaged(), new Sprite(Art.panzerTankTurretDamaged));
	}

	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.TankSprite#renderTankBase(float, float, float, seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
	 */
	@Override
	protected void renderTankBase(float rx, float ry, float trackAngle, Canvas canvas, Camera camera, float alpha) {
		
//		rx += 55f;
//		ry -= 40f;
//		
		
		rx += 10f; //15
		ry -= 28f; // 25
		
		TextureRegion tex = isDestroyed() ? tankTracksDamaged.getCurrentImage() : tankTracks.getCurrentImage();		
		tankTrack.setRegion(tex);		
		
		tankTrack.setRotation(trackAngle);	
		tankTrack.setOrigin(tex.getRegionWidth()/2f,tex.getRegionHeight()/2f-5f);
		tankTrack.setPosition(rx, ry);
		
		canvas.drawSprite(tankTrack);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.TankSprite#renderTurret(float, float, float, seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
	 */
	@Override
	protected void renderTurret(float rx, float ry, float turretAngle, Canvas canvas, Camera camera, float alpha) {
//		rx += 35f;
//		ry += 25f;
		

		rx += 15f;
		ry -= 25f;
		
		rx += 43f;
		ry += 25f;
		
		float originX = 89f;		
		Sprite turretSprite = isDestroyed() ? tankTurretDamaged : tankTurret;
		float originY = 65f;//turretSprite.getRegionHeight()/2f;
				
		turretSprite.setRotation(turretAngle);
		turretSprite.setOrigin(originX, originY);
		turretSprite.setPosition(rx,ry);		
		
		canvas.drawSprite(turretSprite);
	}		
}
