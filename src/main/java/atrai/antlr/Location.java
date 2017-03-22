package atrai.antlr;

/**
 * Represents a location in a source code file.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class Location {
    public final int startLine;
    public final int startColumn;
    public final int endLine;
    public final int endColumn;

    /**
     * Creates a new Location object
     * @param startLine start line number
     * @param startColumn start column number
     * @param endLine end line number
     * @param endColumn end column number
     */
    public Location(int startLine, int startColumn, int endLine, int endColumn) {
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    @Override
    public String toString() {
        return String.format("[begin line: %d, begin column: %d, end line: %d, end column: %d]",
                startLine, startColumn, endLine, endColumn);
    }
}
