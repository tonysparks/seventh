/*
 * see license.txt
 */
package seventh.client.inputs;

import seventh.client.gfx.Cursor;
import seventh.shared.TimeStep;

/**
 * Handles obtaining player input from an input device and maps those inputs
 * to game actions.
 * 
 * @author Tony
 *
 */
public interface GameController {

    
    /**
     * Polls the input device for setting the game actions
     * 
     * @param timeStep
     * @param keyMap
     * @param cursor
     * @param inputKeys
     * @return the inputKeys denoting what was pressed
     */
    public int pollInputs(TimeStep timeStep, KeyMap keyMap, Cursor cursor, int inputKeys);
}
