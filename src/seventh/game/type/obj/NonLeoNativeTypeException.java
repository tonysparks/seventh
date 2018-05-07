package seventh.game.type.obj;

import seventh.shared.Cons;

public class NonLeoNativeTypeException extends Exception {
    public String toString(){
        return "*** ERROR -> objectives must either be an Array of objectives or a Java class or custom Leola class";
    }
    
}
