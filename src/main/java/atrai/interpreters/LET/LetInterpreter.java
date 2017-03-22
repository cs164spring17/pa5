package atrai.interpreters.LET;

import atrai.antlr.ANTLRTokenizer;
import atrai.antlr.GenericAntlrToUntypedTree;
import atrai.antlr.Location;
import atrai.core.UntypedTree;
import atrai.core.Transformer;
import atrai.interpreters.common.Environment;
import atrai.interpreters.common.Interpreter;
import atrai.interpreters.common.SemanticException;

import static atrai.interpreters.common.DynamicTypeChecker.*;

/**
 * Parser for the LET language
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class LetInterpreter extends Interpreter {
    private String grammarName = "atrai.antlr.LET";

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
        ANTLRTokenizer tokenizer = new ANTLRTokenizer(grammarName);
        Transformer transformer = new Transformer(tokenizer);
        transformer.addTransformer("(%LET @_ expr @_ + @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) + i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr @_ - @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) - i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr @_ * @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) * i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr @_ / @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) / i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr @_ == @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) == i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr @_ != @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) != i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr @_ >= @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) >= i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr @_ <= @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) <= i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr @_ < @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) < i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr @_ > @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return i(transformer.transform(c[2], E), l) > i(transformer.transform(c[3], E), l);
        });
        transformer.addTransformer("(%LET @_ expr ( @_ )%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return transformer.transform(c[2], E);
        });
        transformer.addTransformer("(%LET @_ expr @_ %)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return transformer.transform(c[2], E);
        });
        transformer.addTransformer("(%LET @_ prog @_ %)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return transformer.transform(c[2], E);
        });
        transformer.addTransformer("(%LET @_ num @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            return s2i(c[2], l);
        });
        transformer.addTransformer("(%LET @_ iden @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            if (E == null) {
                throw new SemanticException(c[2]+" is not defined.", l);
            }
            Object ret = e(E).get(s(c[2], l));
            if (ret == null) {
                throw new SemanticException(c[2]+" is not defined.", l);
            }
            return ret;
        });
        transformer.addTransformer("(%LET @_ expr let (%LET @_ iden @_%) = @_ in @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            Environment Ep = Environment.extend(s(c[3], l), transformer.transform(c[4], E), e(E));
            return transformer.transform(c[5], Ep);
        });
        transformer.addTransformer("(%LET @_ expr if @_ then @_ else @_%)", (c, E) -> {
            Location l = st.getLocationFromID((Integer) c[1]);
            if (b(transformer.transform(c[2], E), l)) {
                return transformer.transform(c[3], E);
            } else {
                return transformer.transform(c[4], E);
            }
        });
        return transformer.transform(st, null).getRoot();
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
