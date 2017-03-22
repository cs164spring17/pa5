package atrai.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Tests for the {@link Pattern} class.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class PatternTest {

    @Test
    public void test1() throws Exception {
        String pattern = "(%hello world %)";
        String source = "(%hello world%)";
        Object[] captures = match(pattern, source);
        assertEquals(1, captures.length);
    }

    private Object[] match(String pattern, String source) {
        Lexer lexer = new SimpleStringTokenizer();
        UntypedTree s = UntypedTree.parse(source, lexer);
        Pattern p = Pattern.parse(pattern, lexer);
        return p.match(s);
    }

    @Test
    public void test2() throws Exception {
        String pattern = "(% @_ world %)";
        String source = "(%hello world%)";
        Object[] captures = match(pattern, source);
        assertEquals(2, captures.length);
        assertEquals("hello", captures[1].toString());
    }

    @Test
    public void test3() throws Exception {
        String pattern = "(% @_ @_ %)";
        String source = "(%hello world%)";
        Object[] captures = match(pattern, source);
        assertEquals(3, captures.length);
        assertEquals("hello", captures[1].toString());
        assertEquals("world", captures[2].toString());
    }

    @Test
    public void test4() throws Exception {
        String pattern = "(% if (@_) (%{ @_ }%) else @_ %)";
        String source = "(%if ( (% x > 0%) ) (% { (%x = (%- x%) %) }%) else (% (% x %) = (%x + 1%)%)%)";
        Object[] captures = match(pattern, source);
        assertEquals(4, captures.length);
        assertEquals("(%x > 0%)", captures[1].toString());
        assertEquals("(%x = (%- x%)%)", captures[2].toString());
        assertEquals("(%(%x%) = (%x + 1%)%)", captures[3].toString());
    }

    @Test
    public void test5() throws Exception {
        String pattern = "(% `@ @_ %)";
        String source = "(%`@ world%)";
        Object[] captures = match(pattern, source);
        assertEquals(2, captures.length);
        assertEquals("world", captures[1].toString());
    }

    @Test
    public void test6() throws Exception {
        String pattern = "(% `@ @ @_ @*_ %)";
        String source = "(%`@ 1 2 3 world 4%)";
        Object[] captures = match(pattern, source);
        assertEquals(3, captures.length);
        assertEquals("2", captures[1].toString());
        Object[] c = (Object[])captures[2];
        assertEquals("3", c[0].toString());
        assertEquals("world", c[1].toString());
        assertEquals("4", c[2].toString());
    }

    @Test
    public void test7() throws Exception {
        String pattern = "(% `@ @ @_ @*_ %)";
        String source = "(%`@ 1 2%)";
        Object[] captures = match(pattern, source);
        assertEquals(3, captures.length);
        assertEquals("2", captures[1].toString());
        Object[] c = (Object[])captures[2];
        assertEquals(c.length, 0);
    }

    @Test
    public void test8() throws Exception {
        String pattern = "(% `@ @ @_ @*_ %)";
        String source = "(%`@ 1 2 3%)";
        Object[] captures = match(pattern, source);
        assertEquals(3, captures.length);
        assertEquals("2", captures[1].toString());
        Object[] c = (Object[])captures[2];
        assertEquals(c.length, 1);
        assertEquals("3", c[0].toString());
    }

    @Test (expected = RuntimeException.class)
    public void test9() throws Exception {
        String pattern = "(% `@ @ @_ @*_ %)";
        String source = "(%`@ 1 %)";
        Object[] captures = match(pattern, source);
        assertEquals(3, captures.length);
        assertEquals("2", captures[1].toString());
        Object[] c = (Object[])captures[2];
        assertEquals(c.length, 1);
        assertEquals("3", c[0].toString());
    }

    @Test
    public void test10() throws Exception {
        String pattern = "(%h e (% `@ @ @_ @*_ %) o %)";
        String source = "(%h e (%`@ 1 2 3 world 4%) o %)";
        Object[] captures = match(pattern, source);
        assertEquals(3, captures.length);
        assertEquals("2", captures[1].toString());
        Object[] c = (Object[])captures[2];
        assertEquals("3", c[0].toString());
        assertEquals("world", c[1].toString());
        assertEquals("4", c[2].toString());
    }

    @Test (expected = RuntimeException.class)
    public void test11() throws Exception {
        String pattern = "(% `@ @ @*_ @_ @*_%)";
        Lexer lexer = new SimpleStringTokenizer();
        Pattern.parse(pattern, lexer);
    }

    @Test
    public void test12() throws Exception {
        String pattern = "(%`@ @ @*_ @_%)";
        Lexer lexer = new SimpleStringTokenizer();
        Pattern p = Pattern.parse(pattern, lexer);
        assertEquals("(%`@ @ @*_ @_%)", p.toString());
    }

    @Test
    public void test13() throws Exception {
        String pattern = "(% @_ worlds %)";
        String source = "(%hello world%)";
        Object[] captures = match(pattern, source);
        assertEquals(null, captures);
    }

    @Test
    public void test14() throws Exception {
        String pattern = "hello";
        String source = "hello";
        Object[] captures = match(pattern, source);
        assertEquals(1, captures.length);
    }

    @Test
    public void test15() throws Exception {
        String pattern = "hello";
        String source = "hell";
        Object[] captures = match(pattern, source);
        assertEquals(null, captures);
    }

    @Test
    public void test16() throws Exception {
        String pattern = "(% @_ @_ %)";
        String source = "(%hello world%)";
        Lexer lexer = new SimpleStringTokenizer();
        UntypedTree s = UntypedTree.parse(source, lexer);
        Pattern p = Pattern.parse(pattern, lexer);
        Object[] captures = p.match(s);;
        assertSame(captures, p.getMatches());
        assertSame(captures[0], p.getMatch(0));
        assertEquals(captures[1], p.getMatch(1));
        assertEquals(captures[2], p.getMatch(2));
    }


    @Test
    public void test17() throws Exception {
        String pattern = "(%@ @_ @*_%)";
        Pattern p = Pattern.parse(pattern, new SimpleStringTokenizer());
        assertEquals(pattern, p.toString());
    }

    @Test
    public void test18() throws Exception {
        String pattern = "(%@ @_ @*_%)";
        Pattern p = Pattern.parse(pattern, new SimpleStringTokenizer());
        assertEquals("(%@ @_ @*_\n%)\n", p.toIndentedString());
    }


    @Test
    public void test19() throws Exception {
        String pattern = "@";
        Pattern p = Pattern.parse(pattern, new SimpleStringTokenizer());
        assertEquals(pattern, p.toString());
    }

    @Test
    public void test20() throws Exception {
        String pattern = "@";
        Pattern p = Pattern.parse(pattern, new SimpleStringTokenizer());
        assertEquals("@\n", p.toIndentedString());
    }

    @Test
    public void test21() throws Exception {
        String pattern = "@*_";
        Pattern p = Pattern.parse(pattern, new SimpleStringTokenizer());
        assertEquals(pattern, p.toString());
    }

    @Test
    public void test22() throws Exception {
        String pattern = "@*_";
        Pattern p = Pattern.parse(pattern, new SimpleStringTokenizer());
        assertEquals("@*_\n", p.toIndentedString());
    }

    @Test
    public void test23() throws Exception {
        String pattern = "@_";
        Pattern p = Pattern.parse(pattern, new SimpleStringTokenizer());
        assertEquals(pattern, p.toString());
    }

    @Test
    public void test24() throws Exception {
        String pattern = "@_";
        Pattern p = Pattern.parse(pattern, new SimpleStringTokenizer());
        assertEquals("@_\n", p.toIndentedString());
    }

}
