package atrai.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Created by ksen on 3/11/17.
 */
class WildcardStarCaptureToken extends CaptureNode {
    static final WildcardStarCaptureToken instance = new WildcardStarCaptureToken();

    private WildcardStarCaptureToken() {

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
        sb.append(SerializedTreeParser.WILDCARDSTARCAPTURE);
    }

    @Override
    public void toIndentedString(StringBuilder sb, String indent) {
        sb.append(SerializedTreeParser.WILDCARDSTARCAPTURE);
    }

    @Override
    public String toString() {
        return SerializedTreeParser.WILDCARDSTARCAPTURE;
    }

    public void match(Object[] children, int i, int length, ObjectArrayList<Object> captures) {
        Object[]  ret = new Object[length];
        for(int j=0; j<length; j++) {
            ret[j] = children[j+i];
        }
        captures.add(ret);
    }
}
