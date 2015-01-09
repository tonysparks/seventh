/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.ShadeTiles;
import seventh.graph.Edge;
import seventh.graph.GraphNode;
import seventh.map.Tile.SurfaceType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The {@link OrthoMap} can be used for Top-down or Side-scrollers
 * 
 * @author Tony
 * 
 */
public class OrthoMap implements Map {

	/**
	 * Scene Width
	 */
	private int mapWidth;

	/**
	 * Scene Width
	 */
	private int mapHeight;

	/**
	 * Tile width 
	 */
	private int tileWidth;

	/**
	 * Tile width
	 */
	private int tileHeight;

	private int maxX;
	private int maxY;

	/**
	 * Current map location
	 */
	private Vector2f mapOffset;

	private Rectangle worldBounds;
	
	/**
	 * Layers
	 */
	private Layer[] backgroundLayers, foregroundLayers, collidableLayers;

	/**
	 * The current frames viewport
	 */
	private Rectangle currentFrameViewport;
	
	
	/**
	 * Background image
	 */
	private TextureRegion backgroundImage;
	
	/**
	 * The surfaces
	 */
	private SurfaceType[][] surfaces;
	
	private java.util.Map<Integer, TextureRegion> shadeTilesLookup;
	
	/**
	 * Constructs a new {@link OrthoMap}.
	 */
	public OrthoMap(boolean loadAssets) {
		this.currentFrameViewport = new Rectangle();
		if(loadAssets) {
			this.shadeTilesLookup = new HashMap<Integer, TextureRegion>();
		}
		destroy();
	}

	/**
	 * @return the backgroundLayers
	 */
	public Layer[] getBackgroundLayers() {
		return backgroundLayers;
	}
	
	/**
	 * @return the collidableLayers
	 */
	public Layer[] getCollidableLayers() {
		return collidableLayers;
	}
	
