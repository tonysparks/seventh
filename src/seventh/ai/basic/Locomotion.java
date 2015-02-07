/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.List;
import java.util.Random;

import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.BombAction;
import seventh.ai.basic.actions.CrouchAction;
import seventh.ai.basic.actions.DecoratorAction;
import seventh.ai.basic.actions.DropWeaponAction;
import seventh.ai.basic.actions.FireAtAction;
import seventh.ai.basic.actions.FollowEntityAction;
import seventh.ai.basic.actions.HeadScanAction;
import seventh.ai.basic.actions.LookAtAction;
import seventh.ai.basic.actions.MeleeAction;
import seventh.ai.basic.actions.MoveAction;
import seventh.ai.basic.actions.ReloadAction;
import seventh.ai.basic.actions.ShootAction;
import seventh.ai.basic.actions.SprintAction;
import seventh.ai.basic.actions.StareAtEntityAction;
import seventh.ai.basic.actions.SwitchWeaponAction;
import seventh.ai.basic.actions.ThrowGrenadeAction;
import seventh.ai.basic.actions.WalkAction;
import seventh.game.BombTarget;
import seventh.game.Entity;
import seventh.game.Entity.Type;
import seventh.game.Game;
import seventh.game.PlayerEntity;
import seventh.game.Team;
import seventh.game.weapons.GrenadeBelt;
import seventh.game.weapons.Weapon;
import seventh.graph.GraphNode;
import seventh.map.Map;
import seventh.map.MapGraph;
import seventh.map.Tile;
import seventh.math.Vector2f;
import seventh.shared.DebugDraw;
import seventh.shared.Debugable;
import seventh.shared.TimeStep;


/**
 * Used for handling Agent motion
 * 
 * @author Tony
 *
 */
public class Locomotion implements Debugable {

	private Brain brain;	
			
	private DecoratorAction destinationGoal;
	private DecoratorAction facingGoal;
	private DecoratorAction handsGoal;
	private DecoratorAction legsGoal;
	
	private PlayerEntity me;
	
	private final PathPlanner<?> pathPlanner;
	private Vector2f moveDelta;
	
	private Random random;
	
	/*
	 * cached common actions
	 */
	private HeadScanAction headScan;
	private MoveAction moveAction;
	private LookAtAction lookAt;
	private StareAtEntityAction stareAt;
	private FireAtAction fireAt;
	private CrouchAction crouchAction;
	private WalkAction walkAction;
	private SprintAction sprintAction;
	private ReloadAction reloadAction;
	private MeleeAction meleeAction;
	private DropWeaponAction dropWeaponAction;
	private ShootAction shootAction;
	
	/**
	 * 
	 */
	public Locomotion(Brain brain) {
		this.brain = brain;
		
		this.pathPlanner = new PathPlanner<>(brain.getWorld().getGraph());
		
		this.random = brain.getWorld().getRandom();
		this.destinationGoal = new DecoratorAction(brain);
		this.legsGoal = new DecoratorAction(brain);
		this.facingGoal = new DecoratorAction(brain);
		this.handsGoal = new DecoratorAction(brain);
		
		this.moveDelta = new Vector2f();
		
		this.headScan = new HeadScanAction();
		this.moveAction = new MoveAction();
		this.lookAt = new LookAtAction(0);
		this.stareAt = new StareAtEntityAction(null);
		this.fireAt = new FireAtAction(null);
		this.crouchAction = new CrouchAction();
		this.walkAction = new WalkAction();
		this.sprintAction = new SprintAction();
		this.reloadAction = new ReloadAction();
		this.meleeAction = new MeleeAction();
		this.dropWeaponAction = new DropWeaponAction();
		this.shootAction = new ShootAction();
		
		reset(brain);
	}
	
	/**
	 * Resets, generally this was caused by a death
	 */
	public void reset(Brain brain) {
		this.destinationGoal.end(brain);
		this.facingGoal.end(brain);
		this.handsGoal.end(brain);
		this.legsGoal.end(brain);
		
		this.me = brain.getEntityOwner();
	}
		
	/**
	 * Remove the {@link PathPlanner}
	 */
	public void emptyPath() {
		this.pathPlanner.clearPath();
	}
	
	/**
	 * @return the pathFeeder
	 */
	public PathPlanner<?> getPathPlanner() {
		return pathPlanner;
	}
	
