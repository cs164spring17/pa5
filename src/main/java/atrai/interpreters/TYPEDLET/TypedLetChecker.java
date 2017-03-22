package atrai.interpreters.TYPEDLET;

import atrai.antlr.ANTLRTokenizer;
import atrai.antlr.GenericAntlrToUntypedTree;
import atrai.antlr.Location;
import atrai.core.Pattern;
import atrai.core.Transformer;
import atrai.core.UntypedTree;
import atrai.interpreters.common.Environment;
import atrai.interpreters.common.Interpreter;
import atrai.interpreters.common.SemanticException;

import static atrai.interpreters.TYPEDLET.PrimitiveTypeValue.BOOL;
import static atrai.interpreters.common.DynamicTypeChecker.*;

/**
 * Created by ksen on 3/9/17.
 */
class TypeValue {

}

class PrimitiveTypeValue extends atrai.interpreters.TYPEDLET.TypeValue {
    public static final atrai.interpreters.TYPEDLET.PrimitiveTypeValue INT = new atrai.interpreters.TYPEDLET.PrimitiveTypeValue(atrai.interpreters.TYPEDLET.PrimitiveTypeValue.TypeName.INT);
    public static final atrai.interpreters.TYPEDLET.PrimitiveTypeValue BOOL = new atrai.interpreters.TYPEDLET.PrimitiveTypeValue(atrai.interpreters.TYPEDLET.PrimitiveTypeValue.TypeName.BOOL);
    private final atrai.interpreters.TYPEDLET.PrimitiveTypeValue.TypeName val;

    private PrimitiveTypeValue(atrai.interpreters.TYPEDLET.PrimitiveTypeValue.TypeName val) {
        this.val = val;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    enum TypeName {INT, BOOL}

    @Override
    public String toString() {
        return val.toString();
    }
}

class StaticTypeError extends RuntimeException {
    final Location location;
    final Object t1;
    final Object t2;

    public StaticTypeError(String message, Object t1, Object t2, Location location) {
        super(message);
        this.location = location;
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public String toString() {
        return getMessage() + " " + t1 + "!=" + t2 + " at " + getLocation();
    }

    public Location getLocation() {
        return location;
    }
}


public class TypedLetChecker extends Interpreter {
    private String grammarName = "atrai.antlr.TYPEDLET";
    private ANTLRTokenizer tokenizer = new ANTLRTokenizer(grammarName);
    private Pattern typeExtractor = Pattern.parse("(%LET @ typed @ @_%)", tokenizer);

    private Object extractType(Object source) {
        return typeExtractor.match(source)[1];
    }

    @Override
    public Object interpret(UntypedTree st) {
        try {
            return interpretAux(grammarName, st);
        } catch (SemanticException ex) {
            System.err.println("Fatal Error: " + ex.getMessage() + " at " + ex.getLocation());
            throw ex;
        }
    }

