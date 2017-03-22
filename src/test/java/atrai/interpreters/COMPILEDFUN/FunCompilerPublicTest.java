package atrai.interpreters.COMPILEDFUN;

import atrai.core.UntypedTree;
import atrai.interpreters.common.CodeTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by ksen on 3/13/17.
 */
public class FunCompilerPublicTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(stdout);
        System.setErr(stderr);
        System.out.print(outContent.toString());
        System.err.print(errContent.toString());
    }

    @Test
    public void testArithmetic() throws Exception {
        testCase("1+2", "3");
        testCase("1-2", "-1");
        testCase("1*2", "2");
        testCase("2/1", "2");
    }

    private void testCase(String program, String expected) throws Exception {
        outContent.reset();
        FunCompiler inp = new FunCompiler();
        inp.setName("Tmp");
        program = "print(" + program + ")";
        UntypedTree st = inp.parseString(program);
        CodeTemplate ct = (CodeTemplate) inp.interpret(st);
        ct.writeCode();
        ct.compileCode();
        ct.executeCode();
        final String actual = outContent.toString().trim();
        assertEquals(expected, actual);
    }

    @Test
    public void testComparison() throws Exception {
        testCase("1 < 2", "true");
        testCase("1 > 2", "false");
        testCase("1 <= 2", "true");
        testCase("1 >= 2", "false");
        testCase("1 == 2", "false");
        testCase("1 != 2", "true");
    }

    @Test
    public void testVariableDeclaration() throws Exception {
        testCase("let x : int = 3 in x", "3");
        testCase("let x : string = \"foo\" in x", "foo");
        testCase("let x : null = null in x", "null");
        testCase("let x : boolean = 10==10 in x", "true");
    }

    @Test
    public void testVariableAssignment() throws Exception {
        testCase("let x : int = 3 in x = 4", "4");
        testCase("let x : boolean = 0==0 in x = 50==40", "false");
        testCase("let x : string = \"foo\" in x = \"bar\"", "bar");
        testCase("let x : null = null in x = null", "null");
    }

    @Test
    public void testHigherOrderFunction() throws Exception {
        testCase("(let x : (int)->int = fun(y:int):int = y + 1 in x)(6)", "7");
        testCase("let x : (int)->int = fun(y:int):int = y + 1 in (x = fun(y:int):int=y+5)(5)", "10");
    }

    @Test
    public void testConcatenation() throws Exception {
        testCase("\"foo\" + 1", "foo1");
        testCase("1 + \"foo\"", "1foo");
        testCase("\"foo\" + \"bar\"", "foobar");
    }

    @Test
    public void testIf() throws Exception {
        testCase("if 0 == 0 then 3 else 5", "3");
        testCase("if 0 != 0 then 3 else 5", "5");
    }

    @Test
    public void testWhile() throws Exception {
        testCase("while 0 == 1 do \"something\"", "null");
    }

    @Test
    public void testSequence() throws Exception {
        testCase("{ 0; null; \"string\" }", "string");
    }

    @Test
    public void testNullaryFunction() throws Exception {
        testCase("(fun () : int = 3)()", "3");
    }

    @Test
    public void testUnaryFunction() throws Exception {
        testCase("(fun (x:int) : int = x+3)(3)", "6");
        testCase("(fun (x:string) : string = x + 1)(\"foo\")", "foo1");
        testCase("(fun (x:int) : ()->int = fun():int = x+1)(1)()", "2");
        testCase("(fun (x:int) : (int)->int = fun(y:int):int = x+y)(23)(37)", "60");
        testCase("(fun (f:(int)->int) : int = f(1))(fun(x:int):int=x+1)", "2");
        testCase("(fun (b:boolean):int = if b then 1 else 0)(0==0)", "1");
        testCase("(fun (n:null):int = 101)(null)", "101");
    }

    @Test
    public void testTypeSpec() throws Exception {
        testCase("let f:(int)->int = fun(x:int):int=x in 0", "0");
    }

    @Test
    public void testNull() throws Exception {
        testCase("null", "null");
    }

    @Test
    public void testPrintLambda() throws Exception {
        testCase("fun ():null = null", "Lambda");
    }

    @Test
    public void testFactorialFromSpec() throws Exception {
        testCase("let fact : (int) -> int = fun(x : int) : int =\n" +
                        "  let prod : int = 1\n" +
                        "   in { while x > 1 do {\n" +
                        "          prod = prod * x;\n" +
                        "          x = x - 1\n" +
                        "        }; prod }\n" +
                        "in fact(4)",
                "24");
    }

}
