/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

import java.util.List;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.map.Tile.SurfaceType;
import seventh.math.OOB;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Debugable;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A Scene represents a renderable world in which characters can interact, essentially it's a 
 * game world, the game world is comprised of multiple linked {@link Map}s.
 * 
 * @author Tony
 *
 */
public abstract interface Map extends Renderable, Debugable {
	
	/**
	 * Scene Definition
	 * @author Tony
	 *
	 */
	public static class SceneDef {
		
		/**
		 * Map Layer
		 */
		private Layer[] backgroundLayers;
		
		/**
		 * Foreground layer
		 */
		private Layer[] foregroundLayers;
		
		/**
		 * X Dimension
		 */
		private int dimensionX;
		
		/**
		 * Y Dimension
		 */
		private int dimensionY;
		
		/**
		 * Tile width
		 */
		private int tileWidth;
		
		/**
		 * Tile height
		 */
		private int tileHeight;
		
		/**
		 * Background Image
		 */
		private TextureRegion backgroundImage;
		
		private SurfaceType[][] surfaces;
		
		/**
		 * @return the surfaces
		 */
		public SurfaceType[][] getSurfaces() {
			return surfaces;
		}
		
		/**
		 * @param surfaces the surfaces to set
		 */
		public void setSurfaces(SurfaceType[][] surfaces) {
			this.surfaces = surfaces;
		}
		
		/**
		 * @return the layers
		 */
		public Layer[] getBackgroundLayers() {
			return backgroundLayers;
		}

		/**
		 * @param layers the layers to set
		 */
		public void setBackgroundLayers(Layer[] layers) {
			this.backgroundLayers = layers;
		}

		/**
		 * @return the foregroundLayers
		 */
		public Layer[] getForegroundLayers() {
			return foregroundLayers;
		}

		/**
		 * @param foregroundLayers the foregroundLayers to set
		 */
		public void setForegroundLayers(Layer[] foregroundLayers) {
			this.foregroundLayers = foregroundLayers;
		}

		/**
		 * @return the dimensionX
		 */
		public int getDimensionX() {
			return dimensionX;
		}

		/**
		 * @param dimensionX the dimensionX to set
		 */
		public void setDimensionX(int dimensionX) {
			this.dimensionX = dimensionX;
		}

		/**
		 * @return the dimensionY
		 */
		public int getDimensionY() {
			return dimensionY;
		}

		/**
		 * @param dimensionY the dimensionY to set
		 */
		public void setDimensionY(int dimensionY) {
			this.dimensionY = dimensionY;
		}

		/**
		 * @return the tileWidth
		 */
		public int getTileWidth() {
			return tileWidth;
		}

		/**
		 * @param tileWidth the tileWidth to set
		 */
		public void setTileWidth(int tileWidth) {
			this.tileWidth = tileWidth;
		}

		/**
		 * @return the tileHeight
		 */
		public int getTileHeight() {
			return tileHeight;
		}

		/**
		 * @param tileHeight the tileHeight to set
		 */
		public void setTileHeight(int tileHeight) {
			this.tileHeight = tileHeight;
		}


		/**
		 * @return the backgroundImage
		 */
		public TextureRegion getBackgroundImage() {
			return backgroundImage;
		}

		/**
		 * @param backgroundImage the backgroundImage to set
		 */
		public void setBackgroundImage(TextureRegion backgroundImage) {
			this.backgroundImage = backgroundImage;
		}
		
		
	}
	
	/**
	 * Initialize the Scene
	 * 
	 * @param info
	 * @throws MyriadException
	 */
	public abstract void init(SceneDef info) throws Exception;
	
	/**
	 * Free resources
	 */
	public abstract void destroy();
	
	/**
	 * @return the backgroundLayers
	 */
	public Layer[] getBackgroundLayers();
	
	/**
	 * @return the collidableLayers
	 */
	public Layer[] getCollidableLayers();
	
	/**
	 * @return the foregroundLayers
	 */
	public Layer[] getForegroundLayers();
	
