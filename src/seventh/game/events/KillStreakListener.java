/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

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
