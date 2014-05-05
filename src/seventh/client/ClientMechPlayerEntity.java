/*
 * see license.txt 
 */
package seventh.client;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.MechPlayerSprite;
import seventh.game.net.NetWeapon;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class ClientMechPlayerEntity extends ClientPlayerEntity {

	private MechPlayerSprite sprite;
	private boolean isRailgunFiring;
	
	
	/**
	 * @param game
	 * @param pos
	 */
	public ClientMechPlayerEntity(ClientGame game, ClientPlayer player, Vector2f pos) {
		super(game, player, pos);
		
		this.bounds.width = WeaponConstants.MECH_WIDTH;
		this.bounds.height = WeaponConstants.MECH_WIDTH;
		
		this.lineOfSite = WeaponConstants.MECH_DEFAULT_LINE_OF_SIGHT;

		sprite = new MechPlayerSprite(this);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientPlayerEntity#updateWeaponState(seventh.game.net.NetWeapon, long)
	 */
	@Override
	protected void updateWeaponState(NetWeapon netWeapon, long time) {
		if(netWeapon != null) {
			this.isRailgunFiring = netWeapon.state == Weapon.State.FIRING.netValue();
		}
	}
	
	/**
	 * @return true if the railgun is currently firing
	 */
	public boolean isRailgunFiring() {
		return this.isRailgunFiring;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientPlayerEntity#isMech()
	 */
	@Override
	public boolean isMech() {	
		return true;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientPlayerEntity#calculateMovementSpeed()
	 */
	@Override
	protected int calculateMovementSpeed() {	
		return WeaponConstants.MECH_MOVEMENT_SPEED;
	}

	/* (non-Javadoc)
	 * @see seventh.client.ClientPlayerEntity#onDamage()
	 */
	@Override
	protected void onDamage() {		
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientPlayerEntity#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
		sprite.update(timeStep);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		this.sprite.render(canvas, camera, alpha);
	}

}
