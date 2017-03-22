package atrai.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;


/**
 * Objects of type {@link InternalNode} forms the internal nodes of untyped trees, patterns, and templates.
 * An InternalNode is produced by a non-terminal in a grammar. It can have multiple untyped children.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class InternalNode extends TreeNode {
    private Object[] children;
    private ObjectArrayList<Object> tmpChildren;
    private boolean hasStar;

    /**
     * Create a new internal node with an unspecified number of children
     */
    InternalNode() {
        tmpChildren = new ObjectArrayList<>();
        this.hasStar = false;
    }

    /**
     * Create a new internal node with the number of children pre-allocated.
     *
     * @param nChildren The number of children this node will have
     */
    private InternalNode(int nChildren) {
        this.children = new Object[nChildren];
        this.hasStar = false;
    }

    static void printTerminal(StringBuilder sb, Object child) {
        if (child instanceof WildcardToken) {
            sb.append(child.toString());
        } else if (child instanceof WildcardCaptureToken) {
            sb.append(child.toString());
        } else if (child instanceof WildcardStarCaptureToken) {
            sb.append(child.toString());
        } else if (child instanceof ReplacementToken) {
            sb.append(child.toString());
        } else if (child instanceof ReplacementFlattenToken) {
            sb.append(child.toString());
        } else if (child instanceof ReplacementStarToken) {
            sb.append(child.toString());
        } else {
            sb.append(SerializedTreeParser.escapeString(child.toString()));
        }
    }

    private void finalizeNode() {
        if (tmpChildren != null) {
            children = new Object[tmpChildren.size()];
            int i = 0;
            for (Object tmp : tmpChildren) {
                children[i] = tmp;
                i++;
            }
            tmpChildren = null;
        }
    }

    /**
     * Recursively visits each {@link InternalNode} among its children and replaces
     * the {@link #children} array with the values stored in {@link #tmpChildren}
     */
    void finalizeAST() {
        finalizeNode();
        for (Object child : this.children) {
            if (child instanceof InternalNode) {
                ((InternalNode) child).finalizeAST();
            }
        }
    }

    /**
     * Determines whether another tree rooted at an {@link InternalNode} matches this {@link Pattern} tree.
     * It compares the structure of the two trees, using {@link TreeNode#matches(Object, ObjectArrayList)}
     * for {@link TreeNode}s and {@link Object#equals(Object)} for other types.
     * The other nodes populate {@code captures}
     *
     * @param other    The tree to compare this tree to.
     * @param captures Populated by the matching behavior of {@code other}
     * @return True if the trees matched, false otherwise
     */
    boolean matches(Object other, ObjectArrayList<Object> captures) {
        if (!(other instanceof InternalNode)) {
            return false;
        }

        InternalNode otherTmp = (InternalNode) other;
        if (this.hasStar) {
            if (this.children.length > otherTmp.children.length + 1) {
                return false;
            }
        } else if (this.children.length != otherTmp.children.length) {
            return false;
        }

        for (int i = 0; i < this.children.length; i++) {
            if (children[i] instanceof WildcardStarCaptureToken) {
                int len = otherTmp.children.length-this.children.length+1;
                ((WildcardStarCaptureToken) children[i]).match(otherTmp.children, i, len, captures);
                i = i + len;
            } else if (children[i] instanceof WildcardStarToken) {
                int len = otherTmp.children.length-this.children.length+1;
                i = i + len;
            } else if (children[i] instanceof TreeNode) {
                if (!((TreeNode) children[i]).matches(otherTmp.children[i], captures)) {
                    return false;
                }
            } else if (!children[i].equals(otherTmp.children[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a new tree from calling {@link TreeNode#replace(Object[])} on all appropriate
     * parts of the tree. Deep copies of non-node objects are not made.
     *
     * @param captures Used by {@link ReplacementToken} or custom classes to
     *                 replace parts of the tree with elements from this array.
     * @return The new root of the tree.
     */
    InternalNode replace(Object[] captures) {
        boolean flag = false;
        for (int i = 0; i < this.children.length; i++) {
            if (this.children[i] instanceof ReplacementFlattenToken || this.children[i] instanceof ReplacementStarToken) {
                flag = true;
            }
        }
        if (flag) {
            InternalNode ret = new InternalNode();
            for (int i = 0; i < this.children.length; i++) {
                Object o = this.children[i];
                if (o instanceof TreeNode) {
                    Object tmp = ((TreeNode) o).replace(captures);
                    if (o instanceof ReplacementFlattenToken) {
                        if (!(tmp instanceof InternalNode)) {
                            throw new RuntimeException(o + " needs to be replaced with the children of an untyped tree and cannot be replaced with " + tmp);
                        } else {
                            InternalNode tmp2 = (InternalNode) tmp;
                            for (int j = 0; j < tmp2.children.length; j++) {
                                ret.addChild(tmp2.children[j]);
                            }
                        }
                    } else if (o instanceof ReplacementStarToken) {
                        if (!(tmp instanceof Object[])) {
                            throw new RuntimeException(o + " needs to be replaced with an array of objects and cannot be replaced with " + tmp);
                        } else {
                            Object[] tmp2 = (Object[]) tmp;
                            for (int j = 0; j < tmp2.length; j++) {
                                ret.addChild(tmp2[j]);
                            }
                        }
                    } else {
                        ret.addChild(tmp);
                    }
                } else {
                    ret.addChild(o);
                }
            }
            ret.finalizeNode();
            return ret;
        } else {
            InternalNode ret = new InternalNode(this.children.length);
            for (int i = 0; i < this.children.length; i++) {
                Object o = this.children[i];
                if (o instanceof TreeNode) {
                    Object tmp = ((TreeNode) o).replace(captures);
                    ret.children[i] = tmp;
                } else {
                    ret.children[i] = o;
                }
            }
            return ret;

        }
    }

    /**
     * Prints the tree into a {@link StringBuilder}
     *
     * @param sb The {@link StringBuilder} to populate.
     */
    void toSourceString(StringBuilder sb) {
        for (int i = 0; i < this.children.length; i++) {
            if (i != 0) {
                sb.append(' ');
            }
            Object child = this.children[i];
            if (child instanceof InternalNode) {
                sb.append(SerializedTreeParser.LBS);
                ((InternalNode) child).toSourceString(sb);
                sb.append(SerializedTreeParser.RBS);
            } else {
                printTerminal(sb, child);
            }
        }
    }

    /**
     * Pretty prints the tree into a {@link StringBuilder}
     *
     * @param sb     The {@link StringBuilder} to populate.
     * @param indent The sequence to use as indentation (tabs or some amount of spaces)
     */
    public void toIndentedString(StringBuilder sb, String indent) {
        int START = 3;
        for (int i = 0; i < this.children.length; i++) {
            if (i != 0) {
                sb.append(' ');
            }
            boolean inline1 = this.children.length == START + 1 && !(this.children[START] instanceof InternalNode);
            if (!inline1 && i == START) {
                sb.append("\n").append(indent);
            }
            Object child = this.children[i];
            if (child instanceof InternalNode) {
                InternalNode tmp = ((InternalNode) child);
                boolean skip = tmp.children.length == START + 1 && !(tmp.children[START] instanceof InternalNode);
                if (!skip && i > START) {
                    sb.append("\n").append(indent);
                }
                sb.append(SerializedTreeParser.LBS);
                tmp.toIndentedString(sb, indent + INDENTATION);
                //if (!skip) sb.append("\n").append(indent);
                sb.append(SerializedTreeParser.RBS);
                if (!skip && i < this.children.length - 1) sb.append("\n").append(indent);
            } else {
                printTerminal(sb, child);
            }

            if (!inline1 && i == children.length - 1) {
                sb.append("\n").append(indent.substring(0, indent.length() - INDENTATION.length()));
            }
        }
    }

    /**
     * Gets the only child of the node, otherwise throws an exception if more than one child exist (or none do)
     *
     * @return The unique child of the node
     * @throws RuntimeException when the node does not have a single child
     */
    Object getOnlyChild() {
        if (children.length != 1) {
            throw new RuntimeException(this + " must be a leaf or an internal node");
        }
        return children[0];
    }

    /**
     * If the node has not been finalized, this will add {@code child} after the rest of the children.
     *
     * @param child The child to append
     */
    void addChild(Object child) {
        if (child instanceof WildcardStarCaptureToken) {
            if (hasStar) {
                throw new RuntimeException("An internal node can have at most one "+WildcardStarCaptureToken.instance + " or "+ WildcardStarToken.instance+" children.");
            } else {
                hasStar = true;
            }
        }
        this.tmpChildren.push(child);
    }

    /**
     * Displays the string for debugging / re-parsing later.
     *
     * @return a string representation
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SerializedTreeParser.LBS);
        this.toSourceString(sb);
        sb.append(SerializedTreeParser.RBS);
        return sb.toString();
    }

    /**
     * Accept a visitor to traverse the children of this node.  Returns the modified tree.  The original tree is not
     * modified.
     *
     * @param visitor the visitor to accept
     * @param context context to be passed to the modifiers
     */
    InternalNode accept(Visitor visitor, Object context) {
        InternalNode ret = this;
        for (int i = 0; i < this.children.length; i++) {
            Object child = this.children[i];
            if (child instanceof InternalNode) {
                Object tmp = visitor.visit(child, context);
                if (tmp != this.children[i] && ret == this) {
                    ret = new InternalNode(this.children.length);
                    for (int j = 0; j < i; j++) {
                        ret.children[j] = this.children[j];
                    }
                }
                ret.children[i] = tmp;
            } else {
                ret.children[i] = this.children[i];
            }
        }
        return ret;
    }

//    /**
//     * Accept a visitor to traverse and modify the children of this node.
//     *
//     * @param visitor the visitor to accept
//     */
//    InternalNode accept(Visitor visitor) {
//        return accept(visitor, null);
//    }
//
    /**
     * Iterates the children of this {@link InternalNode} and applies {@code lambda} to each children.  The {@code lambda}
     * gets the child as the first argument, the reduced value of the previously visited children, and the {@code environment}
     * as arguments.  It returns the updated reduction value, which becomes the second argument for the application
     * of the {@code lambda} on the next children.
     *
     * @param lambda                function applied to each children
     * @param initialReductionValue initial reduction value
     * @param context               the context
     * @return return the reduction value
     */
    public Object iterate(Reducer lambda, Object initialReductionValue, Object context) {
        for (int i = 0; i < children.length; i++) {
            initialReductionValue = lambda.apply(children[i], initialReductionValue, context);
        }
        return initialReductionValue;
    }
}
