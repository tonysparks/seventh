/*
 * see license.txt 
 */
package seventh.game;

import seventh.game.entities.LightBulb;
import seventh.map.Layer;
import seventh.map.Layer.LayerTileIterator;
import seventh.map.Map;
import seventh.map.Tile;

/**
 * Initializes any map interactions with the game world
 * 
 * @author Tony
 *
 */
public class MapInteraction {

    /**
     * Create the map interactions
     * 
     * @param game
     */
    public static void create(final Game game) {
        final Map map = game.getMap();
        /* Load any layers that have predefined entities
         * on them, as or right now this only includes
         * lights
         */
        Layer[] layers = map.getBackgroundLayers();
        for(int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            if(layer != null && layer.isLightLayer()) {                        
                layer.foreach(new LayerTileIterator() {                               
                    @Override
                    public void onTile(Tile tile, int x, int y) {
                        if(tile != null) {
                            LightBulb light = game.newLight(map.tileToWorld(x, y));
                            light.setColor(0.9f, 0.85f, 0.85f);
                            light.setLuminacity(0.95f);
                        }
                    }
                });                                                    
            }
        }
    }

}
