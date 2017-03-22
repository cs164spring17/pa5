package atrai.interpreters.COMPILEDLET;

import atrai.core.UntypedTree;
import atrai.interpreters.LET.LetInterpreter;
import atrai.interpreters.common.CodeTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Created by ksen on 3/7/17.
 */
public class LetCompilerTest {

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

    private String testCase(String name, String program) throws Exception {
        LetCompiler inp = new LetCompiler();
        inp.setName(name);
        UntypedTree st = inp.parseString(program);
        CodeTemplate ct = (CodeTemplate)inp.interpret( st);
        ct.writeCode();
        ct.compileCode();
        int res = ct.executeCode();
        return res+"\n";
    }


    @Test
    public void inter1() throws Exception {
        String res = testCase("Tmp", "2+3");
        assertEquals("5\n", res);
    }

    @Test
    public void inter2() throws Exception {
        String res = testCase("Tmp", "2+3 + 5");
        assertEquals("10\n", res);
    }

    @Test
    public void inter3() throws Exception {
        String res = testCase("Tmp", "2+ 3 + 5*2*2");
        assertEquals("25\n", res);
    }

    @Test
    public void inter4() throws Exception {
        String res = testCase("Tmp", "2+ (3 + 5)*2*2");
        assertEquals("34\n", res);
    }

    @Test
    public void inter5_0() throws Exception {
        String res = testCase("Tmp", "let x: int = 3 in x");
        assertEquals("3\n", res);
    }

    @Test
    public void inter5() throws Exception {
        String res = testCase("Tmp", "let x: int = 3 in let y: int = 4 in  x * y");
        assertEquals("12\n", res);
    }

    @Test
    public void inter6() throws Exception {
        String res = testCase("Tmp", "let x:int = 7  in let y:int = 2 in let y: int = let x: int = x - 1 in x - y in x - 8 - y");
        assertEquals("-5\n", res);

    }

    @Test
    public void inter7() throws Exception {
        String res = testCase("Tmp", "let x: int = 2 in let y: int = 3 in if (x+y)*2>10 then x*y else x-1");
        assertEquals("1\n", res);

    }

    @Test
    public void inter8() throws Exception {
        String res = testCase("Tmp", "let x: int = 2 in let y: int = 3 in if (x+y)*2 <= 10 then x*y else x-1");
        assertEquals("6\n", res);

    }

    @Test
    public void inter9() throws Exception {
        String res = testCase("Tmp", "let x: int = 2 in let y: int = 3 in (x+y)*2");
        assertEquals("10\n", res);

    }

    @Test
    public void inter10() throws Exception {
        String res = testCase("Tmp", "let x: int = 2 in x");
        assertEquals("2\n", res);

    }

    @Test
    public void inter11() throws Exception {
        String res = testCase("Tmp", "if 3>4 then 1 else 2");
        assertEquals("2\n", res);

    }

}

// run to test generated Java files
// for f in *.java; do javac $f; file=`basename $f .java`; echo $file; java $file; done
