/*
 * see license.txt 
 */
package seventh.map;

/**
 * Creates {@link MapObject} instances based off of the {@link MapObjectData}
 * 
 * @author Tony
 *
 */
public interface MapObjectFactory {

    public MapObject createMapObject(MapObjectData data);
    public Tile createMapTile(TilesetAtlas atlas, TileData data);
}
