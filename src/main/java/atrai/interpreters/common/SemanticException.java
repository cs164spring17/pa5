package atrai.interpreters.common;

import atrai.antlr.Location;

/**
 * An exception that is thrown when the typechecker detects an error.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class SemanticException extends RuntimeException {
    private final Location location;

    public SemanticException(String message) {
        super(message);
        this.location = null;
    }

    public SemanticException(String message, Location location) {
        super(message);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
