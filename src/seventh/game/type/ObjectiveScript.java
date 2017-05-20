/*
 * see license.txt 
 */
package seventh.game.type;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import leola.vm.Leola;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoNativeClass;
import leola.vm.types.LeoObject;
import seventh.game.Team;
import seventh.math.Vector2f;
import seventh.shared.Cons;

/**
 * Loads an {@link Objective} script
 * 
 * @author Tony
 *
 */
public class ObjectiveScript extends AbstractGameTypeScript {

	/**
	 * 
	 */
	public ObjectiveScript(Leola runtime) {
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
		List<Objective> objectives = new ArrayList<>();
		List<Vector2f> alliedSpawnPoints = new ArrayList<>();
		List<Vector2f> axisSpawnPoints = new ArrayList<Vector2f>();
		byte defenders = Team.AXIS_TEAM_ID;
		int minimumObjectivesToComplete = 1;

		File scriptFile = new File(mapFile + ".obj.leola");
		/*
		 * Refactoring target : scriptFile.exist() Refactoring name : Introduce
		 * Assertion Bad smell(reason) : A section of code assumes something
		 * about the state of the program
		 * 
		 */
		Assert.assertTrue(scriptFile != null);
		/*
		 * Refactoring target : scriptFile.exist() Refactoring name : Replace
		 * Error Code with exception, Erase nested if statement Bad
		 * smell(reason) : "If statement" returns a special code to indicate an
		 * error and check only file exist
		 * 
		 */
		fileExist(scriptFile);

		/*
		 * Refactoring target : LeoObject.isTrue(config),
		 * LeoObject.isTrue(scriptedobjectives) Refactoring name : Replace Error
		 * Code with exception, Erase nested if statement Bad smell(reason) :
		 * "If statement" check only instance of LeoObject existed or not
		 */
		LeoObject config = getRuntime().eval(scriptFile);
		checkLeoObject(config);
		LeoObject scriptedObjectives = config.getObject("objectives");
		checkLeoObject(scriptedObjectives);

		/*
		 * Refactoring target : from the statement
		 * "if (o.getValue() instanceof Objective)" to statement "else"
		 * Refactoring name : extract function Bad smell(reason) : duplicated
		 * statement in switch sentence
		 */

		/*
		 * Refactoring target : switch statements about getType Refactoring name
		 * : extract function Bad smell(reason) : code complexity
		 */

		checkObjectivesType(objectives, scriptedObjectives);
		LeoObject scriptedDefenders = config.getObject("defenders");
		checkLeoObject(scriptedDefenders);
		defenders = checkDefendersType(defenders, scriptedDefenders);

		alliedSpawnPoints = loadSpawnPoint(config, "alliedSpawnPoints");
		axisSpawnPoints = loadSpawnPoint(config, "axisSpawnPoints");
		minimumObjectivesToComplete = setMinObjToCom(objectives, config);

		final long timeBetweenRounds = 10_000L;

		GameType gameType = new ObjectiveGameType(getRuntime(), objectives, alliedSpawnPoints, axisSpawnPoints,
				minimumObjectivesToComplete, maxScore, matchTime, timeBetweenRounds, defenders);
		/*
		 * Refactoring target : return gameType; Refactoring name : Introduce
		 * Assertion Bad smell(reason) : A section of code assumes something
		 * about the state of the program
		 * 
		 */
		Assert.assertTrue(gameType != null);
		return gameType;
	}

	private byte checkDefendersType(byte defenders, LeoObject scriptedDefenders) {
		switch (scriptedDefenders.getType()) {
		case INTEGER:
		case LONG:
		case REAL:
			defenders = (byte) scriptedDefenders.asInt();
			break;
		case STRING:
			if (Team.ALLIED_TEAM_NAME.equalsIgnoreCase(scriptedDefenders.toString())) {
				defenders = Team.ALLIED_TEAM_ID;
			}
			break;
		default: {
			Cons.println(
					"*** ERROR -> defenders must either be a 2(for allies) or 4(for axis) or 'allies' or 'axis' values");
		}
		}
		return defenders;
	}

	private void checkObjectivesType(List<Objective> objectives, LeoObject scriptedObjectives) {
		switch (scriptedObjectives.getType()) {
		case ARRAY: {
			LeoArray array = scriptedObjectives.as();
			for (int i = 0; i < array.size(); i++) {
				LeoObject o = array.get(i);
				if (o instanceof LeoNativeClass) {
					setObjective(objectives, o);
				}
			}
			break;
		}
		case NATIVE_CLASS: {
			LeoObject o = scriptedObjectives;
			setObjective(objectives, o);
			break;
		}
		default: {
			Cons.println(
					"*** ERROR -> objectives must either be an Array of objectives or a Java class or custom Leola class");
		}
		}
	}

	private void setObjective(List<Objective> objectives, LeoObject o) {
		if (o.getValue() instanceof Objective) {
			Objective objective = (Objective) o.getValue();
			objectives.add(objective);
		} else {
			Cons.println(((LeoNativeClass) o).getNativeClass() + " is not of type: " + Objective.class.getName());
		}
	}

	/*
	 * Refactoring target : minimumObjectivesToComplete Refactoring name :
	 * extract function Bad smell(reason) : Turn the statements into its own
	 * function
	 * 
	 */
	private int setMinObjToCom(List<Objective> objectives, LeoObject config) {
		int minimumObjectivesToComplete;
		if (config.hasObject("minimumObjectivesToComplete")) {
			minimumObjectivesToComplete = config.getObject("minimumObjectivesToComplete").asInt();
		} else {
			minimumObjectivesToComplete = objectives.size();
		}
		return minimumObjectivesToComplete;
	}

	private void fileExist(File scriptFile) {
		try {
			if (!scriptFile.exists()) {
				throw new Exception();
			}
		} catch (Exception e) {
			Cons.println("*** ERROR -> No associated script file for objective game type.  Looking for: "
					+ scriptFile.getName());
		}

	}

	private void checkLeoObject(LeoObject Leo) {
		try {
			if (!LeoObject.isTrue(Leo)) {
				throw new Exception();
			}
		} catch (Exception e) {
			Cons.println("ERROR");
		}

	}

}
