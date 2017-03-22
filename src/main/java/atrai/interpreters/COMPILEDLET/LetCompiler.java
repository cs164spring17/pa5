package atrai.interpreters.COMPILEDLET;

import atrai.antlr.ANTLRTokenizer;
import atrai.antlr.GenericAntlrToUntypedTree;
import atrai.core.SimpleStringTokenizer;
import atrai.core.Transformer;
import atrai.core.UntypedTree;
import atrai.interpreters.TYPEDLET.TypedLetChecker;
import atrai.interpreters.common.CodeTemplate;
import atrai.interpreters.common.IndexEnvironment;
import atrai.interpreters.common.Interpreter;

public class LetCompiler extends Interpreter {
    private String grammarName = "atrai.antlr.TYPEDLET";
    private int labelCounter = 1;
    private CodeTemplate ct;
    private final ANTLRTokenizer patternLexer = new ANTLRTokenizer(grammarName);
    private final SimpleStringTokenizer templateLexer = new SimpleStringTokenizer();

    public void setName(String name) {
        this.ct = new CodeTemplate(name);
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

    @Override
    public Object interpret(UntypedTree st) {
        TypedLetChecker checker = new TypedLetChecker();
        st = (UntypedTree) checker.interpret(st);
        ct.addLambda("Main", compile(st));
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
        Transformer transformer = new Transformer(patternLexer, templateLexer);
        transformer.addTransformer("(%LET @_ expr @_ + @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.i=t1.i+a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr @_ - @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.i=t1.i-a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr @_ * @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.i=t1.i*a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr @_ / @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.i=t1.i/a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr @_ == @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.b=t1.i==a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr @_ != @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.b=t1.i!=a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr @_ >= @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.b=t1.i>=a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr @_ <= @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.b=t1.i<=a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr @_ < @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.b=t1.i<a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr @_ > @_%)", (c, E) -> {
            c[2] = transformer.transform(c[2], E);
            c[3] = transformer.transform(c[3], E);
        }, "(%$$2 (%stack[sp++].i=a0.i;%) $$3 (%t1.i=stack[sp-1].i;%) (%a0.b=t1.i>a0.i;%) (%sp--;%)%)");
        transformer.addTransformer("(%LET @_ expr ( @_ )%)", (c, E) -> transformer.transform(c[2], E));
        transformer.addTransformer("(%LET @_ expr @_ %)", (c, E) -> transformer.transform(c[2], E));
        transformer.addTransformer("(%LET @_ prog @_ %)", (c, E) -> transformer.transform(c[2], E));
        transformer.addTransformer("(%LET @_ num @_%)", "(%(%a0.i=$2;%)%)");
        transformer.addTransformer("(%LET @ typed (%LET @ iden @_%) @_%)", (c, E) -> {
            c[1] = ie(E).get(c[1]);
            if (!c[2].toString().equals("INT")) return null;
            return c;
        }, "(%(%a0.i = frame[fp-1-$1].i;%)%)");
        transformer.addTransformer("(%LET @ typed (%LET @ iden @_%) @_%)", (c, E) -> {
            c[1] = ie(E).get(c[1]);
            if (!c[2].toString().equals("BOOL")) return null;
            return c;
        }, "(%(%a0.b = frame[fp-1-$1].b;%)%)");
        transformer.addTransformer("(%LET @_ expr let (%LET @_ iden @_%) : @_ = @_ in @_%)", (c, E) -> {
            if (!c[4].toString().equals("INT")) return null;
            c[5] = transformer.transform(c[5], E);
            IndexEnvironment Ep = IndexEnvironment.extend(c[3], ie(E));
            c[6] = transformer.transform(c[6], Ep);
            return c;
        }, "(%$$5 (%frame[fp++]=new Value();%) (%frame[fp-1].i = a0.i;%) $$6 (%frame[--fp]=null;%)%)");
        transformer.addTransformer("(%LET @_ expr let (%LET @_ iden @_%) : @_ = @_ in @_%)", (c, E) -> {
            if (!c[4].toString().equals("BOOL")) return null;
            c[5] = transformer.transform(c[5], E);
            IndexEnvironment Ep = IndexEnvironment.extend(c[3], ie(E));
            c[6] = transformer.transform(c[6], Ep);
            return c;
        }, "(%$$5 (%frame[fp++]=new Value();%) (%frame[fp-1].b = a0.b;%) $$6 (%frame[--fp]=null;%)%)");
        transformer.addTransformer("(%LET @_ expr if @_ then @_ else @_%)", (c, E) -> {
            Object[] ret = new Object[6];
            ret[0] = transformer.transform(c[2], E);
            ret[1] = transformer.transform(c[3], E);
            ret[2] = transformer.transform(c[4], E);
            ret[3] = labelCounter++;
            ret[4] = labelCounter++;
            ret[5] = labelCounter++;
            return ret;
        }, "(%$$0 (%if(a0.b){label=$3;break;}%) (%case $4 :%) $$2 (%label=$5;break;%) (%case $3 :%) $$1 (%case $5 :%)%)");

        // THIS RULE is IMPORTANT as it gets rid of typed wrappers when typed wrappers are unnecessary
        // for some expressions wrappers are used, so this rule must be at the end
        transformer.addTransformer("(%LET @ typed @_ @%)", (c, E) -> transformer.transform(c[1], E));

        return transformer.transform(st, null);
    }

    private IndexEnvironment ie(Object e) {
        return (IndexEnvironment) e;
    }


}
