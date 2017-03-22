package atrai.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Created by ksen on 3/13/17.
 */
class WildcardStarToken extends CaptureNode {
    static final WildcardStarToken instance = new WildcardStarToken();

    private WildcardStarToken() {

    }

    @Override
    public boolean matches(Object other, ObjectArrayList<Object> captures) {
        throw new RuntimeException("Cannot have "+this+" at top level in a pattern.");
    }

    @Override
    public Object replace(Object[] captures) {
        throw new RuntimeException("Cannot have "+this+" in a template.");
    }

    @Override
    void toSourceString(StringBuilder sb) {
        sb.append(SerializedTreeParser.WILDCARDSTAR);
    }

    @Override
    public void toIndentedString(StringBuilder sb, String indent) {
        sb.append(SerializedTreeParser.WILDCARDSTAR);
    }

    @Override
    public String toString() {
        return SerializedTreeParser.WILDCARDSTAR;
    }

}
