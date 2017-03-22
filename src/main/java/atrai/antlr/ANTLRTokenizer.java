package atrai.antlr;

import atrai.core.Lexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;

import java.lang.reflect.Constructor;

/**
 * A general {@link Lexer} wrapper for ANTLR generated lexers. The lexer can be used to tokenize strings inside untyped trees,
 * patterns, and templates.  Loads such an ANTLR grammar by reflection.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class ANTLRTokenizer implements Lexer {
    private final String grammarName;
    private org.antlr.v4.runtime.Lexer lexer;

    /**
     * Loads the ANTLR lexer for the grammar with name {@code grammarName}.
     * The lexer is loaded reflectively.
     *
     * @param grammarName
     */
    public ANTLRTokenizer(String grammarName) {
        this.grammarName = grammarName;
    }

    @Override
    public void setStream(String sub) {
        try {
            Class classDefinition;
            Class[] type;
            Object[] obj;

            type = new Class[]{CharStream.class};
            classDefinition = Class.forName(grammarName + "Lexer");
            Constructor cons = classDefinition.getConstructor(type);
            obj = new Object[]{new ANTLRInputStream(sub)};
            lexer = (org.antlr.v4.runtime.Lexer) cons.newInstance(obj);
            lexer.removeErrorListeners();
            lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        } catch (Exception e) {
            System.err.println("parser exception: " + e);
            e.printStackTrace();   // so we can get stack trace
        }
    }

    @Override
    public String getNextToken() {
        org.antlr.v4.runtime.Token token = lexer.nextToken();
        if (token.getType() == org.antlr.v4.runtime.Token.EOF) {
            return null;
        } else {
            return token.getText();
        }
    }
}
