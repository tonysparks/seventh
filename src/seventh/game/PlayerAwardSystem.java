/*
 * see license.txt 
 */
package seventh.game;

import leola.frontend.listener.EventDispatcher;
import seventh.game.events.KillRollEvent;
import seventh.game.events.KillStreakEvent;
import seventh.game.events.PlayerAwardEvent;
import seventh.game.events.PlayerJoinedEvent;
import seventh.game.events.PlayerJoinedListener;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.PlayerLeftEvent;
import seventh.game.events.PlayerLeftListener;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundEndedListener;
import seventh.game.events.RoundStartedEvent;
import seventh.game.events.RoundStartedListener;
import seventh.shared.SeventhConstants;

/**
 * Keeps track of player stats for kill streaks and bonuses
 * 
 * @author Tony
 *
 */
public class PlayerAwardSystem {
    
    /**
     * Type of award
     * 
     * @author Tony
     *
     */
    public static enum Award {
        FirstBlood,
        KillStreak,
        KillRoll,
        
        // death awards
        Coward,
        Excellence,
        BeastMode,
        FavreMode,
        
        // kill awards
        Participation,
        Marksman,
        BadMan,
        Rodgers,
        ;
        
        public byte netValue() {
            return (byte)ordinal();
        }
        
        private static Award[] values = values();
        
        public static Award fromNetValue(byte b) {
            return values[b];
        }
        
        /**
         * @return number of bits it takes to represent this
         * enum
         */
        public static int numOfBits() {
            return 5;
        }

    }
    

    private static class PlayerStats {
        
        int kills;
        int deaths;
        int killStreak;
        int highestKillStreak;
        
        int killRoll;
        long lastKillTime;
        
        Player player;
        EventDispatcher dispatcher;
        
        public PlayerStats(Player player, EventDispatcher dispatcher) {
            this.player = player;
            this.dispatcher = dispatcher;
        }
                
        public void roundReset() {
            this.killStreak = 0;
            this.lastKillTime = 0;
        }
        
        public void roundEnded() {
            if(deaths == 0) {
                if(kills == 0) {
                    // send out coward award
                    dispatcher.queueEvent(new PlayerAwardEvent(this, player, Award.Coward));
                }
                else if(kills < 5) {
                    // send out 
                    dispatcher.queueEvent(new PlayerAwardEvent(this, player, Award.Excellence));                    
                }
                else if(kills < 10) {
                    // send out 
                    dispatcher.queueEvent(new PlayerAwardEvent(this, player, Award.BeastMode));                    
                }
                else {
                    // send out 
                    dispatcher.queueEvent(new PlayerAwardEvent(this, player, Award.FavreMode));                    
                }
            }
            
            float ratio = 1.0f;
            if(deaths>0) {
                ratio = kills / deaths;
            }
            
            if(kills == 0) {
                // send out worthless award
                dispatcher.queueEvent(new PlayerAwardEvent(this, player, Award.Participation));
            }
            else {
            
                if(ratio > .90f) {
                    dispatcher.queueEvent(new PlayerAwardEvent(this, player, Award.Rodgers));    
                }
                else if(ratio > .70f) {
                    dispatcher.queueEvent(new PlayerAwardEvent(this, player, Award.BadMan));
                }
                else if(ratio > .60f) {
                    dispatcher.queueEvent(new PlayerAwardEvent(this, player, Award.Marksman));
                }
            }
            
        }
        
        public void addKill() {                        
            this.killStreak++;
            if(this.killStreak > this.highestKillStreak) {
                this.highestKillStreak = this.killStreak;
            }
            
            // if the kill streak is worthy, send out an event
            switch(this.killStreak) {
                case 3:
                case 5:                    
                case 10:                    
                case 15:
                    dispatcher.queueEvent(new KillStreakEvent(this, player, this.killStreak));
                    break;                
            }
            
            // TODO - use game time, instead of wall time
            long killTime = System.currentTimeMillis();            
            if(killTime-this.lastKillTime < 3000) {
                this.killRoll++;
                
                // if we have multiple kills within the kill time frame,
                // send out an event of this players awesomeness
                if(this.killRoll>1) {
                    dispatcher.queueEvent(new KillRollEvent(this, player, this.killRoll));
                }
            }
            else {
                this.killRoll = 1;
            }
            
            this.lastKillTime = killTime;
            
            this.kills++;
        }
        
        public void addDeath() {
            this.killStreak = 0;
            this.killRoll = 0;
            this.deaths++;
        }
    }
    
    private PlayerStats[] stats;
    private boolean firstBlood;
    
    /**
     * 
     */
    public PlayerAwardSystem(Game game) {
        this.stats = new PlayerStats[SeventhConstants.MAX_PLAYERS];
        
        final EventDispatcher dispatcher = game.getDispatcher();
        
        dispatcher.addEventListener(PlayerJoinedEvent.class, new PlayerJoinedListener() {
            
            @Override
            public void onPlayerJoined(PlayerJoinedEvent event) {
                stats[event.getPlayer().getId()] = new PlayerStats(event.getPlayer(), dispatcher);
            }
        });
        
        dispatcher.addEventListener(PlayerLeftEvent.class, new PlayerLeftListener() {
            
            @Override
            public void onPlayerLeft(PlayerLeftEvent event) {
                //stats[event.getPlayer().getId()] = null; // Do we need to do this?                
            }        
        });
        
        dispatcher.addEventListener(PlayerKilledEvent.class, new PlayerKilledListener() {
            
            @Override
            public void onPlayerKilled(PlayerKilledEvent event) {
                Player killed = event.getPlayer();
                
                int killerId = event.getKillerId();
                if(isValidPlayerId(killerId)) {
                    if(killed!=null && !killed.isTeammateWith(killerId)) {                    
                        stats[killerId].addKill();
                        
                        if(firstBlood) {                            
                            dispatcher.queueEvent(new PlayerAwardEvent(this, stats[killerId].player, Award.FirstBlood));
                        }
                    }
                }
                
                if(killed!=null && isValidPlayerId(killed.getId())) {
                    stats[killed.getId()].addDeath();
                }
                
                firstBlood = false;
            }
        });
        
        
        dispatcher.addEventListener(RoundStartedEvent.class, new RoundStartedListener() {
            
            @Override
            public void onRoundStarted(RoundStartedEvent event) {
                firstBlood = true;
                
                for(int i = 0; i < stats.length; i++) {
                    if(stats[i]!=null) {
                        stats[i].roundReset();
                    }
                }
            }
        });
        
        dispatcher.addEventListener(RoundEndedEvent.class, new RoundEndedListener() {
            
            @Override
            public void onRoundEnded(RoundEndedEvent event) {
                for(int i = 0; i < stats.length; i++) {
                    if(stats[i]!=null) {
                        stats[i].roundEnded();
                    }
                }
            }
        });
    }
    
    private boolean isValidPlayerId(int playerId) {
        return playerId >= 0 && playerId < this.stats.length && this.stats[playerId] != null;
    }   

}
