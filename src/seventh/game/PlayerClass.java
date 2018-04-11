/*
 * see license.txt 
 */
package seventh.game;

import static seventh.game.entities.Entity.Type.FLAME_THROWER;
import static seventh.game.entities.Entity.Type.GRENADE;
import static seventh.game.entities.Entity.Type.HAMMER;
import static seventh.game.entities.Entity.Type.M1_GARAND;
import static seventh.game.entities.Entity.Type.PISTOL;
import static seventh.game.entities.Entity.Type.RISKER;
import static seventh.game.entities.Entity.Type.ROCKET_LAUNCHER;
import static seventh.game.entities.Entity.Type.SHOTGUN;
import static seventh.game.entities.Entity.Type.SPRINGFIELD;
import static seventh.game.entities.Entity.Type.THOMPSON;

import java.util.Arrays;
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

    public static class WeaponEntry {
        public Type type;
        public int ammoBag;
        
        public WeaponEntry(Type type, int ammoBag) {
            this.type = type;
            this.ammoBag = ammoBag;
        }
    }
    
    private static WeaponEntry entry(Type type, int ammoBag) {
        return new WeaponEntry(type, ammoBag);
    }
    private static WeaponEntry entry(Type type) {
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
                                                Arrays.asList(entry(HAMMER, 1)), 
                                                Arrays.asList(entry(HAMMER, 1), entry(PISTOL), entry(GRENADE, 2)), HAMMER);
    
    public static PlayerClass Scout    = new PlayerClass(0, 50, 0, 
                                                Arrays.asList(entry(THOMPSON),                                                               
                                                              entry(SHOTGUN)), 
                                                
                                                Arrays.asList(entry(PISTOL), entry(GRENADE, 2)), THOMPSON);
    
    
    public static PlayerClass Infantry = new PlayerClass(0, 0, 0, 
                                                Arrays.asList(entry(THOMPSON),                                                               
                                                              entry(M1_GARAND),
                                                              entry(SPRINGFIELD),
                                                              entry(SHOTGUN),
                                                              entry(RISKER)), 
                                                
                                                Arrays.asList(entry(PISTOL), entry(GRENADE, 1)), THOMPSON);

    public static PlayerClass Demolition= new PlayerClass(0, -20, 20, 
                                                Arrays.asList(entry(FLAME_THROWER),
                                                              entry(ROCKET_LAUNCHER)), 
                                                
                                                Arrays.asList(entry(THOMPSON), entry(PISTOL), entry(GRENADE, 1)), ROCKET_LAUNCHER);    
    
    
    
    public static PlayerClass Default   = new PlayerClass(0, 0, 0, 
                                                Arrays.asList(entry(THOMPSON),                                                               
                                                              entry(M1_GARAND),
                                                              entry(SPRINGFIELD),
                                                              entry(SHOTGUN),
                                                              entry(RISKER),
                                                              entry(FLAME_THROWER),
                                                              entry(ROCKET_LAUNCHER)), 
                                                
                                                Arrays.asList(entry(PISTOL), entry(GRENADE, 1)), THOMPSON);
    
    private int healthMultiplier;
    private int speedMultiplier;
    private int damageMultiplier;
    
    private List<WeaponEntry> availableWeapons;
    private List<WeaponEntry> defaultWeapons;
    
    private Type defaultPrimaryWeapon;
    
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
                       Type defaultPrimaryWeapon) {
        
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
    public Type getDefaultPrimaryWeapon() {
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
            if(entry.type == weaponType) {
                return entry;
            }
        }
        
        return null;
    }
}
