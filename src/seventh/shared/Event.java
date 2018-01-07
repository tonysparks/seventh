/*
    Leola Programming Language
    Author: Tony Sparks
    See license.txt
*/
package seventh.shared;


/**
 * A consumable event of interest.
 * 
 * @author Tony
 *
 */
public abstract class Event {

    /**
     * The object which this event has
     * spawned from.
     */
    private Object source;
        
    /**
     * If this event has been consumed
     */
    private boolean consumed;

    /**
     * Time the event was created.
     */
//    private long creationTime;
    
    /**
     * Uses the default method id.
     * @param source
     */
    public Event(Object source) {
        this.source = source;
        

//        /* This time is in java time */
//        this.creationTime = System.currentTimeMillis();
    }
    
    
    /**
     * Consume this event
     */
    public void consume() {
        this.consumed=true;
    }

    /**
     * @return the consumed
     */
    public boolean isConsumed() {
        return consumed;
    }

    /**
     * Unconsume this event.  
     */
    public void unconsume() {
        this.consumed = false;
    }
    
    /**
     * @return the creationTime
     */
//    public long getCreationTime() {
//        return creationTime;
//    }
    
    /**
     * Get the source
     * @return the source of which this event was created.
     */
    public Object getSource() {
        return this.source;
    }


    /**
     * @param source the source to set
     */
    public void setSource(Object source) {
        this.source = source;
    }        
        
}

