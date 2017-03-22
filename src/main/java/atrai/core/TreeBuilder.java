package atrai.core;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import atrai.antlr.Location;

import java.util.ArrayList;

/**
 * A utility class for incrementally building a tree
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class TreeBuilder {
    private final Stack<InternalNode> stack = new ObjectArrayList<>();
    private InternalNode current;
    private InternalNode root;
    private ArrayList<Location> locations;

    /**
     * Create a new tree builder
     */
    public TreeBuilder() {
        reset();
    }

    /**
     * Clear the tree and reset the builder to the initial state
     */
    public void reset() {
        locations = new ArrayList<>();
        current = null;
    }

    /**
     * Begin creating a subtree rooted at the current position.
     * Must be closed by a corresponding call to {@link #endSubTree()}
     */
    public void beginSubTree() {
        if (stack.isEmpty()) {
            if (current != null) {
                throw new RuntimeException("TreeBuilder cannot discard the current rootNode.  Call reset() to discard current rootNode.");
            }
            current = root = new InternalNode();
        }
        stack.push(current);
        current = new InternalNode();
    }

    /**
     * Finish the subtree and insert it to the list of children of
     * the current node when the corresponding {@link #beginSubTree()}
     * method was called
     */
    public void endSubTree() {
        if (stack.isEmpty()) {
            throw new RuntimeException("TreeBuilder cannot discard the current rootNode.  Call reset() to discard current rootNode.");
        }
        TreeNode prev = current;
        current = stack.pop();
        current.addChild(prev);
    }

    /**
     * Add a leaf to the children of the current node.
     *
     * @param leaf the leaf to add
     */
    public void addLeaf(Object leaf) {
        if (stack.isEmpty()) {
            if (current != null) {
                throw new RuntimeException("TreeBuilder cannot discard the current rootNode");
            }
            current = root = new InternalNode();
        }
        current.addChild(leaf);
    }

    /**
     * Once the tree is finished (no subtrees are being built), finalize the tree and return it.
     *
     * @return the tree
     */
    public Object getTree() {
        if (!stack.isEmpty()) {
            throw new RuntimeException("Cannot return partially created rootNode.");
        }
        root.finalizeAST();
        return root.getOnlyChild();
    }

    /**
     * Add a source location to the list
     *
     * @param counter  the index in the sequence of positions (ie. the id of the tree node)
     * @param location the location to add
     */
    public void addLocation(int counter, Location location) {
        locations.add(counter, location);
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }
}
