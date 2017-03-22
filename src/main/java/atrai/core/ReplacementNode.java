package atrai.core;

/**
 * Created by ksen on 3/11/17.
 */
abstract public class ReplacementNode extends TreeNode {
    int index;

    /**
     * Replace {@link ReplacementToken} holes in the tree with the capture groups
     *
     * @param captures the capture groups
     * @return the capture to be used for creating the replacement
     */
    @Override
    Object replace(Object[] captures) {
        if (index < 0 || index > captures.length-1) {
            throw new RuntimeException("Replacement failed for " + this+". No capture at index "+index);
        }
        return captures[index];

    }
}
