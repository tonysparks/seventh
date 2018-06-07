package seventh.game.type.obj;

public class NonDefenderTypeException extends Exception {
    public String toString(){
        return "*** ERROR -> defenders must either be a 2(for allies) or 4(for axis) or 'allies' or 'axis' values";
    }
}
