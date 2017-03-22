package atrai.interpreters.COMPILEDFUN;

import atrai.antlr.ANTLRTokenizer;
import atrai.antlr.GenericAntlrToUntypedTree;
import atrai.core.*;
import atrai.interpreters.TYPEDFUN.staff.TypedFunChecker;
import atrai.interpreters.common.*;

import java.util.ArrayList;
import java.util.Collections;

import static atrai.interpreters.common.DynamicTypeChecker.n;

public class FunCompiler extends Interpreter {
    private String grammarName = "atrai.antlr.TYPEDFUN";
    private ANTLRTokenizer patternLexer = new ANTLRTokenizer(grammarName);
    private SimpleStringTokenizer templateLexer = new SimpleStringTokenizer();
    private Pattern idenExtractor = Pattern.parse("(%FUN @ decl (%FUN @ iden @_%) : @ = @%)", templateLexer);
    private Pattern childrenExtractor = Pattern.parse("(%@*_%)", templateLexer);
    private Pattern typeExtractor = Pattern.parse("(%FUN @ typed @ @_%)", patternLexer);
    private Object pop = UntypedTree.parse("(%frame[--fp]=null;%)", templateLexer).getRoot();
    private Template prepend = Template.parse("(%$$1 $$2%)", templateLexer);
    private int labelCounter = 1;
    private int funCounter = 1;
    private CodeTemplate ct;

    public void setName(String name) {
        ct = new CodeTemplate(name);
    }

    private Object extractType(Object source) {
        Object[] tmp = typeExtractor.match(source);
        return tmp[1];
    }

    @Override
    public UntypedTree parseString(String pgm) {
        GenericAntlrToUntypedTree p = new GenericAntlrToUntypedTree();
        return p.parseStringToUntypedTree("FUN", grammarName, "prog", pgm);
    }

    @Override
    public UntypedTree parseFile(String fname) {
        GenericAntlrToUntypedTree p = new GenericAntlrToUntypedTree();
        return p.parseFileToUntypedTree("FUN", grammarName, "prog", fname);
    }

    @Override
    public Object interpret(UntypedTree st) {
        TypedFunChecker checker = new TypedFunChecker();
        UntypedTree typeCheckedTree = (UntypedTree) checker.interpret(st);
        ct.addLambda("Main", compile(typeCheckedTree));
        return ct;
    }

    /**
     * Compiles a type-annotated tree into a tree containing only low-level Java (LLJ)
     * instructions
     *
     * @param st Type-annotated tree for FUN
     * @return Tree containing LLJ instructions
     */
    private UntypedTree compile(UntypedTree st) {
        // Your code goes here.
        return null;
    }

}
