package atrai.core;

import atrai.antlr.Location;

import java.util.ArrayList;

/**
 * A specialized {@link Tree} for rearranging parts of other trees. Think of this class
 * as a template, where captures from a {@link Pattern} fill in "missing" parts of this
 * tree.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class Template extends Tree {
    /**
     * Construct a new Template tree from the parsed replacement spec and locations
     *
     * @param treeNode  The root of the parsed replacement spec
     * @param locations The corresponding source locations
     */
    public Template(Object treeNode, ArrayList<Location> locations) {
        super(treeNode, locations);
    }

    /**
     * Create a Template from a string containing a replacement spec and a lexer.
     * It is similar to {@link Pattern} but uses the following syntax to paste captures,
     * instead of selecting them.
     * <p>
     * $<i>n</i> - replace this position with the <i>n</i>th capture
     *
     * @param source A string representing the replacement template
     * @param lexer  The lexer for the spec
     * @return A new replacement tree
     */
    public static Template parse(String source, Lexer lexer) {
        TreeBuilder builder = SerializedTreeParser.parse(source, lexer, false, true);
        return new Template(builder.getTree(), builder.getLocations());
    }

    /**
     * Apply the replacement pattern to the captures
     *
     * @param captures trees to be pasted into the replacement
     * @return root of the replaced tree
     */
    public Object replace(Object[] captures) {
        try {
            if (rootNode instanceof InternalNode || rootNode instanceof ReplacementToken) {
                return ((TreeNode) rootNode).replace(captures);
            } else if (rootNode instanceof ReplacementFlattenToken || rootNode instanceof ReplacementStarToken) {
                throw new RuntimeException("Cannot have " + this + " at top level in a template.");
            } else {
                return rootNode;
            }
        } catch(Exception e){
            throw new RuntimeException("Cannot use "+this+" to create an untyped tree. "+e.getMessage());
        }
    }

}
