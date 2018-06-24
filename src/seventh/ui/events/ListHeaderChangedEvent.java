/*
 * see license.txt 
 */
package seventh.ui.events;

import seventh.shared.Event;
import seventh.ui.Button;

/**
 * @author Tony
 *
 */
public class ListHeaderChangedEvent extends Event {

    private Button button;
    private boolean added;
    
    /**
     * @param source
     */
    public ListHeaderChangedEvent(Object source, Button button, boolean added) {
        super(source);
        this.button = button;
        this.added = added;
    }
    
    /**
     * @return the button
     */
    public Button getButton() {
        return button;
    }
    
    /**
     * @return the added
     */
    public boolean isAdded() {
        return added;
    }

}
