/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom.body;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.Entity;
import seventh.game.entities.PlayerEntity;
import seventh.math.FastMath;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;


/**
 * @author Tony
 *
 */
public class LookAtAction extends AdapterAction {
    
    private float destinationOrientation;
    private Vector2f position;
    
    public LookAtAction(float orientation) {
        this.destinationOrientation = orientation;        
    }
    
    public LookAtAction(Entity entity, Vector2f position) {        
        reset(entity, position);
    }
    
    /**
     * 
     */
    public LookAtAction(Vector2f position) {
        this.position = position;
    }

    /**
     * Sets the orientation to move in order for the Entity to look at the
     * dest vector.
     * 
     * @param me
     * @param dest
     */
    public void reset(Entity me, Vector2f dest) {
        this.destinationOrientation = Entity.getAngleBetween(dest, me.getPos());
        
        final float fullCircle = FastMath.fullCircle;
        if(this.destinationOrientation < 0) {
            this.destinationOrientation += fullCircle;
        }
    }
    
    /* (non-Javadoc)
     * @see palisma.ai.Action#start(palisma.ai.Brain)
     */
    @Override
    public void start(Brain brain) {                
        if(this.position != null) {
            reset(brain.getEntityOwner(), this.position);            
        }
    }

    /* (non-Javadoc)
     * @see palisma.ai.Action#isFinished()
     */
    @Override
    public boolean isFinished(Brain brain) {
        PlayerEntity ent = brain.getEntityOwner();
        
        float currentOrientation = ent.getOrientation();
        double currentDegree = Math.toDegrees(currentOrientation);
        double destDegree = Math.toDegrees(destinationOrientation);
        
        // TODO: Work out Looking at something (being shot while takingCover)
        return Math.abs(currentDegree - destDegree) < 5;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
     */
    @Override
    public void update(Brain brain, TimeStep timeStep) {
        PlayerEntity ent = brain.getEntityOwner();
        
        float currentOrientation = ent.getOrientation();
        
        final float fullCircle = FastMath.fullCircle;
        
        // Thank you: http://dev.bennage.com/blog/2013/03/05/game-dev-03/
        float deltaOrientation = (destinationOrientation - currentOrientation);
        float deltaOrientationAbs = Math.abs(deltaOrientation);
        if(deltaOrientationAbs > Math.PI) {
            deltaOrientation *= -1;
        }
        
        final double movementSpeed = Math.toRadians(15.0f);
        
        if(deltaOrientationAbs != 0) {
            float direction = deltaOrientation / deltaOrientationAbs;
            currentOrientation += (direction * Math.min(movementSpeed, deltaOrientationAbs));
            
            if(currentOrientation < 0) {
                currentOrientation = fullCircle + currentOrientation;
            }
            currentOrientation %= fullCircle;
        }
        
        ent.setOrientation( currentOrientation );    
    }

    
    @Override
    public DebugInformation getDebugInformation() {    
        return super.getDebugInformation().add("orientation", Math.toDegrees(destinationOrientation));
    }
}
