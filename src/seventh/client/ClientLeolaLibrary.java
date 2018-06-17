/*
 * see license.txt 
 */
package seventh.client;

import java.util.Random;

import leola.vm.Leola;
import leola.vm.exceptions.LeolaRuntimeException;
import leola.vm.lib.LeolaIgnore;
import leola.vm.lib.LeolaLibrary;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoNamespace;
import leola.vm.types.LeoObject;
import seventh.client.entities.ClientEntity;
import seventh.client.gfx.LightSystem;
import seventh.client.gfx.effects.particle_system.Emitters;
import seventh.client.sfx.Sound;
import seventh.game.game_types.GameType;
import seventh.math.Rectangle;
import seventh.shared.Cons;
import seventh.shared.SoundType;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class ClientLeolaLibrary implements LeolaLibrary {

    private ClientGame game;
    
    /**
     * @param game
     */
    public ClientLeolaLibrary(ClientGame game) {
        this.game = game;
    }
    
    @LeolaIgnore
    @Override
    public void init(Leola leola, LeoNamespace namespace) throws LeolaRuntimeException {
        leola.putIntoNamespace(this, namespace);
        leola.loadStatics(Emitters.class);
    }
    
    
    /**
     * Adds a Timer to the client game world.
     * 
     * @param timer
     * @return true if it was added
     */
    public boolean addGameTimer(Timer timer) {
        return this.game.addGameTimer(timer);
    }
    
    /**
     * Adds a {@link Timer} which will execute the supplied {@link LeoObject}.
     * 
     * @param loop
     * @param endTime
     * @param function
     * @return true if it was added
     */
    public boolean addGameTimer(boolean loop, long endTime, final LeoObject function) {
        return addGameTimer(new Timer(loop, endTime) {
            
            @Override
            public void onFinish(Timer timer) {
                function.xcall();
            }
        });
    }
    
    /**
     * Adds the {@link LeoObject} callback as a the timer function, which will also randomize the start/end time.
     * 
     * @param loop
     * @param minStartTime
     * @param maxEndTime
     * @param function
     * @return true if the timer was added;false otherwise
     */
    public boolean addRandomGameTimer(boolean loop, final long minStartTime, final long maxEndTime, final LeoObject function) {
        return addGameTimer(new Timer(loop, minStartTime) {            
            @Override
            public void onFinish(Timer timer) {
                LeoObject result = function.call();
                if(result.isError()) {
                    Cons.println("*** ERROR: Script error in GameTimer: " + result);
                }
                
                long delta = maxEndTime - minStartTime;
                int millis = (int)delta / 100;
                timer.setEndTime(minStartTime + getRandom().nextInt(millis) * 100);
            }
        });
    }


    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *                          Game State Operations
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    
    /**
     * Get the light system
     * 
     * @return
     */
    public LightSystem getLightSystem() {
        return this.game.getLightSystem();
    }

    
    /**
     * @return the gameType
     */
    public GameType.Type getGameType() {
        return this.game.getGameType();
    }

    /**
     * @return the random
     */
    public Random getRandom() {
        return this.game.getRandom();
    }
    
    
    /**
     * Get all the entities
     * 
     * @param results
     * @return
     */
    public LeoArray getEntities(LeoArray results) {
        LeoArray r = (results != null) ? results : new LeoArray();
        r.clear();
        
        ClientEntity[] ents = this.game.getEntities().getEntities();
        for(int i = 0; i < ents.length; i++) {
            ClientEntity ent = ents[i];
            if(ent!=null && !ent.isDestroyed()) {
                r.add(ent.asScriptObject());
            }
        }
        
        return r;
    }
    
    /**
     * Get all the entities of a specific type
     * 
     * @param results
     * @return
     */
    public LeoArray getEntitiesOfType(LeoArray results, String type) {
        LeoArray r = (results != null) ? results : new LeoArray();
        r.clear();
        
        ClientEntity[] ents = this.game.getEntities().getEntities();
        for(int i = 0; i < ents.length; i++) {
            ClientEntity ent = ents[i];
            if(ent!=null && !ent.isDestroyed()) {
                String entType = ent.getType().name();
                if(entType.equalsIgnoreCase(type) || 
                   entType.replace("_", "").equalsIgnoreCase(type)) {
                    r.add(ent.asScriptObject());
                }
            }
        }
        
        return r;
    }
    
    /**
     * Get all the entities touching the supplied bounds
     * 
     * @param results
     * @return
     */
    public LeoArray getEntitiesInBounds(LeoArray results, Rectangle bounds) {
        LeoArray r = (results != null) ? results : new LeoArray();
        r.clear();
        
        ClientEntity[] ents = this.game.getEntities().getEntities();
        for(int i = 0; i < ents.length; i++) {
            ClientEntity ent = ents[i];
            if(ent!=null && !ent.isDestroyed()) {
                if(ent.getBounds().intersects(bounds)) {
                    r.add(ent.asScriptObject());
                }                
            }
        }
        
        return r;
    }
    
    /**
     * Get all the entities meeting the supplied condition
     * 
     * @param results
     * @return
     */
    public LeoArray getEntitiesFilter(LeoArray results, LeoObject function) {
        LeoArray r = (results != null) ? results : new LeoArray();
        r.clear();
        
        ClientEntity[] ents = this.game.getEntities().getEntities();
        for(int i = 0; i < ents.length; i++) {
            ClientEntity ent = ents[i];
            if(ent!=null && !ent.isDestroyed()) {
                LeoObject result = function.call(ent.asScriptObject());
                if(result.isError()) {
                    Cons.println("*** Error calling getEntitiesFilter failed: " + result.toString());
                }
                else if(result.isTrue()) {
                    r.add(ent.asScriptObject());
                }
            }
        }
        
        return r;
    }
    
    /**
     * Get a {@link ClientEntity} by the supplied id
     * 
     * @param id
     * @return
     */
    public ClientEntity getEntityById(int id) {
        ClientEntity ent = this.game.getEntities().getEntity(id);
        if(ent!=null && !ent.isDestroyed()) {
            return ent;
        }
        
        return null;
    }
    
    /**
     * Get the {@link ClientPlayer} by the supplied id
     * 
     * @param playerId
     * @return
     */
    public ClientPlayer getPlayerById(int playerId) {
        return this.game.getPlayers().getPlayer(playerId);
    }
    
    
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *                          HUD Operations
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    
    
    /**
     * Display a message to the HUD
     * 
     * @param message
     */
    public void msg(String message) {
        this.game.postMessage(message);
    }
    
    
    
    
    
    

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *                          Sound Operations
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    
    
    
    /**
     * API for playing a sound
     * 
     * @param soundType
     * @param x
     * @param y
     * @return the {@link Sound}
     */
    public Sound playSound(SoundType soundType, float x, float y) {
        return this.game.playSound(soundType, x, y); 
    }
    
    /**
     * API for playing a sound
     * 
     * @param snd
     * @param x
     * @param y
     * @return the {@link Sound}
     */
    public Sound playSound(Sound snd, float x, float y) {        
        return this.game.playSound(snd, x, y);
    }
    
    /**
     * API for playing a global sound
     * 
     * @param snd
     * @return the {@link Sound}
     */
    public Sound playGlobalSound(Sound snd) {
        return this.game.playGlobalSound(snd);
    }
        
    /**
     * API for loading a sound
     * 
     * @param path
     * @return the {@link Sound} 
     */
    public Sound loadSound(String path) {
        return this.game.loadSound(path);
    }
    
    /**
     * API for unloading a sound
     * 
     * @param path
     */
    public void unloadSound(String path) {
        this.game.unloadSound(path);
    }
    
    /**
     * API for unloading a sound
     * 
     * @param path
     */
    public void unloadSound(Sound snd) {
        this.game.unloadSound(snd);
    }
    
}
