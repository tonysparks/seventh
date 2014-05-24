/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;

import seventh.game.BombTarget;
import seventh.game.GameInfo;
import seventh.map.Map;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * A container for all the {@link Zone}s in a map
 * 
 * @author Tony
 *
 */
public class Zones {

	private Zone[][] zones;
	
	private final int zoneWidth, zoneHeight;
	private final int mapWidth, mapHeight;
	private final int numberOfCols, numberOfRows;
	private final int numberOfZones;
	private List<Zone> bombTargetZones;
	private GameInfo game;
		
	/**
	 * @param game
	 */
	public Zones(GameInfo game) {
		this.game = game;
		bombTargetZones = new ArrayList<>();
		
		final int ZONE_SIZE_IN_TILES = 12;
		
		Map map = game.getMap();

		mapWidth = map.getMapWidth();
		mapHeight = map.getMapHeight();
		
		zoneWidth = mapWidth / ZONE_SIZE_IN_TILES;
		zoneHeight = mapHeight / ZONE_SIZE_IN_TILES;
		
		
		numberOfCols = mapWidth / zoneWidth;
		numberOfRows = mapHeight / zoneHeight;
		
		numberOfZones = numberOfCols * numberOfRows;
		
		zones = new Zone[numberOfRows][numberOfCols];
		
		int id = 0;
		for(int y = 0; y < numberOfRows; y++) {
			for(int x = 0; x < numberOfCols; x++) {
				zones[y][x] = new Zone(id++, new Rectangle(x * zoneWidth, y * zoneHeight, zoneWidth, zoneHeight));
			}
		}			
	}

	
	/**
	 * Determines which Zone's the {@link BombTarget}s
	 * fall into
	 */
	public void calculateBombTargets() {
		for(int y = 0; y < numberOfRows; y++) {
			for(int x = 0; x < numberOfCols; x++) {
				zones[y][x].clearTargets();
			}
		}
		
		List<BombTarget> targets = game.getBombTargets();
		for(BombTarget target : targets) {
			Zone zone = getZone(target.getCenterPos());
			if(zone != null) {
				zone.addTarget(target);
				bombTargetZones.add(zone);
			}					
		}
	}
	
	/**
	 * @return the zones
	 */
	public Zone[][] getZones() {
		return zones;
	}
	
	/**
	 * @return the number of zones
	 */
	public int getNumberOfZones() {
		return this.numberOfZones;
	}
		
	
	/**
	 * @return the bombTargetZones
	 */
	public List<Zone> getBombTargetZones() {
		return bombTargetZones;
	}
	
	/**
	 * @param pos
	 * @return a {@link Zone} at a specified location
	 */
	public Zone getZone(Vector2f pos) {
		return getZone( (int)pos.x, (int)pos.y);
	}
	
	
	/**
	 * @param x
	 * @param y
	 * @return a {@link Zone} at a specified location
	 */
	public Zone getZone(int x, int y) {
		if(x<0 || y<0 || x>mapWidth || y>mapHeight) {
			return null;
		}
		
		int xIndex = x / zoneWidth;
		int yIndex = y / zoneHeight;
		
		if (xIndex > zones[0].length-1 || yIndex > zones.length-1) {
			return null;
		}
		
		
		return zones[yIndex][xIndex];
	}
	
	/**
	 * @param id
	 * @return the {@link Zone}
	 */
	public Zone getZoneById(int id) {
		// TODO
//		int y = (id / this.numberOfRows) - 1;
//		int x = (id-y) % this.numberOfCols;
//		return this.zones[y][x];
		return null;
	}
}
