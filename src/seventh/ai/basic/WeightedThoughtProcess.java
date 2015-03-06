/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.Random;

import seventh.ai.basic.actions.Goal;
import seventh.ai.basic.actions.Goals;
import seventh.ai.basic.actions.WeightedGoal;
import seventh.ai.basic.actions.evaluators.AttackActionEvaluator;
import seventh.ai.basic.actions.evaluators.ExploreActionEvaluator;
import seventh.shared.DebugDraw;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class WeightedThoughtProcess implements ThoughtProcess {
	
	private Goal currentGoal;
	
	/**
	 * 
	 */
	public WeightedThoughtProcess(Brain brain) {
		
		Goals goals = brain.getWorld().getGoals();
		Random rand = brain.getWorld().getRandom();		
		this.currentGoal = new WeightedGoal(brain, new AttackActionEvaluator(goals, Math.max(rand.nextDouble(), 0.1)),
												   new ExploreActionEvaluator(goals, Math.max(rand.nextDouble(), 0.1)));
	}



	/* (non-Javadoc)
	 * @see seventh.ai.basic.ThoughtProcess#onSpawn(seventh.ai.basic.Brain)
	 */
	@Override
	public void onSpawn(Brain brain) {
		this.currentGoal.cancel();
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
		
		DebugDraw.drawString("Thinking: " + toString(), 100, 200, 0xffffffff);
	}
		
	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation info = new DebugInformation();
		info.add("goal", this.currentGoal);
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
