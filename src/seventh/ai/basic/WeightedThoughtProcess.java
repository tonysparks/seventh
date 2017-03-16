/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.ai.basic.actions.ConcurrentAction;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.WeightedAction;
import seventh.ai.basic.actions.evaluators.AttackActionEvaluator;
import seventh.ai.basic.actions.evaluators.AvoidGrenadeActionEvaluator;
import seventh.ai.basic.actions.evaluators.CommandActionEvaluator;
import seventh.ai.basic.actions.evaluators.DefendSelfActionEvaluator;
import seventh.ai.basic.actions.evaluators.DoNothingEvaluator;
import seventh.ai.basic.actions.evaluators.ExploreActionEvaluator;
import seventh.ai.basic.actions.evaluators.HandleDoorActionEvaluator;
import seventh.ai.basic.actions.evaluators.InvestigateActionEvaluator;
import seventh.ai.basic.actions.evaluators.ReloadWeaponEvaluator;
import seventh.ai.basic.actions.evaluators.StrategyEvaluator;
import seventh.ai.basic.actions.evaluators.SwitchWeaponEvaluator;
import seventh.ai.basic.teamstrategy.TeamStrategy;
import seventh.shared.Randomizer;
import seventh.shared.TimeStep;

/**
 * Weighted thought process means it attempts to make fuzzy decisions based on the surrounding environment and
 * circumstances.
 * 
 * @author Tony
 *
 */
public class WeightedThoughtProcess implements ThoughtProcess {
    
    private ConcurrentAction currentGoal;
    
    /**
     * @param teamStrategy
     * @param brain
     */
    public WeightedThoughtProcess(TeamStrategy teamStrategy, Brain brain) {
        World world = brain.getWorld();
        
        Actions goals = world.getGoals();        
        Randomizer rand = world.getRandom();
        this.currentGoal = new ConcurrentAction(                 
                // high level goals
                new WeightedAction(brain.getConfig(), "highLevelGoal",
                                        new AvoidGrenadeActionEvaluator(goals, rand.getRandomRange(0.8, 0.9), 0.9),
                                        new AttackActionEvaluator(goals, rand.getRandomRangeMin(0.85), 0.82),
                                        new DefendSelfActionEvaluator(goals, rand.getRandomRangeMin(0.85), 0.81),
                                        new CommandActionEvaluator(goals, rand.getRandomRange(0.7, 0.8), 0.8),
                                        new InvestigateActionEvaluator(goals, rand.getRandomRange(0.5, 0.9), 0.6),
//                                        new RideVehicleEvaluator(goals, 1.0f,1f),//brain.getRandomRange(0.5, 0.7), 0.51),
                                        new StrategyEvaluator(teamStrategy, goals, rand.getRandomRange(0.2, 0.6), 0),
                                        new ExploreActionEvaluator(goals, rand.getRandomRange(0.1, 0.5), 0.5)                                            
                ),
                
                // Auxiliary goals, ones that do not impact the high level goals
                new WeightedAction(brain.getConfig(), "auxiliaryGoal",
                                        new ReloadWeaponEvaluator(goals, rand.getRandomRange(0.3, 0.8), 0),
                                        new SwitchWeaponEvaluator(goals, rand.getRandomRange(0.3, 0.8), 0),
                                        new HandleDoorActionEvaluator(goals, 0, 1),
                                        new DoNothingEvaluator(goals, rand.getRandomRange(0.4, 0.9), 0)
                )
        );
        
    }



    /* (non-Javadoc)
     * @see seventh.ai.basic.ThoughtProcess#onSpawn(seventh.ai.basic.Brain)
     */
    @Override
    public void onSpawn(Brain brain) {
        brain.getMotion().pickWeapon();
        
        this.currentGoal.cancel();
        this.currentGoal.start(brain);
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.ThoughtProcess#onKilled(seventh.ai.basic.Brain)
     */
    @Override
    public void onKilled(Brain brain) {
        this.currentGoal.cancel();
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.ThoughtProcess#think(seventh.shared.TimeStep, seventh.ai.basic.Brain)
     */
    @Override
    public void think(TimeStep timeStep, Brain brain) {        
        this.currentGoal.update(brain, timeStep);
    }
        
    /* (non-Javadoc)
     * @see seventh.shared.Debugable#getDebugInformation()
     */
    @Override
    public DebugInformation getDebugInformation() {
        DebugInformation info = new DebugInformation();
        //info.add("goal", this.currentGoal);
        info.add("goal", this.currentGoal.getAction(0));
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
