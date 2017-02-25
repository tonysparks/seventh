/*
 * see license.txt 
 */
package seventh.shared;

import leola.vm.Leola;
import leola.vm.types.LeoObject;

/**
 * Simple JSON utilities
 * 
 * @author Tony
 *
 */
public class JSON {

    /**
     * Parses the supplied json into a {@link LeoObject}.
     * 
     * @param runtime
     * @param json
     * @return the {@link LeoObject}
     * @throws Exception
     */
    public static LeoObject parseJson(Leola runtime, String json) throws Exception {
        /* TODO - replace with an actual JSON parser, this is 
         * a huge security hole
         */
        
        String contents = "return " + json.replace(":", "->"); /* converts to leola map format */        
        LeoObject mapData = runtime.eval(contents);
        return mapData;
    }
    
    /**
     * Parses the supplied json into a {@link LeoObject}
     * 
     * @param json
     * @return the {@link LeoObject}
     * @throws Exception
     */
    public static LeoObject parseJson(String json) throws Exception {
        return parseJson(Scripting.newSandboxedRuntime(), json);
    }

}
