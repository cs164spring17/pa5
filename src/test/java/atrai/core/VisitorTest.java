package atrai.core;

import org.junit.Test;

import static atrai.interpreters.common.DynamicTypeChecker.i;
import static atrai.interpreters.common.DynamicTypeChecker.s2i;
import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link Visitor} class
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class VisitorTest {
    @Test
    public void exprCommute() throws Exception {
        String input = "(%(%3 + (% 5 * 4%)%) - 5%)";
        Visitor vistor = new Visitor(new SimpleStringTokenizer());
        vistor.addTransformerPost("(%@_ @_ @_%)", "(%$3 $2 $1%)");
        UntypedTree st = vistor.parseAndVisit(input);
        System.out.println(st);
        assertEquals("(%5 - (%(%4 * 5%) + 3%)%)", st.toString());
    }

    @Test
    public void exprEval() throws Exception {
        String input = "(%(%(%num 3%) + (%(%num 5%) * (%num 4%)%)%) - (%num 5%)%)";
        Visitor vistor = new Visitor(new SimpleStringTokenizer());
        vistor.addTransformerPost("(%@_ + @_%)", captures -> {
            captures[1] = i(captures[1], null) + i(captures[2], null);
            return captures;
        }, "$1");
        vistor.addTransformerPost("(%@_ * @_%)", captures -> {
            captures[1] = i(captures[1], null) * i(captures[2], null);
            return captures;
        }, "$1");
        vistor.addTransformerPost("(%@_ - @_%)", captures -> {
            captures[1] = i(captures[1], null) - i(captures[2], null);
            return captures;
        }, "$1");
        vistor.addTransformerPost("(%num @_%)", captures -> {
            captures[1] = s2i(captures[1], null);
            return captures;
        }, "$1");

        UntypedTree st = vistor.parseAndVisit(input);
        System.out.println(st);
        assertEquals("18", st.toString());
    }
}
