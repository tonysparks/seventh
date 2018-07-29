/*
 * see license.txt 
 */
package seventh.shared;

import java.io.File;

import leola.vm.Args;
import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.math.Vector2f;
import seventh.server.SeventhScriptingCommonLibrary;

/**
 * Scripting runtime factory methods
 * 
 * @author Tony
 *
 */
public class Scripting {

    public static void execute(LeoObject function) {
        if(function != null) {
            LeoObject result = function.call();
            if(result.isError()) {
                Cons.println("*** ERROR -> " + result);
            }
        }
    }
    
    /**
     * Attempts to load a script file
     * 
     * @param runtime
     * @param scriptFile
     */
    public static LeoObject loadScript(Leola runtime, String scriptFile) {
        File file = new File(scriptFile);
        if(file.exists()) {
            try {                
                return runtime.eval(file);
            }
            catch(Exception e) {
                Cons.println("*** ERROR -> Loading " + file.getName() + ":" + e);
            }
        }
        
        return LeoObject.NULL;
    }
    
    /**
     * Creates a new {@link Leola} runtime that is in sandboxed mode.
     * 
     * @return the runtime
     */
    public static Leola newSandboxedRuntime() {        
        Leola runtime = Args.builder()
                            .setAllowThreadLocals(false)
                            .setIsDebugMode(true)
                            .setBarebones(true)
                            .setSandboxed(false) // TODO
                            .newRuntime();
        
        /* load some helper functions for objective scripts */
        runtime.loadStatics(SeventhScriptingCommonLibrary.class);
        runtime.loadStatics(Vector2f.class);
        runtime.put("console", Cons.getImpl());
        
        
        return runtime;
    }
    
    
    /**
     * Creates a new {@link Leola} runtime that is not in sandboxed mode.
     *  
     * @return the runtime
     */
    public static Leola newRuntime() {                
        Leola runtime = Args.builder()
                            .setIsDebugMode(true)
                            .setAllowThreadLocals(false)
                            .newRuntime();
        
        /* load some helper functions for objective scripts */
        runtime.loadStatics(SeventhScriptingCommonLibrary.class);
        runtime.loadStatics(Vector2f.class);
        runtime.put("console", Cons.getImpl());
        
        return runtime;
    }

}
