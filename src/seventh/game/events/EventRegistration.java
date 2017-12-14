/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventDispatcher;
import leola.frontend.listener.EventListener;
import leola.vm.types.LeoObject;
import seventh.shared.Cons;

/**
 * Registers {@link EventListener}s to events for scripting
 * 
 * @author Tony
 *
 */
public class EventRegistration {
    
    private EventDispatcher dispatcher;

    /**
     * @param dispatcher
     */
    public EventRegistration(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    private void callFunction(String eventName, LeoObject function, Object event) {
        if(function!=null) {
            LeoObject result = function.call(LeoObject.valueOf(event));
            if(result.isError()) {
                Cons.println("*** ERROR: Calling '" + eventName + "' event listener - " + result.toString());
            }
        } 
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
                this.dispatcher.addEventListener(BombDisarmedEvent.class, new BombDisarmedListener() {
                    
                    @Override
                    public void onBombDisarmedEvent(BombDisarmedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "bombexplodedevent": {
                this.dispatcher.addEventListener(BombExplodedEvent.class, new BombExplodedListener() {                    
                    @Override
                    public void onBombExplodedEvent(BombExplodedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "bombplantedevent": {
                this.dispatcher.addEventListener(BombPlantedEvent.class, new BombPlantedListener() {                    
                    @Override
                    public void onBombPlanted(BombPlantedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "flagcapturedevent": {
                this.dispatcher.addEventListener(FlagCapturedEvent.class, new FlagCapturedListener() {                                    
                    @Override
                    public void onFlagCapturedEvent(FlagCapturedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "flagreturnedevent": {
                this.dispatcher.addEventListener(FlagReturnedEvent.class, new FlagReturnedListener() {                                        
                    @Override
                    public void onFlagReturnedEvent(FlagReturnedEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "flagstolenevent": {
                this.dispatcher.addEventListener(FlagStolenEvent.class, new FlagStolenListener() {                                        
                    @Override
                    public void onFlagStolenEvent(FlagStolenEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "gameendevent": {
                this.dispatcher.addEventListener(GameEndEvent.class, new GameEndListener() {                                        
                    @Override
                    public void onGameEnd(GameEndEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "killrollevent": {
                this.dispatcher.addEventListener(KillRollEvent.class, new KillRollListener() {                                        
                    @Override
                    public void onKillRoll(KillRollEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "killstreakevent": {
                this.dispatcher.addEventListener(KillStreakEvent.class, new KillStreakListener() {                                        
                    @Override
                    public void onKillStreak(KillStreakEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerawardevent": {
                this.dispatcher.addEventListener(PlayerAwardEvent.class, new PlayerAwardListener() {                                        
                    @Override
                    public void onPlayerAward(PlayerAwardEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerjoinedevent": {
                this.dispatcher.addEventListener(PlayerJoinedEvent.class, new PlayerJoinedListener() {                                        
                    @Override
                    public void onPlayerJoined(PlayerJoinedEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerkilledevent": {
                this.dispatcher.addEventListener(PlayerKilledEvent.class, new PlayerKilledListener() {                                        
                    @Override
                    public void onPlayerKilled(PlayerKilledEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerleftevent": {
                this.dispatcher.addEventListener(PlayerLeftEvent.class, new PlayerLeftListener() {                                        
                    @Override
                    public void onPlayerLeft(PlayerLeftEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "playerspawnedevent": {
                this.dispatcher.addEventListener(PlayerSpawnedEvent.class, new PlayerSpawnedListener() {                                        
                    @Override
                    public void onPlayerSpawned(PlayerSpawnedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "roundendedevent": {
                this.dispatcher.addEventListener(RoundEndedEvent.class, new RoundEndedListener() {                    
                    @Override
                    public void onRoundEnded(RoundEndedEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "roundstartedevent": {
                this.dispatcher.addEventListener(RoundStartedEvent.class, new RoundStartedListener() {                                        
                    @Override
                    public void onRoundStarted(RoundStartedEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "soundemittedevent": {
                this.dispatcher.addEventListener(SoundEmittedEvent.class, new SoundEmitterListener() {                    
                    @Override
                    public void onSoundEmitted(SoundEmittedEvent event) {                    
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "survivorevent": {
                this.dispatcher.addEventListener(SurvivorEvent.class, new SurvivorEventListener() {                    
                    @Override
                    public void onSurvivorEvent(SurvivorEvent event) {
                        callFunction(eventName, function, event);
                    }
                });
                break;
            }
            case "tileremoveevent": {
                this.dispatcher.addEventListener(TileRemovedEvent.class, new TileRemovedListener() {                                        
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