	@SuppressWarnings("unused")
	private void debugDraw() {
//		int y = 100;
//		int x = 20;
//		final int yOffset = 20;
//		int color = 0xff00ff00;
//		
//		final String message = "%-8s %-19s %-5s";
//		DebugDraw.drawString(String.format(message, "Motion", "State", "IsFinished"), x, y, color);
//		DebugDraw.drawString("====================================", x, y += yOffset, color);
//		
//		String text = String.format(message, "Walking", destinationGoal.getAction() != null ? destinationGoal.getAction().getClass().getSimpleName():"[none]", destinationGoal.isFinished(brain));				
//		DebugDraw.drawString(text, x, y += yOffset, color);
//		
//		text = String.format(message, "Facing", facingGoal.getAction() != null ? facingGoal.getAction().getClass().getSimpleName():"[none]", facingGoal.isFinished(brain));				
//		DebugDraw.drawString(text, x, y += yOffset, color);
//		
//		text = String.format(message, "Hands", handsGoal.getAction() != null ? handsGoal.getAction().getClass().getSimpleName():"[none]", handsGoal.isFinished(brain));				
//		DebugDraw.drawString(text, x, y += yOffset, color);
		
		MapGraph<?> graph = brain.getWorld().getGraph();
		for(int y = 0; y < graph.graph.length; y++) {
			for(int x = 0; x < graph.graph[0].length; x++) {
				GraphNode<Tile, ?> node = graph.getNodeByIndex(x, y);
				if(node != null) {
					Tile t = node.getValue();
					//DebugDraw.fillRectRelative(t.getX(), t.getY(), t.getWidth(), t.getHeight(), 0x1f00ff00);
					//DebugDraw.drawRectRelative(t.getX(), t.getY(), t.getWidth(), t.getHeight(), 0x8f00ffff);
				}
			}
		}
		Entity ent = brain.getEntityOwner();
		if(ent != null) {
			DebugDraw.fillRectRelative(ent.getBounds().x, ent.getBounds().y, ent.getBounds().width, ent.getBounds().height, 0xffff0000);
		}
		
	}
	
