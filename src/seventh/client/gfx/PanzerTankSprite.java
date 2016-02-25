/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.g2d.Sprite;

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
	 * @see seventh.client.gfx.TankSprite#renderTurret(float, float, float, seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float)
	 */
	@Override
	protected void renderTurret(float rx, float ry, float turretAngle, Canvas canvas, Camera camera, float alpha) {
//		rx += 35f;
//		ry += 25f;
		
		rx += 35f;
		ry += 40f;
		
		float originX = 95f;
		boolean isDamaged = false;
		Sprite turretSprite = isDamaged ? tankTurretDamaged : tankTurret;
		float originY = turretSprite.getRegionHeight()/2f;
		
		turretSprite.setRotation(turretAngle);
		turretSprite.setOrigin(originX, originY);
		turretSprite.setPosition(rx,ry);		
		
		canvas.drawSprite(turretSprite);
	}		
}
