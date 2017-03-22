package atrai.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * A leaf node that replaces itself with the given capture index.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
class ReplacementToken extends ReplacementNode {
    /**
     * Creates a new replacement token with the given index
     *
     * @param index the capture group to use as a replacement (e.g. $1 or $3)
     */
    public ReplacementToken(int index) {
        this.index = index;
    }

    /**
     * Invalid to call.
     *
     * @throws RuntimeException always
     */
    public boolean matches(Object other, ObjectArrayList<Object> captures) {
        throw new RuntimeException("Template tokens cannot be used in a pattern.");
    }

    /**
     * Prints the replacement to the given string builder
     *
     * @param sb the string builder to write to
     */
    @Override
    void toSourceString(StringBuilder sb) {
        sb.append(SerializedTreeParser.REPLACEPREFIX).append(index);
    }

    /**
     * Pretty prints the replacement to the given string builder
     *
     * @param sb the string builder to write to
     */
    @Override
    public void toIndentedString(StringBuilder sb, String indent) {
        sb.append(SerializedTreeParser.REPLACEPREFIX).append(index);
    }

    /**
     * @return string representation of the capture group: "$<i>{@link #index}</i>"
     */
    @Override
    public String toString() {
        return SerializedTreeParser.REPLACEPREFIX + index;
    }
}
