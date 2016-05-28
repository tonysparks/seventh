/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import leola.vm.types.LeoArray;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import leola.vm.types.LeoString;
import seventh.client.gfx.TextureUtil;
import seventh.map.Map.SceneDef;
import seventh.map.Tile.SurfaceType;

/**
 * @author Tony
 *
 */
public class TiledMapLoader implements MapLoader {

	private static final int FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
	private static final int FLIPPED_VERTICALLY_FLAG   = 0x40000000;
	private static final int FLIPPED_DIAGONALLY_FLAG   = 0x20000000;
	
	/**
	 * Loads an {@link OrthoMap} created from the "Tiled" program.
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map loadMap(LeoMap map, boolean loadAssets) throws Exception {
		SceneDef def = new SceneDef();
		
		int width = map.getInt("width");
		int height = map.getInt("height");
		
		int tileWidth = map.getInt("tilewidth");
		int tileHeight = map.getInt("tileheight");
		
		def.setDimensionX(width);
		def.setDimensionY(height);
		
		def.setTileWidth(tileWidth);
		def.setTileHeight(tileHeight);
		
		TilesetAtlas atlas = null;
		SurfaceType[][] surfaces = new SurfaceType[height][width];
		def.setSurfaces(surfaces);
		
		LeoArray tilesets = map.getByString("tilesets").as();
		atlas = parseTilesets(tilesets, loadAssets);
		
		
		LeoArray layers = map.getByString("layers").as();
		List<Layer> mapLayers = parseLayers(layers, atlas, loadAssets, tileWidth, tileHeight, surfaces);
		
		List<Layer> backgroundLayers = new ArrayList<Layer>();
		List<Layer> foregroundLayers = new ArrayList<Layer>();
		
		for(Layer layer : mapLayers) {
			if(layer.isForeground()) {
				foregroundLayers.add(layer);
			}
			else {
				backgroundLayers.add(layer);
			}
		}
		
		def.setBackgroundLayers(backgroundLayers.toArray(new Layer[backgroundLayers.size()]));
		def.setForegroundLayers(foregroundLayers.toArray(new Layer[foregroundLayers.size()]));
			
		Map theMap = new OrthoMap(loadAssets);
		theMap.init(def);
		
		return theMap;
	}
	
	/**
	 * Parses the {@link SurfaceType}s layer
	 * @param surfaces
	 * @param data
	 * @param width
	 * @param tileWidth
	 * @param tileHeight
	 */
	private void parseSurfaces(SurfaceType[][] surfaces, TilesetAtlas atlas, LeoArray data, int width, int tileWidth, int tileHeight) {
		int y = -1; // account for zero
		for(int x = 0; x < data.size(); x++) {
						
			if(x % width == 0) {									
				y++;
			}
			
			int tileId = data.get(x).asInt();
			int surfaceId = atlas.getTileId(tileId)-1; /* minus one to get back to zero based */
			surfaces[y][x % width] = SurfaceType.fromId(surfaceId);
		}
	}		
	
