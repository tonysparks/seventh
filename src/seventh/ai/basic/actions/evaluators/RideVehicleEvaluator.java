/*
 * see license.txt
 */
package seventh.ai.basic.actions.evaluators;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.game.vehicles.Vehicle;

/**
 * @author Tony
 *
 */
public class RideVehicleEvaluator extends ActionEvaluator {

    private List<Vehicle> vehiclesToRide;
    private Vehicle vehicleToRide;
    
    /**
     * @param goals
     * @param characterBias
     * @param keepBias
     */
    public RideVehicleEvaluator(Actions goals, double characterBias, double keepBias) {
        super(goals, characterBias, keepBias);
        this.vehiclesToRide = new ArrayList<Vehicle>();
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double calculateDesirability(Brain brain) {
        List<Vehicle> vehicles = brain.getWorld().getVehicles();
        
        double score = 0;
        if(!vehicles.isEmpty()) {
            vehiclesToRide.clear();
            for(int i = 0; i < vehicles.size(); i++) {
                Vehicle v = vehicles.get(i);
                if(v.isAlive() && !v.hasOperator()) {
                    vehiclesToRide.add(v);
                }
            }
            
            Vehicle v = brain.getEntityOwner().getClosest(vehiclesToRide); 
            if(v != null) {
                float distanceToVehicle = brain.getEntityOwner().distanceFromSq(v);
                float reasonableDistance = (32f*10f) * (32f*10f);
                score = 1.0 - (distanceToVehicle/reasonableDistance);
                score = Math.max(0, score);
                this.vehicleToRide = v;
            }
        }
        
        return score;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {
        return getGoals().operateVehicle(vehicleToRide);
    }

}
