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
	 * 
	 */
	public Zones(GameInfo game) {
		this.game = game;
		bombTargetZones = new ArrayList<>();
		
		final int ZONE_SIZE_IN_TILES = 8;
		
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
	
	public int getNumberOfZones() {
		return this.numberOfZones;
	}
	
	/**
	 * @return the bombTargetZones
	 */
	public List<Zone> getBombTargetZones() {
		return bombTargetZones;
	}
	
	public Zone getZone(Vector2f pos) {
		return getZone( (int)pos.x, (int)pos.y);
	}
	
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
}