	/**
	 * Render this object.
	 * 
	 * @param renderer
	 * @param camera
	 * @param alpha
	 */
	public void renderForeground(Canvas canvas, Camera camera, long alpha);

	/**
	 * Renders a solid layer over the set of viewable tiles (Fog of War)
	 */
	public abstract void renderSolid(Canvas canvas, Camera camera, long alpha);
	
	/**
	 * Retrieve a Tile.
	 * 
	 * @param layer
	 * @param x - x array coordinate
	 * @param y - y array coordinate
	 * @return
	 */
	public abstract Tile getTile( int layer, int x, int y );
	
	/**
	 * Retrieve a Tile.
	 * 
	 * @param x - x array coordinate
	 * @param y - y array coordinate
	 * @return
	 */
	public abstract Tile getCollidableTile(int x, int y );
	
	
	
	/**
	 * Get a {@link Tile} from world coordinates
	 * @param layer
	 * @param x - x in world coordinate space
	 * @param y - y in world coordinate space
	 * @return
	 */
	public abstract Tile getWorldTile( int layer, int x, int y);
	
	
	/**
	 * Retrieves the {@link SurfaceType} given the supplied x and y index
	 * @param x
	 * @param y
	 * @return the {@link SurfaceType}
	 */
	public abstract SurfaceType getSurfaceTypeByIndex(int x, int y);
	
	/**
	 * Retrieves the {@link SurfaceType} given the supplied world coordinates
	 * @param x
	 * @param y
	 * @return the {@link SurfaceType}
	 */
	public abstract SurfaceType getSurfaceTypeByWorld(int x, int y);

	
	/**
	 * Get a {@link Tile} from world coordinates
	 * 
	 * @param x - x in world coordinate space
	 * @param y - y in world coordinate space
	 * @return
	 */
	public abstract Tile getWorldCollidableTile(int x, int y);
	
	
	/**
	 * Determines if there is a collidable tile at the location.
	 * 
	 * @param x
	 * @param y
	 * @return true if there is a collidable tile at this location
	 */
	public abstract boolean hasWorldCollidableTile(int x, int y);
	
	/**
	 * Queries to see if there is a heightMask from world coordinates
	 * 
	 * @param x - x in world coordinate space
	 * @param y - y in world coordinate space
	 * @return true if there is a height mask; false otherwise
	 */
	public abstract boolean hasHeightMask( int worldX, int worldY);
	
	/**
	 * Check for a collision given a {@link Rectangle}
	 * 
	 * @param rect
	 * @return true if a collision occurs, false otherwise
	 */
	public abstract boolean rectCollides( Rectangle rect );
	
	/**
	 * Check for a collision given a {@link OOB}
	 * 
	 * @param oob
	 * @return true if a collision occurs, false otherwise
	 */
	public abstract boolean rectCollides( OOB oob );
	
	/**
	 * Check for a collision given a {@link Rectangle}
	 * 
	 * @param rect
	 * @param heightMask
	 * @return true if a collision occurs, false otherwise
	 */
	public abstract boolean rectCollides( Rectangle rect, int heightMask );
	
	/**
	 * Check for a collision given a point
	 * 
	 * @param x
	 * @param y
	 * @return true if a collision occurs, false otherwise
	 */
	public abstract boolean pointCollides(int x, int y);
	
	/**
	 * Check for a collision given a point
	 * 
	 * @param x
	 * @param y
	 * @param heightMask
	 * @return true if a collision occurs, false otherwise
	 */
	public abstract boolean pointCollides(int x, int y, int heightMask);
	
	/**
	 * Check for a collision given the line
	 * @param a - start of the line
	 * @param b - end of the line
	 * @return true if a collision occurs, false otherwise
	 */
	public abstract boolean lineCollides(Vector2f a, Vector2f b);
	
	
	/**
	 * Check for a collision given the line
	 * @param a - start of the line
	 * @param b - end of the line
	 * @param heightMask
	 * @return true if a collision occurs, false otherwise
	 */
	public abstract boolean lineCollides(Vector2f a, Vector2f b, int heightMask);
	
