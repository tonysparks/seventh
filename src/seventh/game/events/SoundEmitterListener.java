/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * @author Tony
 *
 */
public interface SoundEmitterListener extends EventListener {

    @EventMethod
    public void onSoundEmitted(SoundEmittedEvent event);
}