	/**
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {
//		debugDraw();
		
		if(!destinationGoal.isFinished(brain)) {
			destinationGoal.update(brain, timeStep);
		}
		
		if(!legsGoal.isFinished(brain)) {
			legsGoal.update(brain, timeStep);
		}
		
		if(!facingGoal.isFinished(brain)) {
			facingGoal.update(brain, timeStep);
		}
		else {
			if(!destinationGoal.isFinished(brain)) {
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
		if(pathPlanner.hasPath()) {
			if (!pathPlanner.atDestination()) {
//				Vector2f nextDest = pathFeeder.nextDestination(me.getPos());
				Vector2f nextDest = pathPlanner.nextDestination(me);
				moveDelta.set(nextDest);
			}
			else {
				Vector2f nextDest = pathPlanner.getDestination();
				Vector2f.Vector2fSubtract(nextDest, me.getPos(), moveDelta);	
				if(moveDelta.lengthSquared() < 26) {
					moveDelta.zeroOut();
				}
			}
		}
		
		directMove(moveDelta);
	}
	
	/**
	 * Scans the area
	 */
	public void scanArea() {
		if(!this.facingGoal.is(HeadScanAction.class)) {
			this.headScan.reset();
			this.facingGoal.setAction(this.headScan);
		}
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
	 * Moves to the destination, the bot will do a optimized path
	 * to the destination
	 * 
	 * @param dest
	 * @return an {@link Action} to invoke
	 */
	public void moveTo(Vector2f dest) {
		this.moveAction.setDestination(dest);
		this.moveAction.setFuzzyNess(0);
		this.moveAction.clearAvoids();
		this.destinationGoal.setAction(this.moveAction);
	}
	
	/**
	 * Similar to {@link Locomotion#moveTo(Vector2f)} but the path
	 * to the destination is not in a straight line
	 * 
	 * @param dest
	 */
	public void dodgeTo(Vector2f dest) {
		this.moveAction.setDestination(dest);
		this.moveAction.setFuzzyNess(1_000_000);
		this.moveAction.clearAvoids();
		this.destinationGoal.setAction(this.moveAction);
	}
	
	/**
	 * Moves to the destination, avoiding the supplied {@link Zone}s.
	 * 
	 * @param dest
	 * @param avoid
	 */
	public void avoidMoveTo(Vector2f dest, List<Zone> avoid) {
		this.moveAction.setDestination(dest);
		this.moveAction.setFuzzyNess(0);
		this.moveAction.setZonesToAvoid(avoid);
		this.destinationGoal.setAction(this.moveAction);
	}
	
	/**
	 * Wanders around
	 */
	public void wander() {
		dodgeTo(brain.getWorld().getRandomSpot(brain.getEntityOwner()));
		scanArea();
	}
	
	public void followEntity(Entity entity) {
		Action action = new FollowEntityAction(entity);
		
		this.destinationGoal.setAction(action);
	}
	public void stopMoving() {
		this.destinationGoal.end(brain);
	}
	public void stopUsingHands() {
		this.handsGoal.end(brain);
	}
	
	/**
	 * @return the destination this entity is moving towards, or null if no
	 * destination
	 */
	public Vector2f getDestination() {
		if(this.pathPlanner != null) {
			return pathPlanner.getDestination();
		}
		return null;
	}
	
	public boolean isMoving() {
		return this.destinationGoal.hasAction() && !this.destinationGoal.isFinished(brain);
	}
	public boolean isPlanting() {
		return this.handsGoal.hasAction() && this.handsGoal.is(BombAction.class);
	}
	public boolean isDefusing() {
		return this.handsGoal.hasAction() && this.handsGoal.is(BombAction.class);
	}
	
	
	/**
	 * Crouch down
	 */
	public void crouch() {
		this.destinationGoal.end(this.brain);		
		this.legsGoal.end(this.brain);
		this.legsGoal.setAction(this.crouchAction);
	}
	
	/**
	 * Stop crouching
	 */
	public void standup() {
		if(isCrouching()) {
			this.legsGoal.end(this.brain);
		}
	}
	
	/**
	 * @return true if crouching
	 */
	public boolean isCrouching() {
		return this.legsGoal.is(CrouchAction.class);
	}
	
	/**
	 * @return true if sprinting
	 */
	public boolean isSprinting() {
		return this.legsGoal.is(SprintAction.class);
	}
	
	/**
	 * @return true if walking
	 */
	public boolean isWalking() {
		return this.legsGoal.is(WalkAction.class);
	}
	
	public void walk() {
		this.legsGoal.setAction(this.walkAction);
	}
	
	public void stopWalking() {
		if(this.legsGoal.is(WalkAction.class)) {
			this.legsGoal.end(brain);
		}
	}
	
	
	public void sprint() {
		this.legsGoal.setAction(this.sprintAction);
	}
	
	public void stopSprinting() {
		if(this.legsGoal.is(SprintAction.class)) {
			this.legsGoal.end(brain);
		}
	}
	
	public void reload() {
		this.handsGoal.setAction(this.reloadAction);
	}
	
	public void meleeAttack() {
		this.handsGoal.setAction(this.meleeAction);
	}
	public void dropWeapon() {
		this.handsGoal.setAction(this.dropWeaponAction);
	}
	
	public void lookAt(Vector2f pos) {		
		this.lookAt.reset(me, pos);
		this.facingGoal.setAction(this.lookAt);
	}	
	public void stareAtEntity(Entity entity) {
		this.stareAt.reset(entity);
		this.facingGoal.setAction(this.stareAt);
	}
	public boolean isStaringAtEntity() {
		return this.facingGoal.is(StareAtEntityAction.class);
	}
	
	public void fireAt(Entity entity) {
		this.fireAt.reset(entity);
		this.handsGoal.setAction(this.fireAt);
	}
	
	public void shoot() {
		this.handsGoal.setAction(this.shootAction);
	}
	
	public void stopShooting() {
		if(this.handsGoal.is(ShootAction.class)) {
			this.handsGoal.end(this.brain);
		}
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
			if( !destinationGoal.is(FollowEntityAction.class) )
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
		final float threshold = 0f; // was 0
		
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
				if(team.getId()==Team.ALLIED_TEAM_ID) {
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
	
	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation info = new DebugInformation();
		info.add("hands", this.handsGoal)
		    .add("destination", this.destinationGoal)
		    .add("facing", this.facingGoal)
		    .add("legs", this.legsGoal)
		    ;
		return info;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {	
		return getDebugInformation().toString();
	}
}
