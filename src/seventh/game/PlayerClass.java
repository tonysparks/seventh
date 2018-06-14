/*
 * see license.txt 
 */
package seventh.game;

import static seventh.game.entities.Entity.Type.KAR98;
import static seventh.game.entities.Entity.Type.M1_GARAND;
import static seventh.game.entities.Entity.Type.MP40;
import static seventh.game.entities.Entity.Type.MP44;
import static seventh.game.entities.Entity.Type.SPRINGFIELD;
import static seventh.game.entities.Entity.Type.THOMPSON;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import seventh.game.entities.Entity.Type;
import seventh.game.entities.PlayerEntity;

/**
 * Represents a {@link PlayerEntity} class, which limits or enhancements
 * their abilities.
 * 
 * @author Tony
 *
 */
public class PlayerClass {
    
    public enum WeaponClass {
        /* American version first, then German */
        
        PISTOL(Type.PISTOL),
        GRENADE(Type.GRENADE),
        SMOKE_GRENADE(Type.SMOKE_GRENADE),
        SMG(THOMPSON, MP40),
        RIFLE(M1_GARAND, MP44),
        RISKER(Type.RISKER),
        SNIPER(SPRINGFIELD, KAR98),
        SHOTGUN(Type.SHOTGUN),
        FLAME_THROWER(Type.FLAME_THROWER),
        ROCKET_LAUNCHER(Type.ROCKET_LAUNCHER),        
        HAMMER(Type.HAMMER)
        ;
        
        private List<Type> weapons;
        
        private WeaponClass(Type ...weapons) {
            this.weapons = Arrays.asList(weapons);
        }
        
        /**
         * @return the weapons
         */
        public Collection<Type> getWeapons() {
            return weapons;
        }
        
        /**
         * If this class contains the weapon
         * 
         * @param weapon
         * @return 
         */
        public boolean hasWeapon(Type weapon) {
            return weapons.contains(weapon);
        }
        
        public Type getTeamWeapon(Team team) {
            if(team.getId() == Team.ALLIED_TEAM_ID) {
                return getAlliedWeapon();
            }
            
            if(team.getId() == Team.AXIS_TEAM_ID) {
                return getAxisWeapon();
            }
            
            return null;
        }
        
        public Type getAlliedWeapon() {
            return weapons.get(0);
        }
        
        public Type getAxisWeapon() {
            if(weapons.size() > 1) {
                return weapons.get(1);
            }
            return weapons.get(0);
        }
    }
    

    public static class WeaponEntry {
        public WeaponClass type;
        public int ammoBag;
        
        public WeaponEntry(WeaponClass type, int ammoBag) {
            this.type = type;
            this.ammoBag = ammoBag;
        }
    }
    
    private static WeaponEntry entry(WeaponClass type, int ammoBag) {
        return new WeaponEntry(type, ammoBag);
    }
    private static WeaponEntry entry(WeaponClass type) {
        return entry(type, -1);
    }
    
    public static PlayerClass fromNet(byte classId) {
        switch(classId) {
            case 1:   return Engineer;
            case 2:   return Scout;
            case 3:   return Infantry;
            case 4:   return Demolition;
            default:  return Default;
        }
    }

    public static byte toNet(PlayerClass playerClass) {
        if(playerClass == Engineer)   return 1;
        if(playerClass == Scout)      return 2;
        if(playerClass == Infantry)   return 3;
        if(playerClass == Demolition) return 4;
        
        return 4;        
    }
    
    public static PlayerClass Engineer = new PlayerClass(100,  0,  20, 
                                                Arrays.asList(entry(WeaponClass.HAMMER, 1)), 
                                                Arrays.asList(entry(WeaponClass.HAMMER, 1), entry(WeaponClass.PISTOL), entry(WeaponClass.GRENADE, 2)), 
                                                WeaponClass.HAMMER);
    
