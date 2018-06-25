/*
 * see license.txt 
 */
package seventh.game.events;

import java.util.ArrayList;
import java.util.List;

import leola.vm.types.LeoObject;
import seventh.math.Pair;
import seventh.shared.Cons;
import seventh.shared.EventDispatcher;
import seventh.shared.EventListener;

/**
 * Registers {@link EventListener}s to events for scripting
 * 
 * @author Tony
 *
 */
public class EventRegistration {
    
    private EventDispatcher dispatcher;
    private List<Pair<Class<?>, EventListener>> registeredListeners;
    
    /**
     * @param dispatcher
     */
    public EventRegistration(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.registeredListeners = new ArrayList<>();
    }

    private void callFunction(String eventName, LeoObject function, Object event) {
        if(function!=null) {
            LeoObject result = function.call(LeoObject.valueOf(event));
            if(result.isError()) {
                Cons.println("*** ERROR: Calling '" + eventName + "' event listener - " + result.toString());
            }
        } 
    }
    
    private void addEventListener(Class<?> type, EventListener listener) {
        this.dispatcher.addEventListener(type, listener);
        this.registeredListeners.add(new Pair<Class<?>, EventListener>(type, listener));
    }
    
    /**
     * Unregisters the supplied listeners
     */
    public void unregisterListeners() {
        for(Pair<Class<?>, EventListener> pair : this.registeredListeners) {
            this.dispatcher.removeEventListener(pair.getFirst(), pair.getSecond());
        }
        
        this.registeredListeners.clear();
    }
    
    /**
     * Adds an {@link EventListener} to the supplied event name
     * 
     * @param eventName the event name to bind the listener to
     * @param function the listener
     */
    public void addEventListener(final String eventName, final LeoObject function) {
        switch(eventName.toLowerCase()) {
            case "bombdisarmedevent": {
                addEventListener(BombDisarmedEvent.class, new BombDisarmedListener() {
                    
                    @Override
                    public void onBombDisarmedEvent(BombDisarmedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "bombexplodedevent": {
                addEventListener(BombExplodedEvent.class, new BombExplodedListener() {                    
                    @Override
                    public void onBombExplodedEvent(BombExplodedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "bombplantedevent": {
                addEventListener(BombPlantedEvent.class, new BombPlantedListener() {                    
                    @Override
                    public void onBombPlanted(BombPlantedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "flagcapturedevent": {
                addEventListener(FlagCapturedEvent.class, new FlagCapturedListener() {                                    
                    @Override
                    public void onFlagCapturedEvent(FlagCapturedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "flagreturnedevent": {
                addEventListener(FlagReturnedEvent.class, new FlagReturnedListener() {                                        
                    @Override
                    public void onFlagReturnedEvent(FlagReturnedEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "flagstolenevent": {
                addEventListener(FlagStolenEvent.class, new FlagStolenListener() {                                        
                    @Override
                    public void onFlagStolenEvent(FlagStolenEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "gameendevent": {
                addEventListener(GameEndEvent.class, new GameEndListener() {                                        
                    @Override
                    public void onGameEnd(GameEndEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "killrollevent": {
                addEventListener(KillRollEvent.class, new KillRollListener() {                                        
                    @Override
                    public void onKillRoll(KillRollEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "killstreakevent": {
                addEventListener(KillStreakEvent.class, new KillStreakListener() {                                        
                    @Override
                    public void onKillStreak(KillStreakEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerawardevent": {
                addEventListener(PlayerAwardEvent.class, new PlayerAwardListener() {                                        
                    @Override
                    public void onPlayerAward(PlayerAwardEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerjoinedevent": {
                addEventListener(PlayerJoinedEvent.class, new PlayerJoinedListener() {                                        
                    @Override
                    public void onPlayerJoined(PlayerJoinedEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerkilledevent": {
                addEventListener(PlayerKilledEvent.class, new PlayerKilledListener() {                                        
                    @Override
                    public void onPlayerKilled(PlayerKilledEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerleftevent": {
                addEventListener(PlayerLeftEvent.class, new PlayerLeftListener() {                                        
                    @Override
                    public void onPlayerLeft(PlayerLeftEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerspawnedevent": {
                addEventListener(PlayerSpawnedEvent.class, new PlayerSpawnedListener() {                                        
                    @Override
                    public void onPlayerSpawned(PlayerSpawnedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "roundendedevent": {
                addEventListener(RoundEndedEvent.class, new RoundEndedListener() {                    
                    @Override
                    public void onRoundEnded(RoundEndedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "roundstartedevent": {
                addEventListener(RoundStartedEvent.class, new RoundStartedListener() {                                        
                    @Override
                    public void onRoundStarted(RoundStartedEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "soundemittedevent": {
                addEventListener(SoundEmittedEvent.class, new SoundEmitterListener() {                    
                    @Override
                    public void onSoundEmitted(SoundEmittedEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "gameevent": {
                addEventListener(GameEvent.class, new GameEventListener() {                    
                    @Override
                    public void onGameEvent(GameEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "tileremoveevent": {
                addEventListener(TileRemovedEvent.class, new TileRemovedListener() {                                        
                    @Override
                    public void onTileRemoved(TileRemovedEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            default: {
                Cons.println("Unknown event type: '" + eventName + "'");
            }
        }
    }
}
