package atrai.core;

import atrai.antlr.Location;

import java.util.ArrayList;

/**
 * A wrapper for rooted trees
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class UntypedTree extends Tree {

    /**
     * Construct a new untyped tree with given root and source positions.
     *
     * @param treeNode  the root
     * @param locations the corresponding source locations
     */
    public UntypedTree(Object treeNode, ArrayList<Location> locations) {
        super(treeNode, locations);
    }

    /**
     * Create a new untyped tree from some source code and a lexer.
     *
     * @param source The source code
     * @param lexer  The lexer
     * @return The tree
     */
    public static UntypedTree parse(String source, Lexer lexer) {
        TreeBuilder builder = SerializedTreeParser.parse(source, lexer, false, false);
        return new UntypedTree(builder.getTree(), builder.getLocations());
    }

    /**
     * Get the root of the tree (typically a {@link TreeNode})
     *
     * @return the root
     */
    public Object getRoot() {
        return rootNode;
    }

}
