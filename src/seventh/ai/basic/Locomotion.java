/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.Random;

import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.BombAction;
import seventh.ai.basic.actions.DecoratorAction;
import seventh.ai.basic.actions.FireAtAction;
import seventh.ai.basic.actions.FollowEntityAction;
import seventh.ai.basic.actions.HeadScanAction;
import seventh.ai.basic.actions.LookAtAction;
import seventh.ai.basic.actions.MoveAction;
import seventh.ai.basic.actions.StareAtEntityAction;
import seventh.ai.basic.actions.SwitchWeaponAction;
import seventh.ai.basic.actions.ThrowGrenadeAction;
import seventh.game.BombTarget;
import seventh.game.Entity;
import seventh.game.Entity.Type;
import seventh.game.Game;
import seventh.game.PlayerEntity;
import seventh.game.Team;
import seventh.game.weapons.GrenadeBelt;
import seventh.game.weapons.Weapon;
import seventh.map.Map;
import seventh.map.PathFeeder;
import seventh.math.Vector2f;
import seventh.shared.DebugDraw;
import seventh.shared.TimeStep;


/**
 * Used for handling Agent motion
 * 
 * @author Tony
 *
 */
public class Locomotion {

	private Brain brain;	
			
	private DecoratorAction walkingGoal;
	private DecoratorAction facingGoal;
	private DecoratorAction handsGoal;
	
	private PlayerEntity me;
	
	private PathFeeder<?> pathFeeder;
	private Vector2f moveDelta;
	
	private Random random;
	
	/**
	 * 
	 */
	public Locomotion(Brain brain) {
		this.brain = brain;
		this.random = brain.getWorld().getRandom();
		this.walkingGoal = new DecoratorAction(brain);
		this.facingGoal = new DecoratorAction(brain);
		this.handsGoal = new DecoratorAction(brain);
		
		this.moveDelta = new Vector2f();
		
		reset(brain);
	}
	
	/**
	 * Resets, generally this was caused by a death
	 */
	public void reset(Brain brain) {
		this.walkingGoal.end(brain);
		this.facingGoal.end(brain);
		this.handsGoal.end(brain);
		
		this.me = brain.getEntityOwner();
	}
	
	/**
	 * @param pathFeeder the pathFeeder to set
	 */
	public void setPathFeeder(PathFeeder<?> pathFeeder) {
		this.pathFeeder = pathFeeder;
	}
	
	/**
	 * Remove the {@link PathFeeder}
	 */
	public void emptyPath() {
		this.pathFeeder = null;
	}
	
	/**
	 * @return the pathFeeder
	 */
	public PathFeeder<?> getPathFeeder() {
		return pathFeeder;
	}
	
	@SuppressWarnings("unused")
	private void debugDraw() {
		int y = 100;
		int x = 20;
		final int yOffset = 20;
		int color = 0xff00ff00;
		
		final String message = "%-8s %-19s %-5s";
		DebugDraw.drawString(String.format(message, "Motion", "State", "IsFinished"), x, y, color);
		DebugDraw.drawString("====================================", x, y += yOffset, color);
		
		String text = String.format(message, "Walking", walkingGoal.getAction() != null ? walkingGoal.getAction().getClass().getSimpleName():"[none]", walkingGoal.isFinished(brain));				
		DebugDraw.drawString(text, x, y += yOffset, color);
		
		text = String.format(message, "Facing", facingGoal.getAction() != null ? facingGoal.getAction().getClass().getSimpleName():"[none]", facingGoal.isFinished(brain));				
		DebugDraw.drawString(text, x, y += yOffset, color);
		
		text = String.format(message, "Hands", handsGoal.getAction() != null ? handsGoal.getAction().getClass().getSimpleName():"[none]", handsGoal.isFinished(brain));				
		DebugDraw.drawString(text, x, y += yOffset, color);
		
		
	}
	
	/**
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {
		//debugDraw();
		
		if(!walkingGoal.isFinished(brain)) {
			walkingGoal.update(brain, timeStep);
		}
		
		if(!facingGoal.isFinished(brain)) {
			facingGoal.update(brain, timeStep);
		}
		else {
			if(!walkingGoal.isFinished(brain)) {
				scanArea();
			}
		}
		
		if(!handsGoal.isFinished(brain)) {
			handsGoal.update(brain, timeStep);
		}		
		
		moveEntity();
	}
	
	/**
	 * Do the actual movement
	 */
	private void moveEntity() {

		moveDelta.zeroOut();
		if(pathFeeder!=null && !pathFeeder.atDestination()) {
			Vector2f nextDest = pathFeeder.nextDestination(me.getPos());
			moveDelta.set(nextDest);
		}
		
		directMove(moveDelta);
	}
	
	/**
	 * Scans the area
	 */
	public void scanArea() {
		Action action = new HeadScanAction();
		this.facingGoal.setAction(action);
	}
	
	/**
	 * Switches to another weapon in the bots arsenal
	 * @param weapon
	 */
	public void changeWeapon(Type weapon) {
		Weapon currentWeapon = me.getInventory().currentItem();
		if(currentWeapon != null) {
			if(!currentWeapon.getType().equals(weapon)) {
				Action action = new SwitchWeaponAction(weapon);
				handsGoal.setAction(action);
			}
		}
	}
	
	/**
	 * Plants a bomb
	 */
	public void plantBomb(BombTarget bomb) {			
		handsGoal.setAction(new BombAction(bomb, true));		
	}
	
