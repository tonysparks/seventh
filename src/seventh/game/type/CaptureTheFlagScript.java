/*
 * see license.txt 
 */
package seventh.game.type;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Cons;

/**
 * Loads an Capture The Flag rules script
 * 
 * @author Tony
 *
 */
public class CaptureTheFlagScript extends AbstractGameTypeScript {

	/**
	 * 
	 */
	public CaptureTheFlagScript(Leola runtime) {
		super(runtime);
	}

	/**
	 * @param mapFile
	 * @param maxScore
	 * @param matchTime
	 * @return
	 * @throws Exception
	 */
	public GameType loadGameType(String mapFile, int maxScore, long matchTime) throws Exception {
		
		List<Vector2f> alliedSpawnPoints = new ArrayList<>();
		List<Vector2f> axisSpawnPoints = new ArrayList<>();
		
		Vector2f alliedFlagSpawn = new Vector2f();
		Vector2f axisFlagSpawn = new Vector2f();
		
		Rectangle alliedHomeBase = new Rectangle();
		Rectangle axisHomeBase = new Rectangle();
		
		long spawnDelay = 10_000;
		
		File scriptFile = new File(mapFile + ".ctf.leola");
		if(!scriptFile.exists()) {
			Cons.println("*** ERROR -> No associated script file for 'Capture The Flag' game type.  Looking for: " + scriptFile.getName());
		}
		else {
			LeoObject config = getRuntime().eval(scriptFile);
			if(LeoObject.isTrue(config)) {
								
				alliedSpawnPoints = loadSpawnPoint(config, "alliedSpawnPoints");
				axisSpawnPoints = loadSpawnPoint(config, "axisSpawnPoints");
				
				alliedFlagSpawn = (Vector2f)config.getObject("alliedFlagSpawn").getValue();
				axisFlagSpawn = (Vector2f)config.getObject("axisFlagSpawn").getValue();
				
				alliedHomeBase = (Rectangle)config.getObject("alliedHomeBase").getValue();
				axisHomeBase = (Rectangle)config.getObject("axisHomeBase").getValue();
				
				if(config.hasObject("spawnDelay")) {
					spawnDelay = config.getObject("spawnDelay").asLong();
				}
			}
		}
		
		GameType gameType = new CaptureTheFlagGameType(getRuntime(), alliedSpawnPoints, axisSpawnPoints, maxScore, matchTime, 
				alliedFlagSpawn, axisFlagSpawn, alliedHomeBase, axisHomeBase, spawnDelay);
		return gameType;
	}
}
