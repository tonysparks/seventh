/*
 * see license.txt 
 */
package seventh.ai.basic.actions;


/**
 * The result of completing an {@link Action}
 * 
 * @author Tony
 *
 */
public class ActionResult {

    public enum Result {
        SUCCESS,
        FAILURE,
        NONE,
    }
    
    private Result result;
    
    /* Any stored value */
    private Object value;
    
    /**
     * @param result
     * @param value
     */
    public ActionResult(Result result, Object value) {
        this.result = result;
        this.value = value;
    }
    
    /**
     * @param value
     */
    public ActionResult(Object value) {
        this(Result.SUCCESS, value);
    }
    
    /**
     */
    public ActionResult() {
        this(Result.NONE, null);
    }
    
    /**
     * Sets this state from the supplied one
     * @param r
     */
    public void set(ActionResult r) {
        this.result = r.result;
        this.value = r.value;
    }
    
    /**
     * @return true if successful
     */
    public boolean isSuccessful() {
        return result==Result.SUCCESS;
    }
    
    /**
     * @return true if a failure
     */
    public boolean isFailure() {
        return result ==Result.FAILURE;
    }
    
    public void setSuccess() {
        result=Result.SUCCESS;
    }
    
    public void setFailure() {
        result=Result.FAILURE;
    }
    
    public void setSuccess(Object value) {
        this.result=Result.SUCCESS;
        this.value = value;
    }
    
    public void setFailure(Object value) {
        this.result=Result.FAILURE;
        this.value = value;
    }
    
    /**
     * @param result the result to set
     */
    public void setResult(Result result) {
        this.result = result;
    }
    
    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * @return the result
     */
    public Result getResult() {
        return result;
    }
    
    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

}
