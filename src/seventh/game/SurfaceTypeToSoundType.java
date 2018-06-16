/*
 * see license.txt 
 */
package seventh.game;

import seventh.map.Tile.SurfaceType;
import seventh.shared.SoundType;

/**
 * Simple utility class to convert {@link SurfaceType}'s to {@link SoundType}'s
 * 
 * @author Tony
 *
 */
public class SurfaceTypeToSoundType {

    /**
     * Converts the {@link SurfaceType} to a sound for foot steps
     * @param surface
     * @return the appropriate foot steps sound
     */
    public static SoundType toSurfaceSoundType(SurfaceType surface) {
        SoundType result = SoundType.SURFACE_NORMAL;
        switch(surface) {
            case CEMENT:
                result = SoundType.SURFACE_NORMAL;
                break;
            case DIRT:
                result = SoundType.SURFACE_DIRT;
                break;
            case GRASS:
                result = SoundType.SURFACE_GRASS;
                break;
            case METAL:
                result = SoundType.SURFACE_METAL;
                break;
            case SAND:
                result = SoundType.SURFACE_SAND;
                break;
            case UNKNOWN:
                result = SoundType.SURFACE_NORMAL;
                break;
            case WATER:
                result = SoundType.SURFACE_WATER;
                break;
            case WOOD:
                result = SoundType.SURFACE_WOOD;
                break;
            default:
                break;
            
        }
        return result;
    }

    
    /**
     * Converts to the appropriate impact sound (for bullets)
     * @param surface
     * @return
     */
    public static SoundType toImpactSoundType(SurfaceType surface) {
        SoundType result = SoundType.IMPACT_DEFAULT;
        switch(surface) {
            case CEMENT:
                result = SoundType.IMPACT_DEFAULT; // TODO
                break;
            case DIRT:
                result = SoundType.IMPACT_DEFAULT;
                break;
            case GRASS:
                result = SoundType.IMPACT_FOLIAGE;
                break;
            case METAL:
                result = SoundType.IMPACT_METAL;
                break;
            case SAND:
                result = SoundType.IMPACT_DEFAULT;
                break;
            case UNKNOWN:
                result = SoundType.IMPACT_DEFAULT;
                break;
            case WATER:
                result = SoundType.IMPACT_DEFAULT; // TODO
                break;
            case WOOD:
                result = SoundType.IMPACT_WOOD;
                break;
            case GLASS:
                result = SoundType.IMPACT_GLASS;
                break;
            default:
                break;
            
        }
        return result;
    }
}
