package atrai.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Matches any subtree in the corresponding position without capturing it.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
class WildcardToken extends CaptureNode {
    public static final WildcardToken instance = new WildcardToken();

    @Override
    public boolean matches(Object other, ObjectArrayList<Object> captures) {
        return true;
    }

    @Override
    public Object replace(Object[] captures) {
        throw new RuntimeException("Cannot have "+this+" in a template.");
    }

    @Override
    void toSourceString(StringBuilder sb) {
        sb.append(SerializedTreeParser.WILDCARD);
    }

    @Override
    public void toIndentedString(StringBuilder sb, String indent) {
        sb.append(SerializedTreeParser.WILDCARD);
    }

    @Override
    public String toString() {
        return SerializedTreeParser.WILDCARD;
    }
}
