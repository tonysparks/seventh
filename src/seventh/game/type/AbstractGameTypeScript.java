/*
 * see license.txt 
 */
package seventh.game.type;

import java.util.ArrayList;
import java.util.List;

import leola.vm.Leola;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoObject;
import seventh.math.Vector2f;
import seventh.shared.Cons;

/**
 * Loads an game type script file
 * 
 * @author Tony
 *
 */
public abstract class AbstractGameTypeScript {

	private Leola runtime;

	/**
	 * 
	 */
	public AbstractGameTypeScript(Leola runtime) {
		this.runtime = runtime;
	}

	/**
	 * @return the runtime
	 */
	protected Leola getRuntime() {
		return runtime;
	}

	/**
	 * Parses the spawn points for a team
	 * 
	 * @param config
	 * @param teamSpawnPoints
	 * @return a list of spawn points
	 */
	protected List<Vector2f> loadSpawnPoint(LeoObject config, String teamSpawnPoints) {
		List<Vector2f> spawnPoints = new ArrayList<Vector2f>();

		LeoObject scriptSpawns = config.getObject(teamSpawnPoints);
		if (LeoObject.isTrue(scriptSpawns)) {
			if (!scriptSpawns.isArray()) {
				Cons.println(teamSpawnPoints + " must be an array");
			} else {
				LeoArray a = scriptSpawns.as();
				for (int i = 0; i < a.size(); i++) {
					Vector2f v = (Vector2f) a.get(i).getValue();
					spawnPoints.add(v);
				}
			}
		}

		return spawnPoints;
	}

	/**
	 * @param mapFile
	 * @param maxScore
	 * @param matchTime
	 * @return
	 * @throws Exception
	 */
	public abstract GameType loadGameType(String mapFile, int maxScore, long matchTime) throws Exception;
}