	/**
	 * @return the foregroundLayers
	 */
	public Layer[] getForegroundLayers() {
		return foregroundLayers;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.map.Map#getCollisionTilesAt(java.util.List, java.util.List)
	 */
	public List<Tile> getCollisionTilesAt(List<Tile> checkAgainst, List<Tile> results) {
		for(int i = 0; i < checkAgainst.size(); i++) {
			Tile checkMe = checkAgainst.get(i);
			int xIndex = checkMe.getXIndex();
			int yIndex = checkMe.getYIndex();
			
			for (int j = 0; j < this.collidableLayers.length; j++) {		
				Tile tile = getTile(this.collidableLayers[j].getIndex(), xIndex, yIndex); 
				if ( tile != null ) {
					results.add(tile);
					break;
				}
			}
		}
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * @see leola.live.game.Map#checkBounds(int, int)
	 */
	public boolean checkBounds(int x, int y) {
//		return (x <= 0 || y <= 0 || x >= this.mapWidth - this.tileWidth || y >= this.mapHeight - this.tileHeight);
		return (x < 0 || y < 0 || x >= this.mapWidth || y >= this.mapHeight);
	}

	/*
	 * (non-Javadoc)
	 * @see leola.live.game.Map#checkTileBounds(int, int)
	 */
	public boolean checkTileBounds(int x, int y) {
		return (x < 0 || y < 0 || x >= this.maxX || y >= this.maxY);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.game.Map#getTilesInCircle(int, int, float, java.util.List)
	 */
	@Override
	public List<Tile> getTilesInCircle(int layer, int centerX, int centerY, int radius,
			List<Tile> tiles) {
		List<Tile> result = (tiles == null) ? new ArrayList<Tile>() : tiles;
		result.clear();
		
		int length = (radius * 2) + 1;
		
		for(int y = centerY - (length /2); 
			y <= (centerY + (length/2)); 
			y+=tileHeight) {
			
			for(int x = centerX - (length/2);
				x <= (centerX + (length/2));
				x+=tileWidth ) {
				
				if(!checkBounds(x, y)) {
					Tile tile = getWorldTile(layer, x, y); 
					if(tile!=null) {
						result.add(tile);
					}
				}
			}
		}
				
		return result;
	}
		
	
	/* (non-Javadoc)
	 * @see leola.live.game.Map#getTilesInCircle(int, int, float, java.util.List)
	 */
	@Override
	public List<Tile> getTilesInCircle(int centerX, int centerY, int radius,
			List<Tile> tiles) {
		return getTilesInCircle(0, centerX, centerY, radius, tiles);
	}
		 
	
	@Override
	public boolean pointCollides(int x, int y) {
		return pointCollides(x, y, 1);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.game.Map#pointCollides(int, int)
	 */
	@Override
	public boolean pointCollides(int x, int y, int heightMask) {
		if ( checkBounds(x, y) ) {
			return true;
		}

		int tileOffset_x = 0;//(x % this.tileWidth);
		int wx = (tileOffset_x + x) / this.tileWidth;

		int tileOffset_y = 0;//(y % this.tileHeight);
		int wy = (tileOffset_y + y) / this.tileHeight;
		
		for (int i = 0; i < this.collidableLayers.length; i++) {		
			Tile tile = getTile(this.collidableLayers[i].getIndex(), wx, wy); 
			if ( tile != null ) {
				int tileHeightMask = tile.getHeightMask();
				if(tileHeightMask>0) {
					if ((tileHeightMask & heightMask) == tileHeightMask && (tile.pointCollide(x, y))) {
						return true;
					}
				}
				else if( tile.pointCollide(x, y) ) {
					return true;
				}
			}
			
		}
		return false;
	}
	
	@Override
	public boolean rectCollides(Rectangle rect) {
		return rectCollides(rect, 1);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.game.Map#rectCollides(leola.live.math.Rectangle)
	 */
	@Override
	public boolean rectCollides(Rectangle rect, int heightMask) {
		
		if(!worldBounds.contains(rect)) {
			return true;
		}
		
		// screen pixel x,y coordinate to draw the current tile to
		int pixelX = 0;
		int pixelY = 0;

		int indexX = 0;
		int indexY = 0;
		
		int toIndex_x=0, toIndex_y=0;
		
	    // Current Tile offset (to pixels)
		int tileOffset_x = -( rect.x % this.tileWidth );
	    toIndex_x    = ( tileOffset_x + rect.x) / this.tileWidth;

	    // current tile y offset (to pixels)
	    int tileOffset_y = -(rect.y % this.tileHeight);
	    toIndex_y    = (tileOffset_y + rect.y) / this.tileHeight;
	    
	    
		indexY = toIndex_y;
		for (pixelY = tileOffset_y;
		     pixelY < rect.height && indexY < this.maxY; 		     
		     pixelY += this.tileHeight, indexY++) {
						
			for (pixelX = tileOffset_x, indexX = toIndex_x; 
			     pixelX < rect.width && indexX < this.maxX; 
			     pixelX += this.tileWidth, indexX++) {
				
				
				if ( (indexY >= 0 && indexX >= 0) && (indexY < this.maxY && indexX < this.maxX) ) {
					for (int i = 0; i < collidableLayers.length; i++) {
						Layer layer = collidableLayers[i];
							
						Tile tile = layer.getRow(indexY).get(indexX);
						if ( tile != null ) {
							int tileHeightMask = tile.getHeightMask();
							if(tileHeightMask>0) {							
								if ( (tileHeightMask & heightMask) == tileHeightMask && (tile.rectCollide(rect)) ) {
									return true;
								}
							} 
							else if( tile.rectCollide(rect) ) {
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean lineCollides(Vector2f a, Vector2f b) {
		return lineCollides(a, b, 1);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.game.Map#lineCollides(leola.live.math.Vector2f, leola.live.math.Vector2f)
	 */
	@Override
	public boolean lineCollides(Vector2f a, Vector2f b, int heightMask) {
		
		// Uses the Bresenham Line Algorithm
		int x1 = (int)b.x;
		int y1 = (int)b.y;
		
		int x0 = (int)a.x;
		int y0 = (int)a.y;
		
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		
		int sx = 0; 
		int sy = 0;
		
		if (x0 < x1) sx = 1; else sx = -1;
		if (y0 < y1) sy = 1; else sy = -1;
		
		int err = dx-dy;
		
								
		do {		
			if(this.pointCollides(x0,y0, heightMask)) {
				return true;
			}
			
			if(x0 == x1 && y0 == y1) {
				break;
			}
			
			int e2 = err * 2;
			
			if(e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}
			
			if(x0 == x1 && y0 == y1) {
				if(this.pointCollides(x0,y0, heightMask)) {
					return true;
				}
				break;
			}
			
			if(e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
			
			if( checkBounds(x0, y0) ) {					
				return true;
			}
			
		} while( true );
		
		
		return false;
		
	}
	
	/* (non-Javadoc)
	 * @see leola.live.game.Map#setMask(java.util.List, int)
	 */
	@Override
	public void setMask(List<Tile> tiles, int mask) {
		if(tiles != null ) {
			int s = tiles.size();
			for(int i = 0; i < s; i++) {
				tiles.get(i).setMask(mask);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.myriad.render.scene.Scene#freeScene()
	 */
	
	public void destroy() {
		if ( this.backgroundLayers != null ) {
			for (int i = 0; i < this.backgroundLayers.length; i++) {
				Layer layer = this.backgroundLayers[i];
				if ( layer == null ) {
					continue;
				}
				
				for( int j = 0; j < this.backgroundLayers[i].numberOfRows(); j++ ) {
					this.backgroundLayers[i].getRow(j).clear();
				}
				this.backgroundLayers[i] = null;
			}
			
		}
		this.backgroundLayers = null;
		
		
		if ( this.foregroundLayers != null ) {
			for (int i = 0; i < this.foregroundLayers.length; i++) {
				Layer layer = this.foregroundLayers[i];
				if ( layer == null ) {
					continue;
				}
				
				for( int j = 0; j < this.foregroundLayers[i].numberOfRows(); j++ ) {
					this.foregroundLayers[i].getRow(j).clear();
				}
				
				this.foregroundLayers[i] = null;
			}
		}		
		this.foregroundLayers = null;
		
		this.collidableLayers=null;
		
		this.surfaces = null;
		
		this.mapOffset = null;
		this.backgroundImage = null;
		
		this.mapHeight = 0;
		this.mapWidth = 0;
		
		this.maxX = 0;
		this.maxY = 0;
		
		this.tileHeight = 0;
		this.tileWidth = 0;						
	}

	/* (non-Javadoc)
	 * @see seventh.map.Map#getTileWorldHeight()
	 */
	@Override
	public int getTileWorldHeight() {
		return maxY;
	}
	
	/* (non-Javadoc)
	 * @see seventh.map.Map#getTileWorldWidth()
	 */
	@Override
	public int getTileWorldWidth() {	
		return maxX;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.myriad.render.scene.Scene#getMapHeight()
	 */
	
	public int getMapHeight() {
		return this.mapHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.myriad.render.scene.Scene#getMapWidth()
	 */
	
	public int getMapWidth() {
		return this.mapWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.myriad.render.scene.Scene#getScreenTile(int, int, int)
	 */
	
	public Tile getWorldTile(int layer, int x, int y) {
		if(checkBounds(x, y)) {
			return null;
		}
		
//		Vector2f w = worldToTile(x, y);
		int tileOffset_x = 0;//(x % this.tileWidth);
		int wx = (tileOffset_x + x) / this.tileWidth;

		int tileOffset_y = 0;//(y % this.tileHeight);
		int wy = (tileOffset_y + y) / this.tileHeight;

		return getTile(layer, wx, wy);
	}
	
	/* (non-Javadoc)
	 * @see seventh.map.Map#getWorldCollidableTile(int, int)
	 */
	@Override
	public Tile getWorldCollidableTile(int x, int y) {
		if(checkBounds(x, y)) {
			return null;
		}
		
//		Vector2f w = worldToTile(x, y);
		int tileOffset_x = 0;//(x % this.tileWidth);
		int wx = (tileOffset_x + x) / this.tileWidth;

		int tileOffset_y = 0;//(y % this.tileHeight);
		int wy = (tileOffset_y + y) / this.tileHeight;
	
		return getCollidableTile(wx, wy);
	}
	
	
	
	/* (non-Javadoc)
	 * @see leola.live.game.Map#hasHeightMask(int, int)
	 */
	@Override
	public boolean hasHeightMask(int worldX, int worldY) {
//		Vector2f w = worldToTile(x, y);
		int tileOffset_x = 0;//(x % this.tileWidth);
		int wx = (tileOffset_x + worldX) / this.tileWidth;

		int tileOffset_y = 0;//(y % this.tileHeight);
		int wy = (tileOffset_y + worldY) / this.tileHeight;
		
		int numberOfLayers = this.collidableLayers.length;
		for(int i = 0; i < numberOfLayers; i++) {

			if(checkBounds(worldX, worldY)) {
				continue;
			}
						
			Tile tile = this.collidableLayers[i].getRow(wy).get(wx);
			if(tile != null && tile.getHeightMask() > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.myriad.render.scene.Scene#getTile(int, int, int)
	 */
	
	public Tile getTile(int layer, int x, int y) {
		return this.backgroundLayers[layer].getRow(y).get(x);
	}
	
	/* (non-Javadoc)
	 * @see seventh.map.Map#getCollidableTile(int, int)
	 */
	@Override
	public Tile getCollidableTile(int x, int y) {
		for(int i = 0; i < collidableLayers.length; i++) {
			Tile tile = collidableLayers[i].getRow(y).get(x);
			if(tile != null) {
				return tile;
			}
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.myriad.render.scene.Scene#init(org.myriad.render.scene.Scene.SceneDef
	 * )
	 */
	
	public void init(SceneDef info) throws Exception {
		destroy();

		List<Layer> collidableLayers = new ArrayList<Layer>();
		
		int bgSize = info.getBackgroundLayers().length;
		this.backgroundLayers = new Layer[bgSize];	
		for(int i = 0; i < bgSize; i++ ) {
			this.backgroundLayers[i] = info.getBackgroundLayers()[i];
			if(this.backgroundLayers[i].collidable()) {
				collidableLayers.add(this.backgroundLayers[i]);
			}
		}
		
		int fgSize = info.getForegroundLayers().length;
		this.foregroundLayers = new Layer[fgSize];	
		for(int i = 0; i < fgSize; i++ ) {
			this.foregroundLayers[i] = info.getForegroundLayers()[i];
//			if(this.foregroundLayers[i].collidable()) {
//				collidableLayers.add(this.foregroundLayers[i]);
//			}
		}
		
		this.collidableLayers = new Layer[collidableLayers.size()];
		this.collidableLayers = collidableLayers.toArray(this.collidableLayers);

		this.backgroundImage = info.getBackgroundImage();
		
		this.maxX = info.getDimensionX();
		this.maxY = info.getDimensionY();

		this.tileWidth = info.getTileWidth();
		this.tileHeight = info.getTileHeight();

		Vector2f worldCoordinates = tileToWorld(this.maxX, this.maxY);
		this.mapWidth = (int) worldCoordinates.x;
		this.mapHeight = (int) worldCoordinates.y;	
		
		this.worldBounds = new Rectangle(0, 0, this.mapWidth, this.mapHeight);
//		
//		List<Tile> tiles = getTilesInCircle(200, 400, 250, null);
//		for(Tile t : tiles) {
//			t.setMask(1);
//		}
		
		this.surfaces = info.getSurfaces();
		
		if(this.shadeTilesLookup != null) {
			this.shadeTilesLookup = createShadeLookup(45);
		}
	}
	
	/**
	 * Creates the shade lookup table
	 * @param startAlpha
	 * @return the shade lookup table
	 */
	private java.util.Map<Integer, TextureRegion> createShadeLookup(int startAlpha) {
		java.util.Map<Integer, TextureRegion> shadeTilesLookup = new HashMap<>();
		
		TextureRegion[] shadeTiles = new ShadeTiles(startAlpha, tileWidth, tileHeight).createShadeTiles();
		shadeTilesLookup.put(Tile.TILE_NORTH_INVISIBLE, shadeTiles[0]);
		shadeTilesLookup.put(Tile.TILE_EAST_INVISIBLE, shadeTiles[1]);
		shadeTilesLookup.put(Tile.TILE_SOUTH_INVISIBLE, shadeTiles[2]);
		shadeTilesLookup.put(Tile.TILE_WEST_INVISIBLE, shadeTiles[3]);
		
		shadeTilesLookup.put(Tile.TILE_NORTH_INVISIBLE|Tile.TILE_EAST_INVISIBLE, shadeTiles[4]);
		shadeTilesLookup.put(Tile.TILE_NORTH_INVISIBLE|Tile.TILE_WEST_INVISIBLE, shadeTiles[5]);
		shadeTilesLookup.put(Tile.TILE_SOUTH_INVISIBLE|Tile.TILE_EAST_INVISIBLE, shadeTiles[6]);		
		shadeTilesLookup.put(Tile.TILE_SOUTH_INVISIBLE|Tile.TILE_WEST_INVISIBLE, shadeTiles[7]);
		
		shadeTilesLookup.put(Tile.TILE_NORTH_INVISIBLE|Tile.TILE_WEST_INVISIBLE|Tile.TILE_EAST_INVISIBLE, shadeTiles[8]);		
		shadeTilesLookup.put(Tile.TILE_SOUTH_INVISIBLE|Tile.TILE_WEST_INVISIBLE|Tile.TILE_EAST_INVISIBLE, shadeTiles[9]);		
		shadeTilesLookup.put(Tile.TILE_NORTH_INVISIBLE|Tile.TILE_SOUTH_INVISIBLE|Tile.TILE_WEST_INVISIBLE, shadeTiles[10]);		
		shadeTilesLookup.put(Tile.TILE_NORTH_INVISIBLE|Tile.TILE_SOUTH_INVISIBLE|Tile.TILE_EAST_INVISIBLE, shadeTiles[11]);
		
		shadeTilesLookup.put(Tile.TILE_SOUTH_INVISIBLE|Tile.TILE_NORTH_INVISIBLE, shadeTiles[12]);
		shadeTilesLookup.put(Tile.TILE_EAST_INVISIBLE|Tile.TILE_WEST_INVISIBLE, shadeTiles[13]);
		
		shadeTilesLookup.put(Tile.TILE_NORTH_INVISIBLE|Tile.TILE_EAST_INVISIBLE|
				Tile.TILE_SOUTH_INVISIBLE|Tile.TILE_WEST_INVISIBLE, shadeTiles[14]);
		return shadeTilesLookup;
	}

	/* (non-Javadoc)
	 * @see leola.live.game.Map#createMapGraph(leola.live.game.GraphNodeFactory)
	 */
	@Override
	@SuppressWarnings("all")
	public <E> MapGraph<E> createMapGraph(GraphNodeFactory<E> factory) {		
		int numberOfRows = backgroundLayers[0].numberOfRows();
		int numberOfColumns = backgroundLayers[0].getRow(0).size();
		
		GraphNode[][] nodes = new GraphNode[numberOfRows][numberOfColumns];
		for(int i = 0; i < numberOfRows; i++) {
			nodes[i] = new GraphNode[numberOfColumns];
		}
		
		// first build all graph nodes.
		for(int y = 0; y < numberOfRows; y++) {
			for(int x = 0; x < numberOfColumns; x++ ) {
				boolean isCollidable = false;
				for(int i = 0; i < collidableLayers.length; i++) {
					if ( collidableLayers[i] != null ) {
						Tile tile = collidableLayers[i].getRow(y).get(x);
						isCollidable = tile != null;
						if(isCollidable) {
							break;
						}
					}
				}
				
				if(!isCollidable) {					
					GraphNode<Tile, E> node = new GraphNode<Tile, E>(this.getTile(0, x, y));
					nodes[y][x] = node;
				}
			}
		}
		
		
		// now let's build the edge nodes
		for(int y = 0; y < numberOfRows; y++) {
			for(int x = 0; x < numberOfColumns; x++ ) {	
				GraphNode<Tile, E> node = nodes[y][x];
				if(node==null) continue;
				
				GraphNode<Tile, E> nw = null;
				if(y>0 && x>0) nw = nodes[y - 1][x - 1];
				
				GraphNode<Tile, E> n = null;
				if(y>0) n = nodes[y - 1][x];
				
				GraphNode<Tile, E> ne = null;
				if(y>0 && x<numberOfColumns-1) ne = nodes[y - 1][x + 1];
				
				GraphNode<Tile, E> e = null;
				if(x<numberOfColumns-1) e = nodes[y][x + 1];
				
				GraphNode<Tile, E> se = null;
				if(y<numberOfRows-1 && x<numberOfColumns-1) se = nodes[y + 1][x + 1];
				
				GraphNode<Tile, E> s = null;
				if(y<numberOfRows-1) s = nodes[y + 1][x];
				
				GraphNode<Tile, E> sw = null;
				if(y<numberOfRows-1 && x>0) sw = nodes[y + 1][x - 1];
				
				GraphNode<Tile, E> w = null;
				if(x>0) w = nodes[y][x - 1];
				
				if (nw != null && (n!=null||w!=null) ) {
					node.addEdge(new Edge<Tile, E>(node, nw, factory.createEdgeData(this, node, nw)));
				}
				if (n != null) {
					node.addEdge(new Edge<Tile, E>(node, n, factory.createEdgeData(this, node, n)));
				}
				if (ne != null && (n!=null||e!=null)) {
					node.addEdge(new Edge<Tile, E>(node, ne, factory.createEdgeData(this, node, ne)));
				}
				if (e != null) {
					node.addEdge(new Edge<Tile, E>(node, e, factory.createEdgeData(this, node, e)));
				}
				if (se != null && (s!=null||e!=null)) {
					node.addEdge(new Edge<Tile, E>(node, se, factory.createEdgeData(this, node, se)));
				}
				if (s != null) {
					node.addEdge(new Edge<Tile, E>(node, s, factory.createEdgeData(this, node, s)));
				}
				if (sw != null && (s!=null||w!=null)) {
					node.addEdge(new Edge<Tile, E>(node, sw, factory.createEdgeData(this, node, sw)));
				}
				if (w != null) {
					node.addEdge(new Edge<Tile, E>(node, w, factory.createEdgeData(this, node, w)));
				}
			}
		}			
		return new MapGraph<E>(this,nodes);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.game.Map#getTileHeight()
	 */
	public int getTileHeight() {	
		return this.tileHeight;
	}
	
	/* (non-Javadoc)
	 * @see leola.live.game.Map#getTileWidth()
	 */
	public int getTileWidth() {	
		return this.tileWidth;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.myriad.render.scene.Scene#tileToWorld(int, int)
	 */
	
	public Vector2f tileToWorld(int tx, int ty) {
		return new Vector2f(tx * this.tileWidth, ty * this.tileHeight);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.myriad.render.scene.Scene#worldToTile(int, int)
	 */
	
	public Vector2f worldToTile(int x, int y) {
		Vector2f w = new Vector2f();
  
		int tileOffset_x = 0;//(x % this.tileWidth);
		w.x = (tileOffset_x + x) / this.tileWidth;

		int tileOffset_y = 0;//(y % this.tileHeight);
		w.y = (tileOffset_y + y) / this.tileHeight;

		return (w);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.myriad.render.Renderable#render(org.myriad.render.Renderer,
	 * org.myriad.render.Camera)
	 */
	
	public void render(Canvas canvas, Camera camera, long alpha) {
					
		Vector2f camPos = /*PhysicsUtil.interpolatedPosition(camera.getPhysicsObject(), alpha);*/camera.getPosition();
		Rectangle viewport = camera.getViewPort();
	
		// remember the current frames viewport
		this.currentFrameViewport.setBounds(viewport);
		this.mapOffset = camPos;
		
				
		// the viewport x, and y location
		int vx = viewport.getX();
		int vy = viewport.getY(); 
		
		// screen pixel x,y coordinate to draw the current tile to
		int pixelX = 0;
		int pixelY = 0;

		int indexX = 0;
		int indexY = 0;
		
		int toIndex_x=0, toIndex_y=0;
		
	    // Current Tile offset (to pixels)
		int tileOffset_x =  -( (int)camPos.x % this.tileWidth );
	    toIndex_x    = ( tileOffset_x + (int)camPos.x) / this.tileWidth;

	    // current tile y offset (to pixels)
	    int tileOffset_y =  -( (int)camPos.y % this.tileHeight);
	    toIndex_y    = (tileOffset_y + (int)camPos.y) / this.tileHeight;
	    
	    // render the background
	    renderBackground(canvas, camera);
	    
		indexY = toIndex_y;
		for (pixelY = tileOffset_y;
		     pixelY < viewport.getHeight() && indexY < this.maxY; 		     
		     pixelY += this.tileHeight, indexY++) {
			
			for (pixelX = tileOffset_x, indexX = toIndex_x; 
			     pixelX < viewport.getWidth() && indexX < this.maxX; 
			     pixelX += this.tileWidth, indexX++) {
				
				if ( (indexY >= 0 && indexX >= 0) && (indexY < this.maxY && indexX < this.maxX) ) {
					//for(int i = this.backgroundLayers.length - 1; i >= 0; i--) 
					for(int i = 0; i < this.backgroundLayers.length; i++)
					{
						Layer layer = this.backgroundLayers[i];
						
						if ( layer == null ) {
							continue;
						}
						
						if ( layer.isPropertyLayer()) {
							continue;
						}
							
						Tile tile = layer.getRow(indexY).get(indexX);
						if ( tile != null ) {												
							tile.setRenderingPosition(pixelX + vx, pixelY + vy);
							tile.render(canvas, camera, alpha);
							
							//break;
							// helpful debug stuff
//							canvas.setFont("Arial", 8);
							//String text = "(" + indexX + "," + indexY +")";
							//canvas.drawString(text, pixelX + vx + 16 - (canvas.getWidth(text)/2) , pixelY + vy + 16, 0xff00ff00);
							//canvas.drawRect(pixelX + vx, pixelY + vy, tile.getWidth(), tile.getHeight(), 0xff00ff00);
//							if(layer.collidable()) {
//								canvas.drawRect(pixelX + vx, pixelY + vy, tile.getWidth(), tile.getHeight(), 0xff00ffff);
//							}
						}
					}
				}
			}
		}

	}
	
	/* (non-Javadoc)
	 * @see org.myriad.render.scene.Scene#renderForeground(org.myriad.render.Renderer, org.myriad.render.Camera, org.myriad.core.TimeUnit)
	 */
	
	public void renderForeground(Canvas canvas, Camera camera, long alpha) {
		Vector2f camPos = /*PhysicsUtil.interpolatedPosition(camera.getPhysicsObject(), alpha);*/camera.getPosition();
		Rectangle viewport = camera.getViewPort();
				
		// the viewport x, and y location
		int vx = viewport.getX();
		int vy = viewport.getY(); 
		
		// screen pixel x,y coordinate to draw the current tile to
		int pixelX = 0;
		int pixelY = 0;

		int indexX = 0;
		int indexY = 0;
		
		int toIndex_x=0, toIndex_y=0;
		
	    // Current Tile offset (to pixels)
		int tileOffset_x =  -( (int)camPos.x % this.tileWidth );
	    toIndex_x    = ( tileOffset_x + (int)camPos.x) / this.tileWidth;

	    // current tile y offset (to pixels)
	    int tileOffset_y = -( (int)camPos.y % this.tileHeight);
	    toIndex_y    = (tileOffset_y + (int)camPos.y) / this.tileHeight;
	    
		indexY = toIndex_y;
		for (pixelY = tileOffset_y;
		     pixelY < viewport.getHeight() && indexY < this.maxY; 		     
		     pixelY += this.tileHeight, indexY++) {
			
			for (pixelX = tileOffset_x, indexX = toIndex_x; 
			     pixelX < viewport.getWidth() && indexX < this.maxX; 
			     pixelX += this.tileWidth, indexX++) {
				
				if ( (indexY >= 0 && indexX >= 0) && (indexY < this.maxY && indexX < this.maxX) ) {
					
					//for(int i = this.foregroundLayers.length - 1; i >= 0; i--) 
					for(int i = 0; i < this.foregroundLayers.length; i++)
					{
						Layer layer = this.foregroundLayers[i];
						if ( layer == null ) {
							continue;
						}
						
						if ( layer.isPropertyLayer()) {
							continue;
						}
						
						Tile tile = layer.getRow(indexY).get(indexX);
//						Tile maskedTile = getTile(0, indexX, indexY);
						if ( tile != null ) {
//							if(maskedTile == null || maskedTile.getMask() == 0) {
//								canvas.setCompositeAlpha(0.1f);
							
							
							tile.setRenderingPosition(pixelX + vx, pixelY + vy);
							tile.render(canvas, camera, alpha);
//							}							
//							canvas.setCompositeAlpha(1f);
							
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see leola.live.game.Map#renderSolid(leola.live.gfx.Canvas, leola.live.gfx.Camera)
	 */
	public void renderSolid(Canvas canvas, Camera camera, long alpha) {
		Vector2f camPos = camera.getPosition();
		Rectangle viewport = camera.getViewPort();
		
		int currentColor = canvas.getColor();
		
	//	this.shadeTilesLookup = createShadeLookup(45);
		
		// the viewport x, and y location
		int vx = viewport.getX();
		int vy = viewport.getY(); 
		
		// screen pixel x,y coordinate to draw the current tile to
		int pixelX = 0;
		int pixelY = 0;

		int indexX = 0;
		int indexY = 0;
		
		int toIndex_x=0, toIndex_y=0;
		
	    // Current Tile offset (to pixels)
		int tileOffset_x =  -( (int)camPos.x % this.tileWidth );
	    toIndex_x    = ( tileOffset_x + (int)camPos.x) / this.tileWidth;

	    // current tile y offset (to pixels)
	    int tileOffset_y = -( (int)camPos.y % this.tileHeight);
	    toIndex_y    = (tileOffset_y + (int)camPos.y) / this.tileHeight;	    	    
	    
		indexY = toIndex_y;
		Layer layer = this.backgroundLayers[0];
		for (pixelY = tileOffset_y;
		     pixelY < viewport.getHeight() && indexY < this.maxY; 		     
		     pixelY += this.tileHeight, indexY++) {
			
			for (pixelX = tileOffset_x, indexX = toIndex_x; 
			     pixelX < viewport.getWidth() && indexX < this.maxX; 
			     pixelX += this.tileWidth, indexX++) {
				
				if ( (indexY >= 0 && indexX >= 0) && (indexY < this.maxY && indexX < this.maxX) ) {																
					Tile tile = layer.getRow(indexY).get(indexX);
					if ( tile != null ) {
						int mask = tile.getMask();
						if(mask==0) {							
							canvas.fillRect(pixelX + vx, pixelY + vy, tileWidth, tileHeight, currentColor);
						}
						else if (mask > 1) {
							
							int px = pixelX + vx;
							int py = pixelY + vy;
							
							TextureRegion image = this.shadeTilesLookup.get(mask-1);
							if(image!=null) {								
								canvas.drawImage(image, px, py, null);
							}
						}
					}
					
				}
			}
		}		
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.myriad.render.Renderable#update(org.myriad.core.TimeStep)
	 */
	
	public void update(TimeStep timeStep) {
		if ( this.mapOffset==null || this.currentFrameViewport==null ) {
			return;
		}
		
		Vector2f camPos = this.mapOffset;
		Rectangle viewport = this.currentFrameViewport;
		
		// screen pixel x,y coordinate to draw the current tile to
		int pixelX = 0;
		int pixelY = 0;

		int indexX = 0;
		int indexY = 0;

		int toIndex_x=0, toIndex_y=0;
		
	    // Current Tile offset (to pixels)
		int tileOffset_x =  -( (int)camPos.x % this.tileWidth );
	    // to next index
	    toIndex_x    = ( tileOffset_x + (int)camPos.x) / this.tileWidth;

	    // current tile y offset (to pixels)
	    int tileOffset_y = -( (int)camPos.y % this.tileHeight);
	    toIndex_y    = (tileOffset_y + (int)camPos.y) / this.tileHeight;

		indexY = toIndex_y;
		for (pixelY = tileOffset_x;
		     pixelY < viewport.getHeight() && indexY < this.maxY; 		     
		     pixelY += this.tileHeight, indexY++) {
			
			for (pixelX = toIndex_y, indexX = toIndex_x; 
			     pixelX < viewport.getWidth() && indexX < this.maxX; 
			     pixelX += this.tileWidth, indexX++) {
				
				if ( (indexY >= 0 && indexX >= 0) && (indexY < this.maxY && indexX < this.maxX) ) {
					for (Layer layer : this.backgroundLayers) {
						if ( layer == null ) {
							continue;
						}
						
						Tile tile = layer.getRow(indexY).get(indexX);
						if ( tile != null ) {
							tile.update(timeStep);
						}
					}
					
					for (Layer layer : this.foregroundLayers) {
						if ( layer == null ) {
							continue;
						}
						
						Tile tile = layer.getRow(indexY).get(indexX);
						if ( tile != null ) {
							tile.update(timeStep);
						}
					}
				}

			}
		}
	}
	
	/**
	 * Render the background image
	 * 
	 * @param r
	 * @param camera
	 */
	private void renderBackground(Canvas canvas, Camera camera) {
		if ( this.backgroundImage == null ) {
			return;
		}
				
		Rectangle viewport = camera.getViewPort();
						
		canvas.drawScaledImage(this.backgroundImage
				 , viewport.getX()
				 , viewport.getY()
				 , viewport.getWidth()
				 , viewport.getHeight(), 0xFFFFFFFF);
		
//		r.drawImage(this.backgroundImage
//				 , (int) screenCord.x
//				 , (int) screenCord.y
//				 , viewport.getWidth()
//				 , viewport.getHeight()
//				 , camPos.x
//				 , camPos.y);
	}

	/* (non-Javadoc)
	 * @see seventh.map.Map#getSurfaceTypeByIndex(int, int)
	 */
	@Override
	public SurfaceType getSurfaceTypeByIndex(int x, int y) {
		return this.surfaces[y][x];
	}

	/* (non-Javadoc)
	 * @see seventh.map.Map#getSurfaceTypeByWorld(int, int)
	 */
	@Override
	public SurfaceType getSurfaceTypeByWorld(int x, int y) {
		if(checkBounds(x, y)) {
			return null;
		}
		
		int tileOffset_x = 0;
		int wx = (tileOffset_x + x) / this.tileWidth;

		int tileOffset_y = 0;
		int wy = (tileOffset_y + y) / this.tileHeight;
		return this.surfaces[wy][wx];
	}

	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("width", this.mapWidth)
		  .add("height", this.mapHeight)
		  .add("tileWidth", this.tileWidth)
		  .add("tileHeight", this.tileHeight)
		  .add("maxX", this.maxX)
		  .add("maxY", this.maxY);
		
		String[] tiles = new String[this.maxY];
		for(int i = 0; i < tiles.length; i++) {
			tiles[i] = "\"";
		}
		
		Layer[] layers = getCollidableLayers();
		for(int y = 0; y < getTileWorldHeight(); y++) {
			for(int x = 0; x < getTileWorldWidth(); x++) {
				Tile topTile = null;
				for(int i = 0; i < layers.length; i++) {
					Tile tile = layers[i].getRow(y).get(x); 
					if(tile != null) {
						topTile = tile;
						break;
					}
				}
				
				if(topTile != null) {
					tiles[y] += "X";
				}
				else {
					tiles[y] += "O";
				}
			}
		}
		
		for(int i = 0; i < tiles.length; i++) {
			tiles[i] += "\"";
		}
		
		me.add("tiles", tiles);
		
		return me;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {	
		return getDebugInformation().toString();
	}

}