    private Object interpretAux(String grammarName, UntypedTree st) {
        Transformer transformer = new Transformer(tokenizer);

        // note that tranformers are applied in the order in which they are added

        // first type check and wrap each sub-expression with type
        transformer.addTransformer("(%LET @_ expr @_ @_ @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            if (c[3].equals("+") || c[3].equals("-") || c[3].equals("/") || c[3].equals("*")) {
                c[2] = transformer.transform(c[2], E);
                c[4] = transformer.transform(c[4], E);
                Object oprnd1 = extractType(c[2]);
                Object oprnd2 = extractType(c[4]);
                if (!(PrimitiveTypeValue.INT.equals(oprnd1) && PrimitiveTypeValue.INT.equals(oprnd2))) {
                    throw new StaticTypeError("Type error: ", oprnd1, oprnd2, l);
                }
                c[0] = PrimitiveTypeValue.INT;
                return c; // using pure modifier, but modifying c
            } else {
                return null; // ignore this rule
            }
        }, "(%LET $1 typed (%LET $1 expr $2 $3 $4%) $0%)");
        // first type check and wrap each sub-expression with type
        transformer.addTransformer("(%LET @_ expr @_ @_ @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            if (c[3].equals(">") || c[3].equals("<") || c[3].equals(">=") || c[3].equals("<=") || c[3].equals("==") || c[3].equals("!=")) {
                c[2] = transformer.transform(c[2], E);
                c[4] = transformer.transform(c[4], E);
                Object oprnd1 = extractType(c[2]);
                Object oprnd2 = extractType(c[4]);
                if (!(PrimitiveTypeValue.INT.equals(oprnd1) && PrimitiveTypeValue.INT.equals(oprnd2))) {
                    throw new StaticTypeError("Type error: ", oprnd1, oprnd2, l);
                }
                c[0] = PrimitiveTypeValue.BOOL;
                return c; // using pure modifier, but modifying c
            } else {
                return null; // ignore this rule
            }
        }, "(%LET $1 typed (%LET $1 expr $2 $3 $4%) $0%)");

        // strip () from an expression
        transformer.addTransformer("(%LET @_ expr ( @_ )%)", (c, E) -> transformer.transform(c[2], E));
        transformer.addTransformer("(%LET @_ expr @_ %)", (c, E) -> transformer.transform(c[2], E));
        transformer.addTransformer("(%LET @_ prog @_ %)", (c, E) -> transformer.transform(c[2], E));
        transformer.addTransformer("(%LET @_ num @_%)", (c, E) -> {
            c[2] = PrimitiveTypeValue.INT;
        }, "(%LET $1 typed $0 $2%)");
        transformer.addTransformer("(%LET @_ iden @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            if (E == null) {
                throw new SemanticException(c[2]+" is not defined.", l);
            }
            Object ret = e(E).get(s(c[2], l));
            if (ret == null) {
                throw new SemanticException(c[2]+" is not defined.", l);
            }
            c[2] = ret;
        }, "(%LET $1 typed $0 $2%)");
        transformer.addTransformer("(%LET @_ expr let (%LET @_ iden @_%) : (%LET @_ type @_%) = @_ in @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            c[6] = transformer.transform(c[6], E);
            Object t = extractType(c[6]);
            if (c[5].equals("int")) {
                c[5] = PrimitiveTypeValue.INT;
                if (!t.equals(c[5])) {
                    throw new StaticTypeError("Type error: ", t, c[5], l);
                }
                c[7] = transformer.transform(c[7], Environment.extend(s(c[3],l), PrimitiveTypeValue.INT, e(E)));
            }
            if (c[5].equals("boolean")) {
                c[5] = BOOL;
                if (!t.equals(c[5])) {
                    throw new StaticTypeError("Type error: ", t, c[5], l);
                }
                c[7] = transformer.transform(c[7], Environment.extend(s(c[3],l), BOOL, e(E)));
            }
            c[0] = extractType(c[7]);
        }, "(%LET $1 typed (%LET $1 expr let (%LET $2 iden $3%) : $5 = $6 in $7%) $0%)");
        transformer.addTransformer("(%LET @_ expr if @_ then @_ else @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            c[2] = transformer.transform(c[2], E);
            if (!(BOOL.equals(extractType(c[2])))) {
                throw new StaticTypeError("Type error: ", BOOL, c[2], l);
            }
            c[3] = transformer.transform(c[3], E);
            c[4] = transformer.transform(c[4], E);
            Object retType = extractType(c[3]);
            if (!retType.equals(extractType(c[4]))) {
                throw new StaticTypeError("Type error: ", c[3], c[4], l);
            }
            c[0] = retType;
        }, "(%LET $1 typed (%LET $1 expr if $2 then $3 else $4%) $0%)");

        return transformer.transform(st, null);
    }


    @Override
    public UntypedTree parseString(String pgm) {
        GenericAntlrToUntypedTree p = new GenericAntlrToUntypedTree();
        return p.parseStringToUntypedTree("LET", grammarName, "prog", pgm);
    }

    @Override
    public UntypedTree parseFile(String fname) {
        GenericAntlrToUntypedTree p = new GenericAntlrToUntypedTree();
        return p.parseFileToUntypedTree("LET", grammarName, "prog", fname);
    }
}
