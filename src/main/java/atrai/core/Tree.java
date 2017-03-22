package atrai.core;

import atrai.antlr.Location;

import java.util.ArrayList;

/**
 * Parent type for trees. Supports pretty printing and location lookups.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class Tree {
    final ArrayList<Location> locations;
    protected Object rootNode;

    /**
     * Create a new tree from a root node and a locations list
     *
     * @param rootNode  root node
     * @param locations corresponding source locations
     */
    Tree(Object rootNode, ArrayList<Location> locations) {
        this.rootNode = rootNode;
        this.locations = locations;
    }

    /**
     * Dump the tree as a string.
     *
     * @return string representation of the tree
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (rootNode instanceof TreeNode) {
            if (rootNode instanceof InternalNode)
                sb.append(SerializedTreeParser.LBS);
            ((TreeNode) rootNode).toSourceString(sb);
            if (rootNode instanceof InternalNode)
                sb.append(SerializedTreeParser.RBS);
        } else {
            InternalNode.printTerminal(sb,rootNode);
        }
        return sb.toString();
    }

    /**
     * Pretty print the tree with indentation.
     *
     * @return pretty string representation of the tree
     */
    public String toIndentedString() {
        StringBuilder sb = new StringBuilder();
        if (rootNode instanceof TreeNode) {
            if (rootNode instanceof InternalNode)
                sb.append(SerializedTreeParser.LBS);
            ((TreeNode) rootNode).toIndentedString(sb, TreeNode.INDENTATION);
            if (rootNode instanceof InternalNode)
                sb.append(SerializedTreeParser.RBS);
        } else {
            InternalNode.printTerminal(sb,rootNode);
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Look up a source location given a tree node id.
     *
     * @param id the tree node id
     * @return the location in the source that generated that tree node
     */
    public Location getLocationFromID(int id) {
        return locations.get(id);
    }
}
