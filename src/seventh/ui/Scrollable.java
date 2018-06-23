/*
 * see license.txt 
 */
package seventh.ui;

import seventh.math.Rectangle;

/**
 * @author Tony
 *
 */
public interface Scrollable {

    Rectangle getViewport();
    int getTotalHeight();
    int getTotalWidth();
}
