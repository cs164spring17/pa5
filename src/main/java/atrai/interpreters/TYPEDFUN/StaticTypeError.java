package atrai.interpreters.TYPEDFUN;

import atrai.antlr.Location;

public class StaticTypeError extends RuntimeException {
    public StaticTypeError(String message, Object t1, Object t2, Location location) {
        super(message + " " + t1 + "!=" + t2 + " at " + location);
    }
}
