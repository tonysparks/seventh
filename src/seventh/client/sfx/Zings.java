/*
 * see license.txt
 */
package seventh.client.sfx;

import seventh.client.ClientGame;
import seventh.client.entities.ClientBullet;
import seventh.client.entities.ClientControllableEntity;
import seventh.client.entities.ClientEntities;
import seventh.client.entities.ClientEntity;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class Zings {

    public static class ZingRecord {
        private long lastTimePlayed;
        
        public void playZing(long gameClock, int id, ClientEntity ent) {
            if(lastTimePlayed + 1_000 < gameClock) {
                Sounds.playSound(Sounds.bulletZing, id, ent.getCenterPos());
                lastTimePlayed = gameClock;                
            }
        }
    }
    
    private ZingRecord[] zings;
    private ClientGame game;
    private ClientEntities entities;
    /**
     * 
     */
    public Zings(ClientGame game) {
        this.game = game;
        this.entities = game.getEntities();
        this.zings = new ZingRecord[entities.getMaxNumberOfEntities()];
        for(int i = 0; i < this.zings.length; i++) {
            this.zings[i] = new ZingRecord();
        }
    }
    
    /**
     * Resets so this can be used again
     */
    public void reset() {
        for(int i = 0; i < this.zings.length; i++) {
            this.zings[i].lastTimePlayed = 0;
        }
    }
    
    /**
     * checks to see if there are any bullets shooting by the player, if there
     * are, it plays some bullet zing sounds
     * 
     * @param timeStep
     */
    public void checkForBulletZings(TimeStep timeStep) {
        long gameClock = timeStep.getGameClock();
        
        ClientControllableEntity localEntity = game.getLocalPlayerFollowingEntity();
        
        if(localEntity != null) {
            ClientEntity[] entityList = game.getEntities().getEntities();
            int size = entityList.length;
            for(int i = 0; i < size; i++) {
                ClientEntity ent = entityList[i];
                if(ent != null) {
                    if(ent instanceof ClientBullet) {
                        ClientBullet bullet = (ClientBullet)ent;
                        if(ent.isAlive() && ent.isRelativelyUpdated(300) && bullet.getOwnerId() != localEntity.getId()) {
                            if(localEntity.inEarShot(ent)) {
                                zings[ent.getId()].playZing(gameClock, localEntity.getId(), ent);
                                game.getCamera().addShake(100, 5.2f);
                            }
                        }
                    }
                }
            }
        }
    }

}
