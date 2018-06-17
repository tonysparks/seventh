/*
 * see license.txt 
 */
package seventh.game.game_types.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import leola.vm.Leola;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import seventh.game.game_types.AbstractGameTypeScript;
import seventh.game.game_types.GameType;
import seventh.math.Vector2f;
import seventh.shared.Cons;

/**
 * Loads an Commander rules script
 * 
 * @author Tony
 *
 */
public class CommanderScript extends AbstractGameTypeScript {

    /**
     * 
     */
    public CommanderScript(Leola runtime) {
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
        List<Vector2f> axisSpawnPoints = new ArrayList<Vector2f>();
        
        LeoObject config = null;
        
        File scriptFile = new File(mapFile + ".cmd.leola");
        if(!scriptFile.exists()) {
            Cons.println("*** ERROR -> No associated script file for Commander game type.  Looking for: " + scriptFile.getName());
        }
        else {
            config = getRuntime().eval(scriptFile);
            if(LeoObject.isTrue(config)) {
                                
                alliedSpawnPoints = loadSpawnPoint(config, "alliedSpawnPoints");
                axisSpawnPoints = loadSpawnPoint(config, "axisSpawnPoints");
            }
        }
        
        GameType gameType = new CommanderGameType(getRuntime(), 
                alliedSpawnPoints, 
                axisSpawnPoints, 
                maxScore, 
                matchTime, 
                config==null ? new LeoMap() : config);
        
        return gameType;
    }
}
