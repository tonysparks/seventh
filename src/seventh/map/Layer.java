/*
 *	leola-live 
 *  see license.txt
 */
package seventh.map;

/**
 * A {@link Layer} in a {@link Map}.
 * 
 * @author Tony
 *
 */
public class Layer {

    /**
     * Iterator callback for iterating over a {@link Layer}s
     * {@link Tile}s
     * @author Tony
     *
     */
    public static interface LayerTileIterator {
        public void onTile(Tile tile, int x, int y);
    }
    
	/**
	 * Underlying layer
	 */
	private Tile[][] rows;
	
	/**
	 * Collidables
	 */
	private boolean canCollide;
	
	/**
	 * If its a foreground layers
	 */
	private boolean isForeground;
	
	/**
	 * Light layer
	 */
	private boolean isLightLayer;
	
	private boolean isVisible;
	
	/**
	 * If this is a property layer and 
	 * doesn't have render data
	 */
	private boolean isPropertyLayer;
	
	
	/**
	 * If this layer can be destroyed
	 */
	private boolean isDestructable;
	
	/**
	 * If this layer has animations
	 */
	private boolean hasAnimation;
	
	private int index;
	private int heightMask;
	
	private String name;
	
	/**
	 * Constructs a {@link Layer}.
	 */
	public Layer(String name,
			     boolean collidable, 
			     boolean isForeground, 
			     boolean isDestructable, 
			     boolean isLightLayer, 
			     boolean isVisible,
			     int index, 
			     int heightMask,
			     int numberOfRows) {
		
		this.name = name;
		this.rows = new Tile[numberOfRows][];		
		this.canCollide = collidable;
		this.isForeground = isForeground;
		this.isDestructable = isDestructable;
		this.isLightLayer = isLightLayer;
		this.isPropertyLayer = isLightLayer || collidable;
		this.isVisible = isVisible;
		this.index = index;
		this.heightMask = heightMask;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the heightMask
	 */
	public int getHeightMask() {
		return heightMask;
	}
		
	/**
	 * Applies the height mask to this layer
	 */
	public void applyHeightMask() {
		for(int rowIndex = 0; rowIndex < this.rows.length; rowIndex++) {
			Tile[] row = this.rows[rowIndex];
			for(int i = 0; i < row.length;i++) {
				Tile t = row[i];
				if(t!=null) {
					t.setHeightMask(heightMask);					
				}
			}
		}
	}
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * If the tiles on this layer are collidable.
	 * 
	 * @return
	 */
	public boolean collidable() {
		return this.canCollide;
	}
	
	/**
	 * @return the isForeground
	 */
	public boolean isForeground() {
		return isForeground;
	}
	
	/**
     * @return the isDestructable
     */
    public boolean isDestructable() {
        return isDestructable;
    }
	
	/**
	 * @return the isLightLayer
	 */
	public boolean isLightLayer() {
		return isLightLayer;
	}
	
	/**
	 * @return the isPropertyLayer
	 */
	public boolean isPropertyLayer() {
		return isPropertyLayer;
	}

	/**
	 * @return the isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/**
	 * @param isForeground the isForeground to set
	 */
	public void setForeground(boolean isForeground) {
		this.isForeground = isForeground;
	}

	/**
	 * The number of rows in this layer.
	 * 
	 * @return
	 */
	public int numberOfRows() {
		return this.rows.length;
	}
	/**
	 * Get a row in the layer.
	 * 
	 * @param i
	 * @return
	 */
	public Tile[] getRow(int i) {
		return this.rows[i];
	}
	
	/**
	 * Add a row.
	 * 
	 * @param row
	 */
	public void addRow(int index, Tile[] row) {
		this.rows[index] = row;		
		for(int i = 0; i < row.length;i++) {
			Tile t = row[i];
			if(t!=null) {
				t.setHeightMask(heightMask);
			}
		}
	}
	
	/**
	 * Iterate over each {@link Tile} in this {@link Layer}
	 * @param it
	 */
	public void foreach(LayerTileIterator it) {
	    int worldHeight = this.rows.length;
	    for(int y = 0; y < worldHeight; y++) {
	        
	        Tile[] row = getRow(y);
	        int worldWidth = row.length;
            for(int x = 0; x < worldWidth; x++) {
                Tile tile = row[x];
                it.onTile(tile, x, y);
            }
        }
	}
	
	public boolean hasAnimations() {
		return this.hasAnimation;
	}
	
	public void setContainsAnimations(boolean hasAnimations) {
		this.hasAnimation = hasAnimations;
	}
	
	public void destroy() {
		for(int rowIndex = 0; rowIndex < this.rows.length; rowIndex++) {
			Tile[] row = this.rows[rowIndex];
			for(int i = 0; i < row.length;i++) {
				row[i] = null;
			}
			this.rows[rowIndex] = null;
		}
	}
}