	private List<Layer> parseLayers(LeoArray layers,TilesetAtlas atlas, boolean loadImages, int tileWidth, int tileHeight, SurfaceType[][] surfaces) throws Exception {
		
		List<Layer> mapLayers = new ArrayList<Layer>(layers.size());
		
		int index = 0;
		for(LeoObject l : layers) {			
			LeoMap layer = l.as();
			
			LeoArray data = layer.getByString("data").as();
			int width = layer.getInt("width");	
			int height = layer.getInt("height");
			
			boolean isCollidable = false;
			boolean isForeground = false;
			boolean isProperty = false;
			boolean isSurfaceTypes = false;
			boolean isDestructable = false;
			boolean isVisible = layer.getBoolean("visible");
			
			int heightMask = 0;
			if ( layer.has(LeoString.valueOf("properties"))) {
				LeoMap properties = layer.getByString("properties").as();			
				isCollidable = properties.getString("collidable").equals("true");
				isForeground = properties.getString("foreground").equals("true");
				isDestructable = properties.getString("destructable").equals("true");
				
				if(properties.containsKeyByString("heightMask")) {
					String strMask = properties.getString("heightMask");
					heightMask = Integer.parseInt(strMask);
				}
				
				if(properties.containsKeyByString("lights")) {
					isProperty = true;
				}
				
				if(properties.containsKeyByString("surfaces")) {
					isSurfaceTypes = true;
				}
								
			}
			
			if(isSurfaceTypes) {
				parseSurfaces(surfaces, atlas, data, width, tileWidth, tileHeight);
				continue;
			}
			
			Layer mapLayer = new Layer(layer.getString("name"),
									   isCollidable, 
					                   isForeground, 
					                   isDestructable, 
					                   isProperty, 
					                   isVisible,
					                   index, 
					                   heightMask,
					                   height);
			mapLayers.add(mapLayer);
						
			Tile[] row = null; //new Tile[width];
			
			int y = -tileHeight; // account for zero
			int rowIndex = 0;
			for(int x = 0; x < data.size(); x++) {
				int tileId = data.get(x).asInt();
				boolean flippedHorizontally = (tileId & FLIPPED_HORIZONTALLY_FLAG) != 0;
				boolean flippedVertically = (tileId & FLIPPED_VERTICALLY_FLAG) != 0; 
				boolean flippedDiagonally = (tileId & FLIPPED_DIAGONALLY_FLAG) != 0;
				
				tileId &= ~(FLIPPED_HORIZONTALLY_FLAG |
                            FLIPPED_VERTICALLY_FLAG |
                            FLIPPED_DIAGONALLY_FLAG);
				
				if(x % width == 0) {						
					row = new Tile[width];
					mapLayer.addRow(rowIndex++, row);
					y+=tileHeight;
				}
				
				if(loadImages) {
					TextureRegion image = atlas.getTile(tileId);
					if(image != null) {
						Tile tile = null;
						if( atlas.isAnimatedTile(tileId) ) {
							tile = new AnimatedTile(atlas.getAnimatedTile(tileId), index, tileWidth, tileHeight); 
							mapLayer.setContainsAnimations(true);
						}
						else {
							tile = new Tile(image, index, tileWidth, tileHeight);
						}
												
						tile.setPosition( (x%width) * tileWidth, y);
//						tile.setSurfaceType(atlas.getTileSurfaceType(tileId));
						tile.setFlips(flippedHorizontally, flippedVertically, flippedDiagonally);
						
						if(isCollidable) {
							int collisionId = atlas.getTileId(tileId);
							tile.setCollisionMaskById(collisionId);
						}
						row[x%width] = tile;
					}
					else {
						row[x%width] = null;
					}
				}
				// if we are headless...
				else {
					if(tileId != 0) {
						Tile tile = new Tile(null, index, tileWidth,tileHeight);
						tile.setPosition( (x%width) * tileWidth, y);
//						tile.setSurfaceType(atlas.getTileSurfaceType(tileId));
						
						if(isCollidable) {
							int collisionId = atlas.getTileId(tileId);
							tile.setCollisionMaskById(collisionId);
						}
						row[x%width] = tile;
					}
					else {
						row[x%width] = null;
					}
				}
			}
			
			
			if(!isForeground) {
				index++;
			}	
			
			mapLayer.applyHeightMask();
		}
		
		return mapLayers;
	}
	
	private TilesetAtlas parseTilesets(LeoArray tilesets, boolean loadImages) throws Exception {
		if(tilesets.isEmpty()) {
			throw new IllegalArgumentException("There must be at least 1 tileset");
		}
		
		TilesetAtlas atlas = new  TilesetAtlas();
		for(LeoObject t : tilesets) {
			LeoMap tileset = t.as();
						
			// skip the sourced tilesets
			if(t.hasObject("source")) {
				// HACK: Updated version of Tiled which no longer supports inlining
				// shared tilests (lame)
				String source = tileset.getString("source");
				if(source.endsWith("collidables.tsx")) {
					tileset.putByString("image", LeoString.valueOf("./assets/gfx/tiles/collision_tileset.png"));
					tileset.putByString("name", LeoString.valueOf("collidables"));
				}
				else if(source.endsWith("city.tsx")) {
					tileset.putByString("image", LeoString.valueOf("./assets/gfx/tiles/cs2dnorm.png"));
					tileset.putByString("name", LeoString.valueOf("city"));
				}
				
				tileset.putByString("tilewidth", LeoObject.valueOf(32));
				tileset.putByString("tileheight", LeoObject.valueOf(32));
			}
			
			int firstgid = tileset.getInt("firstgid");			
			int margin = tileset.getInt("margin");
			int spacing = tileset.getInt("spacing");
			int tilewidth = tileset.getInt("tilewidth");
			int tileheight = tileset.getInt("tileheight");
			
			LeoMap tilesetprops = null;
			LeoObject props = tileset.getByString("tileproperties");
			if(LeoObject.isTrue(props) && props.isMap()) {
				tilesetprops = props.as();
			}

			TextureRegion image = null;
			TextureRegion[] images = null;
												
			if(loadImages) {
				final String imagePath = tileset.getString("image");
				image = TextureUtil.loadImage(imagePath);
				image.flip(false, true);
				images = TextureUtil.toTileSet(image, tilewidth, tileheight, margin, spacing);			
			}
			atlas.addTileset(new Tileset(firstgid, images, tilesetprops));
		}
		
		return atlas;
	}
}
