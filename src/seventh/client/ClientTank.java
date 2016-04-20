/*
 * The Seventh
 * see license.txt 
 */
package seventh.client;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.TankSprite;
import seventh.client.gfx.particle.Emitter;
import seventh.client.gfx.particle.RenderableEffect;
import seventh.client.gfx.particle.RocketTrailEmitter;
import seventh.game.Entity.State;
import seventh.game.net.NetEntity;
import seventh.game.net.NetTank;
import seventh.game.weapons.Weapon;
import seventh.map.Map;
import seventh.math.OBB;
import seventh.math.Vector2f;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * The Client representation of a Tank
 * 
 * @author Tony
 *
 */
public class ClientTank extends ClientVehicle {

	private TankSprite tankSprite;	
	private float turretOrientation;
	private Vector2f turretFacing;
	
	private Weapon.State primaryWeaponState;
	private Weapon.State secondaryWeaponState;
	
	private ClientGameEffects effects;
	private Vector2f previousTrackMark;
	private Vector2f trackMarkOffset;
	private OBB vehicleOOB;
	
	private boolean removeGfx;
	
	/**
	 * @param pos
	 */
	protected ClientTank(ClientGame game, Vector2f pos) {
		super(game, pos);	
		
		this.effects = game.getGameEffects();
		
		this.lineOfSight = WeaponConstants.TANK_DEFAULT_LINE_OF_SIGHT;

		this.bounds.width = WeaponConstants.TANK_AABB_WIDTH;
		this.bounds.height = WeaponConstants.TANK_AABB_HEIGHT;
		
				
		this.previousTrackMark = new Vector2f();
		this.trackMarkOffset = new Vector2f();
		this.turretFacing = new Vector2f();
		
		this.vehicleOOB = new OBB();
		this.vehicleOOB.setBounds(WeaponConstants.TANK_WIDTH, WeaponConstants.TANK_HEIGHT);
		
		setOnRemove(new OnRemove() {
			
			@Override
			public void onRemove(ClientEntity me, ClientGame game) {
				removeGfx = true;
			}
		});
	}
	
	/**
	 * @return if we should remove the destroyed graphics
	 */
	private boolean removeGfx() {
		return this.removeGfx;
	}
	
	/**
	 * @param tankSprite the tankSprite to set
	 */
	public void setTankSprite(TankSprite tankSprite) {
		this.tankSprite = tankSprite;
	}
	
