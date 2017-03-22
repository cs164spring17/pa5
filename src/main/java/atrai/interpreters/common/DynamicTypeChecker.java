package atrai.interpreters.common;

import atrai.antlr.Location;
import atrai.core.InternalNode;

/**
 * A run time type checker and caster for LET/LETREC-style languages.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public final class DynamicTypeChecker {

    /**
     * Checks if the {@code node} is not an instance of the {@link InternalNode}
     * @param node
     * @return
     */
    public static boolean isLeaf(Object node) {
        return !(node instanceof InternalNode);
    }

    /**
     * Casts and returns {@code o} to {@link Object[]}.  Throws exception if casting fails.
     *
     * @param o
     * @return
     */
    public static Object[] a(Object o) {
        if (o == null) return null;
        if (o instanceof Object[]) {
            return (Object[]) o;
        } else {
            throw new RuntimeException("Internal error: expecting object of type Object[] instead of " + o);
        }
    }

    /**
     * Casts and returns {@code o} to {@link InternalNode}.  Throws exception if casting fails.
     *
     * @param o
     * @return
     */
    public static InternalNode n(Object o) {
        if (o == null) return null;
        if (o instanceof InternalNode) {
            return (InternalNode) o;
        } else {
            throw new RuntimeException("Internal error: expecting object of type InternalNode instead of " + o);
        }
    }

    /**
     * Casts and returns {@code o} to {@link Environment}.  Throws exception if casting fails.
     *
     * @param o
     * @return
     */
    public static Environment e(Object o) {
        if (o == null) return null;
        if (o instanceof Environment) {
            return (Environment) o;
        } else {
            throw new RuntimeException("Internal error: expecting object of type Environment instead of " + o);
        }
    }

    /**
     * Casts and returns {@code o} to {@code int}.  Throws exception if casting fails.
     *
     * @param o
     * @return
     */
    public static int i(Object o, Location location) {
        if (o instanceof Integer) {
            return (Integer) o;
        } else {
            throw new SemanticException("Dynamic type checking failed: expecting int instead of " + o, location);
        }
    }

    /**
     * Casts and returns {@code o} to {@code boolean}.  Throws exception if casting fails.
     *
     * @param o
     * @return
     */
    public static boolean b(Object o, Location location) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            throw new SemanticException("Dynamic type checking failed: expecting boolean instead of " + o, location);
        }
    }

    /**
     * Casts and returns {@code o} to {@link String}.  Throws exception if casting fails.
     *
     * @param o
     * @return
     */
    public static String s(Object o, Location location) {
        if (o instanceof String) {
            return (String) o;
        } else {
            throw new SemanticException("Dynamic type checking failed: expecting String instead of " + o, location);
        }
    }

    /**
     * Converts {@code o} from {@link String} to {@code int}.  Throws exception if casting/conversion fails.
     *
     * @param o
     * @return
     */

    public static int s2i(Object o, Location location) {
        try {
            if (o instanceof String) {
                return Integer.parseInt((String) o);
            } else {
                throw new SemanticException("Dynamic type checking failed: expecting String of int instead of " + o, location);
            }
        } catch (NumberFormatException e) {
            throw new SemanticException("Dynamic type checking failed: expecting String of int instead of " + o, location);
        }
    }
}
