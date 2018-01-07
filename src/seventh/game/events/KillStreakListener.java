/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * Listens for {@link KillStreakEvent}s
 * 
 * @author Tony
 *
 */
public interface KillStreakListener extends EventListener {

    @EventMethod
    public void onKillStreak(KillStreakEvent event);
}