	/**
	 * Defuses a bomb
	 */
	public void defuseBomb(BombTarget bomb) {			
		handsGoal.setAction(new BombAction(bomb, false));		
	}
	
	/**
	 * Moves the 
	 * @param dest
	 * @return an {@link Action} to invoke
	 */
	public void moveTo(Vector2f dest) {
		Action action = new MoveAction(dest);		
		this.walkingGoal.setAction(action);
	}
	
	/**
	 * Wanders around
	 */
	public void wander() {
		moveTo(brain.getWorld().getRandomSpot(brain.getEntityOwner()));
		scanArea();
	}
	
	public void followEntity(Entity entity) {
		Action action = new FollowEntityAction(entity);
		
		this.walkingGoal.setAction(action);
	}
	public void stopMoving() {
		this.walkingGoal.end(brain);
	}
	
	public boolean isMoving() {
		return this.walkingGoal.hasAction() && !this.walkingGoal.isFinished(brain);
	}
	
	public void lookAt(Vector2f pos) {		
		this.facingGoal.setAction(new LookAtAction(this.me, pos));
	}	
	public void stareAtEntity(Entity entity) {
		this.facingGoal.setAction(new StareAtEntityAction(entity));
	}
	public boolean isStaringAtEntity() {
		return this.facingGoal.is(StareAtEntityAction.class);
	}
	
	public void fireAt(Entity entity) {
		this.handsGoal.setAction(new FireAtAction(entity));
	}
	
	public boolean handsInUse() {
		return this.handsGoal.hasAction();
	}
	
	public boolean isTooClose(Entity ent) {
		return (Vector2f.Vector2fDistanceSq(this.me.getPos(), ent.getPos()) < 2500);
	}
	
	public boolean throwGrenade(Vector2f pos) {
		GrenadeBelt belt=this.me.getInventory().getGrenades();
		if( belt.getNumberOfGrenades() > 0 && !handsInUse()) {
			this.handsGoal.setAction(new ThrowGrenadeAction(me, pos));
			return true;
		}
		return false;
	}
	
	public void attack(Entity ent) {
		if(ent != null) {
			if (! isStaringAtEntity()) 
			{
				stareAtEntity(ent);
			}
			//if (! isMoving() && !isTooClose(ent) )
			if( !walkingGoal.is(FollowEntityAction.class) )
			{
				followEntity(ent);
			}
			
			if (! handsInUse()) 
			{
				fireAt(ent);
			}
		}
	}
	
	/**
	 * @return if we are attacking
	 */
	public boolean isAttacking() {
		return this.handsGoal.is(FireAtAction.class);
	}
	
	/**
	 * Directly moves the entity, based on the delta inputs
	 * @param delta
	 */
	public void directMove(Vector2f delta) {		
		directMove(delta.x, delta.y);
	}
	
	/**
	 * Directly moves the entity, based on the delta inputs
	 * @param x the X direction to move
	 * @param y the Y direction to move
	 */
	public void directMove(float x, float y) {
		final float threshold = 0f;
		
		if (x < -threshold ) {
			me.moveLeft(); 
		}
		else if (x > threshold ) {
			me.moveRight();
		}
		else {
			me.noMoveX();
		}
		
		if(y < -threshold ) {
			me.moveUp();
		}
		else if(y > threshold) {
			me.moveDown();
		}
		else {
			me.noMoveY();
		}

	}
	
	
	/**
	 * Picks a weapon class to use
	 * @return the type of weapon
	 */
	public Type pickWeapon() {
		
		PlayerEntity bot = brain.getEntityOwner();
		Type type = getRandomWeapon(bot.getTeam());
		bot.setWeaponClass(type);
		
		changeWeapon(type);
		
		return type;
	}
	
	
	/**
	 * Picks a semi-random weapon.  It may attempt to pick 
	 * a weapon based on the world (this is chosen randomly).
	 * 
	 * @param team
	 * @return the weapon type
	 */
	private Type getRandomWeapon(Team team) {
		int index = -1;
		if(team != null) {						
			boolean pickSmart = random.nextBoolean();
			if(pickSmart) {
				index = pickThoughtfulWeapon(team);
			}
			else {
				index = random.nextInt(Game.alliedWeapons.length);
			}
			
			if(index > -1 && index < Game.alliedWeapons.length) {
				if(team.getId()==Team.ALLIED_TEAM) {
					return Game.alliedWeapons[index];
				}
				else {
					return Game.axisWeapons[index];
				}
			}
		}
		
		return Type.UNKNOWN;
	}
	
	/**
	 * Picks a weapon based on some world conditions.
	 * 
	 * @param team
	 * @return the index to the weapon
	 */
	private int pickThoughtfulWeapon(Team team) {
		if(team != null) {
			Map map = brain.getWorld().getMap();
			boolean isSmallMap = false;
			boolean isLargeMap = false;
			
			int size = map.getMapHeight() + map.getMapWidth();
			if(size >= 3200 ) {
				isLargeMap = true;
			}
			else if( size <= 1920) {
				isSmallMap = true;
			}
			
			if(isLargeMap) {
				return random.nextInt(2) + 2;	
			}
			else if(isSmallMap) {
				return random.nextInt(2);
			}
			
			return random.nextInt(Game.alliedWeapons.length);
		}
		
		return -1;
	}
}
