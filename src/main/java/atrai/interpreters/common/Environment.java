package atrai.interpreters.common;


/**
 * Represents the current execution environment for LET/LETREC-style programs.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class Environment {
    private final String key;
    private final Environment next;
    private Object val;

    private Environment(String key, Object val, Environment next) {
        this.key = key;
        this.val = val;
        this.next = next;
    }

    public static Environment extend(String key, Object value, Environment env) {
        return new Environment(key, value, env);
    }

    public Object get(String key) {
        Environment tmp = this;
        while (tmp != null) {
            if (key.equals(tmp.key)) {
                return tmp.val;
            }
            tmp = tmp.next;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Environment tmp = this;
        sb.append("Environment {");
        while (tmp != null) {
            sb.append(tmp.key).append(":").append(tmp.val);
            tmp = tmp.next;
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean set(String s, Object val) {
        Environment tmp = this;
        while (tmp != null) {
            if (s.equals(tmp.key)) {
                tmp.val = val;
                return true;
            }
            tmp = tmp.next;
        }
        return false;
    }
}
