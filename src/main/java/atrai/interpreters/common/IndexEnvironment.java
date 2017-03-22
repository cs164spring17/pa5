package atrai.interpreters.common;

/**
 * Created by ksen on 3/7/17.
 */
public class IndexEnvironment {
    private final Object key;
    private final IndexEnvironment next;

    private IndexEnvironment(Object key, IndexEnvironment next) {
        this.key = key;
        this.next = next;
    }

    public static IndexEnvironment extend(Object key, IndexEnvironment env) {
        return new IndexEnvironment(key, env);
    }

    public int get(Object key) {
        IndexEnvironment tmp = this;
        int i = 0;
        while (tmp != null) {
            if (key.equals(tmp.key)) {
                return i;
            }
            tmp = tmp.next;
            i++;
        }
        return -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        IndexEnvironment tmp = this;
        sb.append("IndexEnvironment {");
        while (tmp != null) {
            sb.append(tmp.key).append(",");
            tmp = tmp.next;
        }
        sb.append("}");
        return sb.toString();
    }
}
