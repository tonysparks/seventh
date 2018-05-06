/*
 * see license.txt 
 */
package seventh.game.type.obj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import leola.vm.Leola;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoNativeClass;
import leola.vm.types.LeoObject;
import seventh.game.Team;
import seventh.game.type.AbstractGameTypeScript;
import seventh.game.type.GameType;
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
        final long timeBetweenRounds = 10_000L;

        File scriptFile = new File(mapFile + ".obj.leola");
        if (!scriptFile.exists()) {
            throw new NotExistScriptFileException();
        };

        LeoObject config = getRuntime().eval(scriptFile);
        if (LeoObject.isTrue(config)) {
            
            addscriptedObjectives(objectives, config);
            defenders = addScriptedDenfenders(defenders, config);
            alliedSpawnPoints = loadSpawnPoint(config, "alliedSpawnPoints");
            axisSpawnPoints = loadSpawnPoint(config, "axisSpawnPoints");
            minimumObjectivesToComplete = checkMinumumObjectCompleteSize(objectives, config);
            
        }

        return new ObjectiveGameType(getRuntime(), objectives, alliedSpawnPoints, axisSpawnPoints,
                minimumObjectivesToComplete, maxScore, matchTime, timeBetweenRounds, defenders);
        
    }

    private int checkMinumumObjectCompleteSize(List<Objective> objectives, LeoObject config) {
        int minimumObjectivesToComplete;
        if (config.hasObject("minimumObjectivesToComplete")) {
            minimumObjectivesToComplete = config.getObject("minimumObjectivesToComplete").asInt();
        } else {
            minimumObjectivesToComplete = objectives.size();
        }
        return minimumObjectivesToComplete;
    }

    private void addscriptedObjectives(List<Objective> objectives, LeoObject config) throws Exception {
        LeoObject scriptedObjectives = config.getObject("objectives");
        if (LeoObject.isTrue(scriptedObjectives)) {
            loadscriptObjectCase(objectives, scriptedObjectives);
        }
    }    
    private void loadscriptObjectCase(List<Objective> objectives, LeoObject scriptedObjectives) throws Exception{
        switch (scriptedObjectives.getType()) {
        case ARRAY: {
            addAllLeoObjectValues(objectives, scriptedObjectives);
            break;
        }
        case NATIVE_CLASS: {
            addNativeClassObjectValues(objectives, scriptedObjectives);
            break;
        }
        default: {
            throw new NonObjectTypeException();
        }
        }
    }

    private byte addScriptedDenfenders(byte defenders, LeoObject config) throws Exception {
        LeoObject scriptedDefenders = config.getObject("defenders");
        if (LeoObject.isTrue(scriptedDefenders)) {
            defenders = loadscriptedDefenderCase(defenders, scriptedDefenders);
        }
        return defenders;
    }

    private byte loadscriptedDefenderCase(byte defenders, LeoObject scriptedDefenders)throws Exception {
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
            throw new NonDefenderTypeException();
        }
        }
        return defenders;
    }


    private void addAllLeoObjectValues(List<Objective> objectives, LeoObject scriptedObjectives) throws Exception {
        LeoArray array = scriptedObjectives.as();
        for (int i = 0; i < array.size(); i++) {
            addLeoObjectValues(objectives, array, i);
        }
    }

    private void addNativeClassObjectValues(List<Objective> objectives, LeoObject scriptedObjectives)throws Exception {
        LeoObject o = scriptedObjectives;
        addObjectValue(objectives, o);
    }


    private void addLeoObjectValues(List<Objective> objectives, LeoArray array, int i) throws Exception {
        LeoObject o = array.get(i);
        if (o instanceof LeoNativeClass) {
            addObjectValue(objectives, o);
        }
    }
    private void addObjectValue(List<Objective> objectives, LeoObject o) throws NonLeoNativeTypeException {
        if (o.getValue() instanceof Objective) {
            objectives.add((Objective)o.getValue());
        } else {
            printNonNativeClassTypeToConsole(o);
            throw new NonLeoNativeTypeException();
        }
    }


    private void printNonNativeClassTypeToConsole(LeoObject o) {
        Cons.println(((LeoNativeClass) o).getNativeClass() + " is not of type: "+ Objective.class.getName());
    }
}