	/**
	 * Check the {@link Map} boundaries
	 * @param worldX
	 * @param worldY
	 * @return true if out of bounds
	 */
	public abstract boolean checkBounds( int worldX, int worldY );
	
	/**
	 * Checks the map boundaries based on tile coordinates
	 * @param x
	 * @param y
	 * @return true if out of bounds
	 */
	public boolean checkTileBounds(int x, int y);
	
	/**
	 * Get the {@link Map}s width
	 * @return
	 */
	public abstract int getMapWidth();
	
	/**
	 * Get the {@link Map}s height
	 * @return
	 */
	public abstract int getMapHeight();
	
	/**
	 * @return the tile width
	 */
	public abstract int getTileWidth();
	
	/**
	 * @return the tile height
	 */
	public abstract int getTileHeight(); 
	
	public int getTileWorldWidth();
	public int getTileWorldHeight();
	
	/**
	 * Creates a new {@link MapGraph}
	 * @param factory
	 * @return the new {@link MapGraph}
	 */
	public <E> MapGraph<E> createMapGraph(GraphNodeFactory<E> factory);
	
	/**
	 * Convert world coordinates to tile coordinates
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public abstract Vector2f worldToTile( int x, int y);
	public abstract int worldToTileX(int x);
	public abstract int worldToTileY(int y);
	
	/**
	 * Convert tile coordinates to world coordinates
	 * 
	 * @param tx
	 * @param ty
	 * @return
	 */
	public abstract Vector2f tileToWorld( int tx, int ty );
	
	
	/**
	 * @param layer
	 * @param centerX
	 * @param centerY
	 * @param radius
	 * @param tiles -- same tileset returned
	 * @return a list of tiles within the circle
	 */
	public abstract List<Tile> getTilesInCircle(int layer, int centerX, int centerY, int radius, List<Tile> tiles);
	
	/**
	 * @param centerX
	 * @param centerY
	 * @param radius
	 * @param tiles -- same tileset returned
	 * @return a list of tiles within the circle
	 */
	public abstract List<Tile> getTilesInCircle(int centerX, int centerY, int radius, List<Tile> tiles);


	/**
	 * Get the tiles inside the {@link Rectangle}
	 * 
	 * @param bounds
	 * @param tiles
	 * @return  a list of tiles within the {@link Rectangle}
	 */
	public abstract List<Tile> getTilesInRect(Rectangle bounds, List<Tile> tiles);
	
	/**
	 * Get the tiles inside the {@link Rectangle}
	 * 
	 * @param layer
	 * @param bounds
	 * @param tiles
	 * @return  a list of tiles within the {@link Rectangle}
	 */
	public abstract List<Tile> getTilesInRect(int layer, Rectangle bounds, List<Tile> tiles);
	
	/**
	 * Gets the Collision tiles that match up against the supplied 'checkAgainst' tiles
	 * @param checkAgainst the tiles to match up against
	 * @param results the result set to return (output parameter)
	 * @return the same results object passed
	 */
	public List<Tile> getCollisionTilesAt(List<Tile> checkAgainst, List<Tile> results);
	
	/**
	 * Sets the mask on the tiles
	 * @param tiles
	 * @param mask
	 */
	public abstract void setMask(List<Tile> tiles, int mask);
	
	/**
	 * Removes a destructable tile at the supplied tile index.
	 * 
	 * @param tileX
	 * @param tileY
	 */
	public abstract boolean removeDestructableTileAt(int tileX, int tileY);
	
	/**
	 * Removes a destructable tile at the supplied world coordinates
	 * 
	 * @param worldX
	 * @param worldY
	 */
	public abstract boolean removeDestructableTileAtWorld(int worldX, int worldY);
	
	
	/**
	 * Returns a list of tile indexes that have been removed.
	 * @return Returns a list of tile indexes that have been removed.
	 */
	public abstract List<Tile> getRemovedTiles();
	public abstract boolean removeDestructableTilesAt(int[] tilePositions);
}

