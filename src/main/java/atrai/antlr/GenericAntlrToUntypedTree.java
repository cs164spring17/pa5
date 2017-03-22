package atrai.antlr;

import atrai.core.UntypedTree;
import atrai.core.TreeBuilder;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.antlr.v4.runtime.tree.Tree;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Given an ANTLR lexer and parser, creates a {@link UntypedTree} from the parse tree provided by ANTLR.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */

class ThrowingErrorListener extends BaseErrorListener {

    public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
            throws ParseCancellationException {
        System.err.println("Syntax Error: at line " + line + ":" + charPositionInLine + " " + msg);
        throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
    }
}

public class GenericAntlrToUntypedTree {
    private List<String> ruleNames = null;
    private List<String> tokenNames = null;
    private int counter;
    private CommonTokenStream tokens;

    /**
     * Usage: program-name prefix grammar startSymbol file
     *        prints the untyped tree obtained by parsing the file with the ANTLR grammar
     *
     * @param args 1st arg is the user-defined language name to be added as the first token of each sub untyped tree
     *             2nd arg is the full-qualified ANTLR grammar name.  Used to parse the file specified as arg 4.
     *             3rd arg is the name of the start symbol in the grammar
     *             4th arg is the name of the file to be parsed
     *
     */
    public static void main(String args[]) {
        GenericAntlrToUntypedTree p = new GenericAntlrToUntypedTree();
        System.out.println(p.parseFileToUntypedTree(args[0], args[1], args[2], args[3]).toIndentedString());
    }

    private void setRuleNames(Parser recog) {
        String[] ruleNames = recog != null ? recog.getRuleNames() : null;
        String[] tokenNames = recog != null ? recog.getTokenNames() : null;
        this.ruleNames = ruleNames != null ? Arrays.asList(ruleNames) : null;
        this.tokenNames = tokenNames != null ? Arrays.asList(tokenNames) : null;
    }

    private UntypedTree parseToUntypedTree(String prefix, String grammarName, String startSymbol, ANTLRInputStream inputStream)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Class classDefinition;
        Class[] type;
        Object[] obj;

        type = new Class[]{CharStream.class};
        classDefinition = Class.forName(grammarName + "Lexer");
        Constructor cons = classDefinition.getConstructor(type);
        obj = new Object[]{inputStream};
        Lexer lexer = (Lexer) cons.newInstance(obj);
        lexer.removeErrorListeners();
        lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
        tokens = new CommonTokenStream(lexer);

        type = new Class[]{TokenStream.class};
        classDefinition = Class.forName(grammarName + "Parser");
        cons = classDefinition.getConstructor(type);
        obj = new Object[]{tokens};
        Parser parser = (Parser) cons.newInstance(obj);
        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);

        Method method = parser.getClass().getMethod(startSymbol);
        ParserRuleContext t = (ParserRuleContext) method.invoke(parser);
        parser.setBuildParseTree(false);
        setRuleNames(parser);

        TreeBuilder builder = new TreeBuilder();
        getUntypedTree(prefix, t, builder);
        return new UntypedTree(builder.getTree(), builder.getLocations());
    }

    /**
     * Parses the contents of the file {@code fname} to an untyped tree using the ANTLR grammar {@code grammarName}.
     *
     * @param prefix a user-defined token to be added at the beginning of each sub untyped tree
     * @param grammarName the name of the ANTLR grammar (with fully qualified package name) to be used to parse the file
     * @param startSymbol the name of the start symbol in the grammar
     * @param fname name of the file to be parsed
     * @return
     */
    public UntypedTree parseFileToUntypedTree(String prefix, String grammarName, String startSymbol, String fname) {
        try {
            counter = 0;
            return parseToUntypedTree(prefix, grammarName, startSymbol, new ANTLRFileStream(fname));
        } catch (Exception e) {
            throw new RuntimeException("Parser exception:", e);
        }
    }

    /**
     * Parses the {@code sourceString} to an untyped tree using the ANTLR grammar {@code grammarName}.
     *
     * @param prefix a user-defined token to be added at the beginning of each sub untyped tree
     * @param grammarName the name of the ANTLR grammar (with fully qualified package name) to be used to parse the file
     * @param startSymbol the name of the start symbol in the grammar
     * @param sourceString the string to be parsed
     * @return
     */
    public UntypedTree parseStringToUntypedTree(String prefix, String grammarName, String startSymbol, String sourceString) {
        try {
            counter = 0;
            return parseToUntypedTree(prefix, grammarName, startSymbol, new ANTLRInputStream(sourceString));
        } catch (Exception e) {
            throw new RuntimeException("Parser exception:", e);
        }
    }

    private String getRuleName(Tree t) {
        int ruleIndex = ((RuleNode) t).getRuleContext().getRuleIndex();
        return ruleNames.get(ruleIndex);
    }

    private String getTokenName(Tree t) {
        return tokenNames.get(((TerminalNodeImpl) t).getSymbol().getType());
    }

    private Location getLocation(RuleContext t) {
        Interval interval = t.getSourceInterval();
        Token startToken = tokens.get(interval.a);
        Token endToken = tokens.get(interval.b);
        return new Location(startToken.getLine(), startToken.getCharPositionInLine(), endToken.getLine(), endToken.getCharPositionInLine() + endToken.getText().length());
    }

    private void getUntypedTree(String prefix, RuleContext t, TreeBuilder builder) {
        int n = t.getChildCount();
        if (n == 0) {
            return;
        }

        builder.beginSubTree();
        builder.addLeaf(prefix);
        builder.addLocation(counter, getLocation(t));
        builder.addLeaf(counter++);
        builder.addLeaf(getRuleName(t));
        for (int i = 0; i < n; i++) {
            ParseTree tree = t.getChild(i);
            if (tree instanceof TerminalNodeImpl) {
                String s = tree.getText();
                builder.addLeaf(s);
            } else {
                getUntypedTree(prefix, (RuleContext) tree, builder);
            }
        }
        builder.endSubTree();
    }

}
