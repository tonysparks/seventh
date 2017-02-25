/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.ai.basic.Brain;
import seventh.shared.Cons;
import seventh.shared.TimeStep;

/**
 * Allows for scripting actions
 * 
 * @author Tony
 *
 */
public class ScriptedAction extends AdapterAction {

    private CompositeAction goal;
    private LeoObject goalFunction;
    private boolean isFunctionDone;
    
    private String name;
    
    /**
     * @param name
     * @param goalFunction
     */
    public ScriptedAction(String name, LeoObject goalFunction) {
        this.goalFunction = goalFunction;
        this.name = name;
        this.goal = new CompositeAction(name);
        
        this.isFunctionDone = false;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
     */
    @Override
    public void interrupt(Brain brain) {
        goal.interrupt(brain);
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
     */
    @Override
    public void resume(Brain brain) {
        goal.resume(brain);
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
     */
    @Override
    public void update(Brain brain, TimeStep timeStep) {
        goal.update(brain, timeStep);
        
        if(goal.isFinished(brain)) {
            try {
                LeoObject result = goalFunction.xcall(Leola.toLeoObject(brain), Leola.toLeoObject(goal), Leola.toLeoObject(goal.getActionResult()));
                if(!LeoObject.isTrue(result)) {
                    this.isFunctionDone = true;
                }
                else {
                    Object obj = result.getValue();
                    if(obj instanceof Action) {
                        Action newAction = (Action)obj;
                        goal.addFirstAction(newAction);
                    }
                }
            }
            catch(Throwable e) {
                Cons.println("Unable to execute scripted goal: " + e);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#isFinished(seventh.ai.basic.Brain)
     */
    @Override
    public boolean isFinished(Brain brain) {
        return goal.isFinished(brain) && this.isFunctionDone;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#getActionResult()
     */
    @Override
    public ActionResult getActionResult() {
        return goal.getActionResult();
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#getDebugInformation()
     */
    @Override
    public DebugInformation getDebugInformation() {    
        DebugInformation me = new DebugInformation();
        me.add("type", this.name);
        me.add("goal", this.goal);
        return me;
    }
}
