package seventh.game.type.obj;

public class NotExistScriptFileException extends Exception{
    
    public String toString() {
    return "*** ERROR -> No associated script file for objective game type";
    
    }
}