    public static PlayerClass Scout    = new PlayerClass(0, 50, 0, 
                                                Arrays.asList(entry(WeaponClass.SMG),                                                               
                                                              entry(WeaponClass.SHOTGUN)), 
                                                
                                                Arrays.asList(entry(WeaponClass.PISTOL), entry(WeaponClass.GRENADE, 2)), 
                                                WeaponClass.SMG);
    
    
    public static PlayerClass Infantry = new PlayerClass(0, 0, 0, 
                                                Arrays.asList(entry(WeaponClass.SMG),                                                               
                                                              entry(WeaponClass.RIFLE),
                                                              entry(WeaponClass.RISKER),
                                                              entry(WeaponClass.SNIPER),
                                                              entry(WeaponClass.SHOTGUN)), 
                                                
                                                Arrays.asList(entry(WeaponClass.PISTOL), entry(WeaponClass.GRENADE, 1)), 
                                                WeaponClass.SMG);

    public static PlayerClass Demolition= new PlayerClass(0, -20, 20, 
                                                Arrays.asList(entry(WeaponClass.FLAME_THROWER),
                                                              entry(WeaponClass.ROCKET_LAUNCHER)), 
                                                
                                                Arrays.asList(entry(WeaponClass.SMG), entry(WeaponClass.PISTOL), entry(WeaponClass.GRENADE, 1)), 
                                                WeaponClass.ROCKET_LAUNCHER);    
    
    
    
    public static PlayerClass Default   = new PlayerClass(0, 0, 0, 
                                                Arrays.asList(entry(WeaponClass.SMG),                                                               
                                                              entry(WeaponClass.RIFLE),
                                                              entry(WeaponClass.RISKER),
                                                              entry(WeaponClass.SNIPER),
                                                              entry(WeaponClass.SHOTGUN),                                                              
                                                              entry(WeaponClass.FLAME_THROWER),
                                                              entry(WeaponClass.ROCKET_LAUNCHER)), 
                                                
                                                Arrays.asList(entry(WeaponClass.PISTOL), entry(WeaponClass.GRENADE, 1)), 
                                                WeaponClass.SMG);
    
    private int healthMultiplier;
    private int speedMultiplier;
    private int damageMultiplier;
    
    private List<WeaponEntry> availableWeapons;
    private List<WeaponEntry> defaultWeapons;
    
    private WeaponClass defaultPrimaryWeapon;
    
    /**
     * @param healthMultiplier
     * @param speedMultiplier
     * @param damageMultiplier
     * @param availableWeapons
     * @param defaultWeapons
     * @param defaultPrimaryWeapon
     */
    public PlayerClass(int healthMultiplier, 
                       int speedMultiplier, 
                       int damageMultiplier, 
                       List<WeaponEntry> availableWeapons,
                       List<WeaponEntry> defaultWeapons,
                       WeaponClass defaultPrimaryWeapon) {
        
        this.healthMultiplier = healthMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.availableWeapons = availableWeapons;
        this.defaultWeapons = defaultWeapons;
        
        this.defaultPrimaryWeapon = defaultPrimaryWeapon;
    }
    /**
     * @return the healthMultiplier
     */
    public int getHealthMultiplier() {
        return healthMultiplier;
    }
    /**
     * @return the speedMultiplier
     */
    public int getSpeedMultiplier() {
        return speedMultiplier;
    }
    /**
     * @return the damageMultiplier
     */
    public int getDamageMultiplier() {
        return damageMultiplier;
    }
    /**
     * @return the availableWeapons
     */
    public List<WeaponEntry> getAvailableWeapons() {
        return availableWeapons;
    }
    /**
     * @return the defaultWeapons
     */
    public List<WeaponEntry> getDefaultWeapons() {
        return defaultWeapons;
    }
    
    /**
     * @return the defaultPrimaryWeapon
     */
    public WeaponClass getDefaultPrimaryWeapon() {
        return defaultPrimaryWeapon;
    }
    
    
    /**
     * Determines if the supplied weapon type is an available option for this player class
     * 
     * @param weaponType
     * @return true if the weapon is available
     */
    public boolean isAvailableWeapon(Type weaponType) {        
        return getAvailableWeaponEntry(weaponType) != null;
    }
    
    
    /**
     * Get the {@link WeaponEntry} for the supplied weapontype
     * 
     * @param weaponType
     * @return the {@link WeaponEntry} or null if not an option
     */
    public WeaponEntry getAvailableWeaponEntry(Type weaponType) {
        for(int i = 0; i < availableWeapons.size(); i++) {
            WeaponEntry entry = availableWeapons.get(i);
            if(entry.type.hasWeapon(weaponType)) {
                return entry;
            }
        }
        
        return null;
    }
}
