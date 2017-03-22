package atrai.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import atrai.antlr.Location;

import java.util.ArrayList;

/**
 * A specialized {@link Tree} for matching against other trees.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class Pattern extends Tree {
    private final ObjectArrayList<Object> tmpCaptures = new ObjectArrayList<>(10);
    private Object[] lastMatches = null;

    /**
     * Construct a new Pattern tree from the parsed pattern and locations array
     *
     * @param treeNode  The root of the parsed pattern
     * @param locations The corresponding source locations
     */
    private Pattern(Object treeNode, ArrayList<Location> locations) {
        super(treeNode, locations);
    }

    /**
     * Create a Pattern from a string representing the pattern and a lexer for the string.
     * These patterns use the following special symbols to construct trees:
     * <p>
     * (% %) - matching pairs of these symbols group children of a tree
     * \@ - a non-capture group
     * \@_   - a capture group
     * `     - backtick, escape next character
     *
     * @param source A string representing the pattern
     * @param lexer  The lexer for patterns
     * @return A new pattern tree
     */
    public static Pattern parse(String source, Lexer lexer) {
        TreeBuilder builder = SerializedTreeParser.parse(source, lexer, true, false);
        return new Pattern(builder.getTree(), builder.getLocations());
    }

    /**
     * Convenience overload for matching against an untyped tree.
     *
     * @param source the tree to match against
     * @return the captured matches
     */
    public Object[] match(UntypedTree source) {
        try {
            return match(source.rootNode);
        } catch (Exception e) {
            throw new RuntimeException("Cannot match " + this + " against " + source + ". " + e.getMessage());
        }
    }

    /**
     * Matches the pattern against node or a leaf in an untyped tree. Sets the 0th capture
     * to be the overall tree, then recurs to match other nodes.
     *
     * @param source The tree to match
     * @return the captured matches
     */
    public Object[] match(Object source) {
        tmpCaptures.clear();
        tmpCaptures.add(source);
        if (rootNode instanceof TreeNode) {
            if (((TreeNode) rootNode).matches(source, tmpCaptures)) {
                return (lastMatches = tmpCaptures.toArray());
            } else {
                return (lastMatches = null);
            }
        } else if (rootNode.equals(source)) {
            return (lastMatches = tmpCaptures.toArray());
        } else {
            return (lastMatches = null);
        }
    }

    /**
     * Returns the array of captures from the last {@link Pattern#match call}
     *
     * @return the array of captures from the last match or null if thet last match failed
     */
    public Object[] getMatches() {
        return lastMatches;
    }

    /**
     * Returns the capture at {@code index} in the array of captures obtained during the last match
     *
     * @param index index of the capture to return
     * @return object at {@code index} in the array of captures
     */
    public Object getMatch(int index) {
        return lastMatches[index];
    }
}
