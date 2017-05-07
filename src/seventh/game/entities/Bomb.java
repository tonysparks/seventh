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
    private Rectangle blastRectangle;
    
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
                
        
        this.setNetBomb(new NetBomb());
        this.getNetBomb().id = getId();
        setNetEntity(getNetBomb());
        
        // 30 second timer
        this.setTimer(new Timer(false, 30_000));
        this.getTimer().stop();        
        
        this.setPlantTimer(new Timer(false, 3_000));
        this.getPlantTimer().stop();
        
        this.setDisarmTimer(new Timer(false, 5_000));
        this.getDisarmTimer().stop();
        
        this.setBlowingupTimer(new Timer(false, 3_000));
        this.getBlowingupTimer().stop();
        
        this.setNextExplosionTimer(new Timer(true, 300));
        this.getNextExplosionTimer().start();
        
        this.setDispatcher(game.getDispatcher());        
        
        this.setBlastRectangle(new Rectangle());
        
        this.setSplashWidth(20);
        this.setMaxSpread(40);
        this.setTickMarker(10);
    }
    
    /* (non-Javadoc)
     * @see seventh.game.Entity#update(leola.live.TimeStep)
     */
    @Override
    public boolean update(TimeStep timeStep) {
        super.update(timeStep);
        
        this.getTimer().update(timeStep);
        this.getPlantTimer().update(timeStep);
        this.getDisarmTimer().update(timeStep);
        
        this.getBlowingupTimer().update(timeStep);;
        this.getNextExplosionTimer().update(timeStep);
                
        /* check and see if the bomb timer has expired,
         * if so the bomb goes boom
         */
        checkBombExplode();
        
        bombTimeCount();
        
        
        /* the bomb goes off for a while, creating
         * many expositions, once this expires we
         * kill the bomb
         */
        bombKill();
        
        
        
        /* check and see if we are done planting this bomb */
        checkPlantedBomb();
        
        
        
        /* check and see if the bomb has been disarmed */
        checkDisarmedBomb();
        
        return true;
    }

	private void bombKill() {
		if(this.getBlowingupTimer().isTime()) {
            this.getTimer().stop();
            
            // ka-bom
            if(this.getBombTarget()!=null) {
                this.getBombTarget().kill(this);
            }
            
            kill(this);                                    
        }
	}

	private void checkDisarmedBomb() {
		if(this.getDisarmTimer().isTime()) {
            this.getTimer().stop();
            
            getDispatcher().queueEvent(new BombDisarmedEvent(this, Bomb.this));
            this.getDisarmTimer().stop();
            
            softKill();
            
            if(getBombTarget()!=null) {
                getBombTarget().reset();
            }
            setBombTarget(null);
        }
	}

	private void checkPlantedBomb() {
		if(this.getPlantTimer().isTime()) {            
            this.getTimer().start();            
            getDispatcher().queueEvent(new BombPlantedEvent(this, Bomb.this));
            this.getPlantTimer().stop();
        }
	}

	private void bombTimeCount() {
		if(this.getTimer().isUpdating()) {
            if(this.getTimer().getRemainingTime() < 10_000) {
                long trSec = this.getTimer().getRemainingTime()/1_000;
                if(trSec <= getTickMarker()) {
                    game.emitSound(getId(), SoundType.BOMB_TICK, getPos());                    
                    setTickMarker(getTickMarker() - 1);
                }            
            }
        }
        else {
            setTickMarker(10);
        }
	}

	private void checkBombExplode() {
		if(this.getTimer().isTime()) {
            if(!this.getBlowingupTimer().isUpdating()) {
                this.getBlowingupTimer().start();
                getDispatcher().queueEvent(new BombExplodedEvent(this, Bomb.this));
            }
                            
            if(this.getNextExplosionTimer().isTime()) {
                game.newBigExplosion(getCenterPos(), this.getPlanter(), this.getSplashWidth(), this.getMaxSpread(), 200);
            }
        }
	}
    
    /**
     * @return the estimated blast radius
     */
    public Rectangle getCalculateBlastRectangle() {
        /* the max spread of the blast could contain a splash width at either end,
         * so therefore we add the splashWidth twice
         */
        int maxWidth = (this.getSplashWidth() + this.getMaxSpread()) * 4;
        
        this.getBlastRectangle().setSize(maxWidth, maxWidth);
        this.getBlastRectangle().centerAround(getCenterPos());
        return this.getBlastRectangle();
    }

	/**
     * @return the bombTarget
     */
    public BombTarget getBombTarget() {
        return bombTarget;
    }
    
    private void setBombTarget(BombTarget bombTarget) {
		this.bombTarget = bombTarget;
	}

	/**
     * Plants this bomb on the supplied {@link BombTarget}
     * 
     * @param planter
     * @param bombTarget
     */
    public void plant(PlayerEntity planter, BombTarget bombTarget) {
        this.setBombTarget(bombTarget);
        this.pos.set(bombTarget.getCenterPos());
        
        if(this.getPlanter() == null) {
            this.setPlanter(planter);            
        }
        
        
        if(this.getPlanter() != null) {
            this.getPlantTimer().start();
        }
        else {
            this.getPlantTimer().stop();
        }                
    }
    
    /**
     * @return true if this bomb is being planted
     */
    public boolean isPlanting() {
        return this.getPlanter() != null && this.getPlantTimer().isUpdating();
    }
    
    /**
     * @return true if this bomb is being disarmed
     */
    public boolean isDisarming() {
        return this.getDisarmer() != null && this.getDisarmTimer().isUpdating();
    }
    
    /**
     * @return true if this bomb is blowing up right now
     */
    public boolean isBlowingUp() {
        return this.getBlowingupTimer().isUpdating();
    }
    
    /**
     * Stops planting the bomb
     */
    public void stopPlanting() {
        this.setPlanter(null);
        this.getPlantTimer().stop();
    }
    
    /**
     * @return true if this bomb is planted on a bomb target
     */
    public boolean isPlanted() {
        return this.getTimer().isUpdating();
    }
    
    /**
     * @return the planter
     */
    private PlayerEntity getPlanter() {
        return planter;
    }
    
    private void setPlanter(PlayerEntity planter) {
		this.planter = planter;
	}

	/**
     * @return the disarmer
     */
    private PlayerEntity getDisarmer() {
        return disarmer;
    }
    
    private void setDisarmer(PlayerEntity disarmer) {
		this.disarmer = disarmer;
	}

	/**
     * Disarm the bomb
     */
    public void disarm(PlayerEntity entity) {
        if(this.getDisarmer() == null) {
            this.setDisarmer(entity);
        }
        
        if(this.getDisarmer()!=null) {
            this.getDisarmTimer().start();
        }
        else {
            this.getDisarmTimer().stop();
        }        
    }
    
    /**
     * Stops disarming this bomb
     */
    public void stopDisarming() {
        this.setDisarmer(null);
        this.getDisarmTimer().stop();
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
        this.getNetBomb().timeRemaining = (int) this.getTimer().getRemainingTime();
        return this.getNetBomb();
    }

	private Timer getTimer() {
		return timer;
	}

	private void setTimer(Timer timer) {
		this.timer = timer;
	}

	private Timer getPlantTimer() {
		return plantTimer;
	}

	private void setPlantTimer(Timer plantTimer) {
		this.plantTimer = plantTimer;
	}

	private Timer getDisarmTimer() {
		return disarmTimer;
	}

	private void setDisarmTimer(Timer disarmTimer) {
		this.disarmTimer = disarmTimer;
	}

	private Timer getBlowingupTimer() {
		return blowingupTimer;
	}

	private void setBlowingupTimer(Timer blowingupTimer) {
		this.blowingupTimer = blowingupTimer;
	}

	private Timer getNextExplosionTimer() {
		return nextExplosionTimer;
	}

	private void setNextExplosionTimer(Timer nextExplosionTimer) {
		this.nextExplosionTimer = nextExplosionTimer;
	}

	private NetBomb getNetBomb() {
		return netBomb;
	}

	private void setNetBomb(NetBomb netBomb) {
		this.netBomb = netBomb;
	}

	private EventDispatcher getDispatcher() {
		return dispatcher;
	}

	private void setDispatcher(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	private int getSplashWidth() {
		return splashWidth;
	}

	private void setSplashWidth(int splashWidth) {
		this.splashWidth = splashWidth;
	}

	private int getMaxSpread() {
		return maxSpread;
	}

	private void setMaxSpread(int maxSpread) {
		this.maxSpread = maxSpread;
	}

	private int getTickMarker() {
		return tickMarker;
	}

	private void setTickMarker(int tickMarker) {
		this.tickMarker = tickMarker;
	}
	
    private Rectangle getBlastRectangle(){
    	return this.blastRectangle;
    }
    
    private void setBlastRectangle(Rectangle blastRectangle){
    	this.blastRectangle = blastRectangle;
    }
}
