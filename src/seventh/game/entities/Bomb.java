/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.entities;

import leola.frontend.listener.EventDispatcher;
import seventh.game.Game;
import seventh.game.events.BombDisarmedEvent;
import seventh.game.events.BombExplodedEvent;
import seventh.game.events.BombPlantedEvent;
import seventh.game.net.NetBomb;
import seventh.game.net.NetEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * A Bomb is something that can be planted on a {@link BombTarget} and makes 
 * it go boom!
 * 
 * @author Tony
 *
 */
public class Bomb extends Entity {

	private Timer timer;
	private Timer plantTimer, disarmTimer;
	private Timer blowingupTimer, nextExplosionTimer;
	
	private NetBomb netBomb;
	
	private EventDispatcher dispatcher;
	
	private PlayerEntity planter, disarmer;
	private BombTarget bombTarget;
	
	private int splashWidth, maxSpread;
	private Rectangle blastRadius;
	
	private int tickMarker;
	
	/**
	 * @param position
	 * @param game
	 * @param plantedOn -- the entity which this bomb is planted on
	 */
	public Bomb(Vector2f position, final Game game) {
		super(position, 0, game, Type.BOMB);
		
		this.bounds.width = 5;
		this.bounds.height = 5;
				
		
		this.netBomb = new NetBomb();
		this.netBomb.id = getId();
		setNetEntity(netBomb);
		
		// 30 second timer
		this.timer = new Timer(false, 30_000);
		this.timer.stop();		
		
		this.plantTimer = new Timer(false, 3_000);
		this.plantTimer.stop();
		
		this.disarmTimer = new Timer(false, 5_000);
		this.disarmTimer.stop();
		
		this.blowingupTimer = new Timer(false, 3_000);
		this.blowingupTimer.stop();
		
		this.nextExplosionTimer = new Timer(true, 300);
		this.nextExplosionTimer.start();
		
		this.dispatcher = game.getDispatcher();		
		
		this.blastRadius = new Rectangle();
		
		this.splashWidth = 20;
		this.maxSpread = 40;
		this.tickMarker = 10;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#update(leola.live.TimeStep)
	 */
	@Override
	public boolean update(TimeStep timeStep) {
		super.update(timeStep);
		
		this.timer.update(timeStep);
		this.plantTimer.update(timeStep);
		this.disarmTimer.update(timeStep);
		
		this.blowingupTimer.update(timeStep);;
		this.nextExplosionTimer.update(timeStep);
				
		/* check and see if the bomb timer has expired,
		 * if so the bomb goes boom
		 */
		if(this.timer.isTime()) {
			if(!this.blowingupTimer.isUpdating()) {
				this.blowingupTimer.start();
				dispatcher.queueEvent(new BombExplodedEvent(this, Bomb.this));
			}
							
			if(this.nextExplosionTimer.isTime()) {
				game.newBigExplosion(getCenterPos(), this.planter, this.splashWidth, this.maxSpread, 200);
			}
		}
		
		if(this.timer.isUpdating()) {
			if(this.timer.getRemainingTime() < 10_000) {
				long trSec = this.timer.getRemainingTime()/1_000;
				if(trSec <= tickMarker) {
					game.emitSound(getId(), SoundType.BOMB_TICK, getPos());					
					tickMarker--;
				}			
			}
		}
		else {
			tickMarker=10;
		}
		
		
		/* the bomb goes off for a while, creating
		 * many expositions, once this expires we
		 * kill the bomb
		 */
		if(this.blowingupTimer.isTime()) {
			this.timer.stop();
			
			// ka-bom
			if(this.bombTarget!=null) {
				this.bombTarget.kill(this);
			}
			
			kill(this);									
		}
		
		
		
		/* check and see if we are done planting this bomb */
		if(this.plantTimer.isTime()) {			
			this.timer.start();			
			dispatcher.queueEvent(new BombPlantedEvent(this, Bomb.this));
			this.plantTimer.stop();
		}
		
		
		
		/* check and see if the bomb has been disarmed */
		if(this.disarmTimer.isTime()) {
			this.timer.stop();
			
			dispatcher.queueEvent(new BombDisarmedEvent(this, Bomb.this));
			this.disarmTimer.stop();
			
			softKill();
			
			if(bombTarget!=null) {
				bombTarget.reset();
			}
			bombTarget = null;
		}
		
		return true;
	}
	
	/**
	 * @return the estimated blast radius
	 */
	public Rectangle getBlastRadius() {
		/* the max spread of the blast could contain a splash width at either end,
		 * so therefore we add the splashWidth twice
		 */
		int maxWidth = (this.splashWidth + this.maxSpread) * 4;
		
		this.blastRadius.setSize(maxWidth, maxWidth);
		this.blastRadius.centerAround(getCenterPos());
		return this.blastRadius;
	}
	
	/**
	 * @return the bombTarget
	 */
	public BombTarget getBombTarget() {
		return bombTarget;
	}
	
	/**
	 * Plants this bomb on the supplied {@link BombTarget}
	 * 
	 * @param planter
	 * @param bombTarget
	 */
	public void plant(PlayerEntity planter, BombTarget bombTarget) {
		this.bombTarget = bombTarget;
		this.pos.set(bombTarget.getCenterPos());
		
		if(this.planter == null) {
			this.planter = planter;			
		}
		
		
		if(this.planter != null) {
			this.plantTimer.start();
		}
		else {
			this.plantTimer.stop();
		}				
	}
	
	/**
	 * @return true if this bomb is being planted
	 */
	public boolean isPlanting() {
		return this.planter != null && this.plantTimer.isUpdating();
	}
	
	/**
	 * @return true if this bomb is being disarmed
	 */
	public boolean isDisarming() {
		return this.disarmer != null && this.disarmTimer.isUpdating();
	}
	
	/**
	 * @return true if this bomb is blowing up right now
	 */
	public boolean isBlowingUp() {
		return this.blowingupTimer.isUpdating();
	}
	
	/**
	 * Stops planting the bomb
	 */
	public void stopPlanting() {
		this.planter = null;
		this.plantTimer.stop();
	}
	
	/**
	 * @return true if this bomb is planted on a bomb target
	 */
	public boolean isPlanted() {
		return this.timer.isUpdating();
	}
	
	/**
	 * @return the planter
	 */
	public PlayerEntity getPlanter() {
		return planter;
	}
	
	/**
	 * @return the disarmer
	 */
	public PlayerEntity getDisarmer() {
		return disarmer;
	}
	
	/**
	 * Disarm the bomb
	 */
	public void disarm(PlayerEntity entity) {
		if(this.disarmer == null) {
			this.disarmer = entity;
		}
		
		if(this.disarmer!=null) {
			this.disarmTimer.start();
		}
		else {
			this.disarmTimer.stop();
		}		
	}
	
	/**
	 * Stops disarming this bomb
	 */
	public void stopDisarming() {
		this.disarmer=null;
		this.disarmTimer.stop();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#canTakeDamage()
	 */
	@Override
	public boolean canTakeDamage() {
		return false;
	}

	/* (non-Javadoc)
	 * @see seventh.game.Entity#getNetEntity()
	 */
	@Override
	public NetEntity getNetEntity() {
		this.netBomb.timeRemaining = (int) this.timer.getRemainingTime();
		return this.netBomb;
	}

}