	/**
	 * @return the tankSprite
	 */
	public TankSprite getTankSprite() {
		return tankSprite;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#updateState(seventh.game.net.NetEntity, long)
	 */
	@Override
	public void updateState(NetEntity state, long time) {	
		super.updateState(state, time);
		
		NetTank netTank = (NetTank)state;
		this.currentState = State.fromNetValue(netTank.state);
		
		if(this.currentState==State.DESTROYED) {
			TankSprite tankSprite = getTankSprite();
			if(tankSprite != null&&!tankSprite.isDestroyed()) {
				tankSprite.setDestroyed(true);
								
				game.addForegroundEffect(new RenderableEffect(tankSprite) {					
					@Override
					public boolean isDone() {
						return removeGfx();
					}
				});
				Emitter rocketTrail = new RocketTrailEmitter(getCenterPos(), 40_000, 0) {					
					@Override
					public boolean isDone() {
						return !this.isAlive() || removeGfx();
					}
				};				
				rocketTrail.start();
				game.addForegroundEffect(rocketTrail);
			}
		}
		
		this.orientation = (float)Math.toRadians(netTank.orientation);
		this.turretOrientation = (float)Math.toRadians(netTank.turretOrientation);
		
		this.turretFacing.set(1,0);
		Vector2f.Vector2fRotate(this.turretFacing, this.turretOrientation, this.turretFacing);
		this.facing.set(this.turretFacing); // TODO - should we be overiding this like this?
		
		this.primaryWeaponState = Weapon.State.fromNet(netTank.primaryWeaponState);
		this.secondaryWeaponState = Weapon.State.fromNet(netTank.secondaryWeaponState);

		Vector2f center = new Vector2f(getPos());
		center.x += WeaponConstants.TANK_AABB_WIDTH/2f;
		center.y += WeaponConstants.TANK_AABB_HEIGHT/2f;
		this.vehicleOOB.update(orientation, center);
		
		if(netTank.operatorId != SeventhConstants.INVALID_PLAYER_ID) {
			ClientPlayer clientPlayer = game.getPlayers().getPlayer(netTank.operatorId);
			if(clientPlayer!=null) {
				setOperator(clientPlayer.getEntity());
			}
		}
		else {
			setOperator(null);
		}
		
		if(prevState != null && nextState != null) {
			if (prevState.posX != nextState.posX ||
				prevState.posY != nextState.posY) {
				
				float distanceSq = (nextState.posX - previousTrackMark.x) * (nextState.posX - previousTrackMark.x) + 
					               (nextState.posY - previousTrackMark.y) * (nextState.posY - previousTrackMark.y);
				
				if(distanceSq > 12*12) {
					previousTrackMark.set(nextState.posX, nextState.posY);
					
					// left track
					trackMarkOffset.set(10, -15);
					Vector2f.Vector2fRotate(trackMarkOffset, vehicleOOB.orientation, trackMarkOffset);
					Vector2f.Vector2fAdd(vehicleOOB.topLeft, trackMarkOffset, trackMarkOffset);
					this.effects.addTankTrackMark(getId(), this.trackMarkOffset, this.orientation);

					// right track mark
					trackMarkOffset.set(10, 15);
					Vector2f.Vector2fRotate(trackMarkOffset, vehicleOOB.orientation, trackMarkOffset);
					Vector2f.Vector2fAdd(vehicleOOB.bottomLeft, trackMarkOffset, trackMarkOffset);
					this.effects.addTankTrackMark(getId(), trackMarkOffset, this.orientation);					
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see seventh.client.ClientControllableEntity#movementPrediction(seventh.map.Map, seventh.shared.TimeStep, seventh.math.Vector2f)
	 */
	@Override
	public void movementPrediction(Map map, TimeStep timeStep, Vector2f vel) {
//		if(isAlive() && !vel.isZero()) {			
//			int movementSpeed = calculateMovementSpeed();
//									
//			double dt = timeStep.asFraction();
//			int newX = (int)Math.round(predictedPos.x + vel.x * movementSpeed * dt);
//			int newY = (int)Math.round(predictedPos.y + vel.y * movementSpeed * dt);
//						
//			bounds.x = newX;
//			if( map.rectCollides(bounds) ) {
//				bounds.x = (int)predictedPos.x;
//						
//			}
//			
//			bounds.y = newY;
//			if( map.rectCollides(bounds)) {
//				bounds.y = (int)predictedPos.y;								
//			}
//			
//			predictedPos.x = bounds.x;
//			predictedPos.y = bounds.y;
//			
//			clientSideCorrection(pos, predictedPos, predictedPos, 0.15f);
//			lastMoveTime = timeStep.getGameClock(); 
//		}		
//		else 
		{
			float alpha = 0.15f + 0.108f * (float)((timeStep.getGameClock() - lastMoveTime) / timeStep.getDeltaTime());
			if(alpha > 0.75f) {
				alpha = 0.75f;
			}
			clientSideCorrection(pos, predictedPos, predictedPos, alpha);
		}
	}
	
	/**
	 * @return calculates the movement speed based on
	 * state + current weapon + stamina
	 */
	@Override
	protected int calculateMovementSpeed() {
		return WeaponConstants.TANK_MOVEMENT_SPEED;
	}

	/**
	 * @return the turretFacing
	 */
	public Vector2f getTurretFacing() {
		return turretFacing;
	}
	
	/**
	 * @return the turretOrientation
	 */
	public float getTurretOrientation() {
		return turretOrientation;
	}
	
	/**
	 * @return the primaryWeaponState
	 */
	public Weapon.State getPrimaryWeaponState() {
		return primaryWeaponState;
	}
	
	/**
	 * @return the secondaryWeaponState
	 */
	public Weapon.State getSecondaryWeaponState() {
		return secondaryWeaponState;
	}
	

	/**
	 * @return true if the primary weapon is firing
	 */
	public boolean isPrimaryWeaponFiring() {
		return primaryWeaponState==Weapon.State.FIRING;
	}
	
	/**
	 * @return true if the secondary weapon is firing
	 */
	public boolean isSecondaryWeaponFiring() {
		return secondaryWeaponState==Weapon.State.FIRING;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientEntity#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
				
		this.tankSprite.update(timeStep);
	}

	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		this.tankSprite.render(canvas, camera, alpha);			
	}

}
