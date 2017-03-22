package atrai.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * A common interface for tree nodes.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
abstract class TreeNode {
    final public static String INDENTATION = "    ";

    /**
     * Checks for a match with another tree and populates {@code captures}
     *
     * @param other    the tree to match against
     * @param captures output parameter to place captures
     * @return true if matched, else false
     */
    abstract boolean matches(Object other, ObjectArrayList<Object> captures);

    /**
     * Replace {@link ReplacementToken} holes in the tree with the capture groups
     *
     * @param captures the capture groups
     * @return the modified tree
     */
    abstract Object replace(Object[] captures);

    /**
     * Print the original source for the tree (modulo whitespace)
     *
     * @param sb the output stream
     */
    abstract void toSourceString(StringBuilder sb);

    /**
     * Pretty print the tree node
     *
     * @param sb     the output stream
     * @param indent the indentation to use
     */
    public abstract void toIndentedString(StringBuilder sb, String indent);

}
