package atrai.antlr;

import atrai.core.UntypedTree;
import atrai.core.Visitor;
import org.junit.Test;

import java.util.HashMap;

import static atrai.interpreters.common.DynamicTypeChecker.*;

/**
 * Tests for the Expr2 language interpreter
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class Expr2Interpreter {

    @Test
    public void exprEval() throws Exception {
        final HashMap<String, Integer> state = new HashMap<>();
        String grammarName = "atrai.antlr.Expr2";
        GenericAntlrToUntypedTree g = new GenericAntlrToUntypedTree();
        UntypedTree st = g.parseFileToUntypedTree("Expr", grammarName, "prog", "src/main/antlr4/sample.expr");
        System.out.println(st);

        Visitor vistor = new Visitor(new ANTLRTokenizer(grammarName));
        vistor.addTransformerPost("(%Expr @_ @_ (%Expr @_ num @_%) + (%Expr @_ num @_%)%)", captures -> {
            Location l = st.getLocationFromID((Integer) captures[1]);
            captures[4] = i(captures[4], l) + i(captures[6], l);
            return captures;
        }, "(%Expr $1 num $4%)");
        vistor.addTransformerPost("(%Expr @_ @_ (%Expr @_ num @_%) * (%Expr @_ num @_%)%)", captures -> {
            Location l = st.getLocationFromID((Integer) captures[1]);
            captures[4] = i(captures[4], l) * i(captures[6], l);
            return captures;
        }, "(%Expr $1 num $4%)");
        vistor.addTransformerPost("(%Expr @_ @_ (%Expr @_ num @_%) - (%Expr @_ num @_%)%)", captures -> {
            Location l = st.getLocationFromID((Integer) captures[1]);
            captures[4] = i(captures[4], l) - i(captures[6], l);
            return captures;
        }, "(%Expr $1 num $4%)");

        vistor.addTransformerPre("(%Expr @_ num @_%)", captures -> {
            Location l = st.getLocationFromID((Integer) captures[1]);
            captures[2] = s2i(captures[2], l);
            return captures;
        }, "(%Expr $1 num $2%)");

        vistor.addTransformerPre("(%Expr @_ stat (%Expr @_ iden @_%) = @_ @_%)", "(%Expr $1 stat (%Expr $2 lhs $3%) = $4 $5%)");

        vistor.addTransformerPost("(%Expr @_ stat (%Expr @_ lhs @_%) = (%Expr @_ num @_%) @_%)", captures -> {
            Location l = st.getLocationFromID((Integer) captures[1]);
            state.put(s(captures[3], l), i(captures[5], l));
            return captures;
        }, "(%Expr $1 stat $6%)");
        vistor.addObserverPost("(%Expr @_ stat (%Expr @_ num @_%) @_%)", captures -> {
            System.out.println(captures[3]);
        });
        vistor.addTransformerPost("(%Expr @_ iden @_%)", captures -> {
            Location l = st.getLocationFromID((Integer) captures[1]);
            Integer res = state.get(s(captures[2], l));
            if (res != null) {
                captures[2] = res;
                return captures;
            } else {
                return null;
            }
        }, "(%Expr $1 num $2%)");

        vistor.addTransformerPost("(%Expr @_ @_ ( (%Expr @_ num @_%) )%)", "(%Expr $1 num $4%)");

        UntypedTree ret = vistor.visit(st);
        System.out.println(ret);
    }
}
