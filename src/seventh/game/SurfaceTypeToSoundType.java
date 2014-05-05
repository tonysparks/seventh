/*
 * see license.txt 
 */
package seventh.game;

import seventh.map.Tile.SurfaceType;

/**
 * @author Tony
 *
 */
public class SurfaceTypeToSoundType {

	public static SoundType toSoundType(SurfaceType surface) {
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

}
