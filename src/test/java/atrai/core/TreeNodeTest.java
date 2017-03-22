package atrai.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link TreeNode} class
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class TreeNodeTest {

    @Test
    public void test1() throws Exception {
        String input = "(%hello world%)";
        commonTester(input);
    }

    private void commonTester(String input) {
        Lexer lexer = new SimpleStringTokenizer();
        UntypedTree t = UntypedTree.parse(input, lexer);
        System.out.println(t);
        assertEquals(input, t.toString());
    }

    @Test
    public void test2() throws Exception {
        String input = "(%hello (%nested world%)%)";
        commonTester(input);
    }

    @Test
    public void test3() throws Exception {
        String input = "(%hello (%nested (%more nested world%) world%) world (%nested other%)%)";
        commonTester(input);
    }

    @Test
    public void test3_1() throws Exception {
        String input = "(%x``x%)";
        commonTester(input);
    }

    @Test
    public void test4() throws Exception {
        String input = "(%hello`%) (%nested (%more nested``x world%) world%) world (%neste`(%d other%)%)";
        commonTester(input);
    }

    @Test
    public void test5() throws Exception {
        String input = "(%%)";
        commonTester(input);
    }

    @Test
    public void testWC1() throws Exception {
        String input = "(%@ nested @%)";
        commonTesterWC(input);
    }

    private void commonTesterWC(String input) {
        commonTesterWC(input, null);
    }

    private void commonTesterWC(String input, String expected) {
        Lexer lexer = new SimpleStringTokenizer();
        Pattern t = Pattern.parse(input, lexer);
        System.out.println(t);
        if (expected != null)
            assertEquals(expected, t.toString());
        else
            assertEquals(input, t.toString());

    }

    @Test
    public void testWC2() throws Exception {
        String input = "(%@ (%@_ @%) @%)";
        commonTesterWC(input);
    }

    @Test
    public void testWC22() throws Exception {
        String input = "(%$1 (%$2_ $3_%) $4%)";
        commonTesterWC(input, "(%`$1 (%`$2_ `$3_%) `$4%)");
    }

    @Test
    public void testWC3() throws Exception {
        String input = "(%hello (%nested (%@ nested @%) world%) world (%@ other%)%)";
        commonTesterWC(input);
    }

    @Test
    public void testWC4() throws Exception {
        String input = "(%hello`@`%) (%nested (%@ nes`@ted``x @%) @%) world (%neste`(% @ other%)%)";
        commonTesterWC(input);
    }

    @Test
    public void testWC4_2() throws Exception {
        String input = "(%hello`@`%)%)";
        commonTesterWC(input);
    }

    @Test
    public void testWC5() throws Exception {
        String input = "(%%)";
        commonTesterWC(input);
    }

    @Test
    public void testRP1() throws Exception {
        String input = "(%@ nested @%)";
        commonTesterRP(input, "(%`@ nested `@%)");
    }

    private void commonTesterRP(String input, String expected) {
        Lexer lexer = new SimpleStringTokenizer();
        Template t = Template.parse(input, lexer);
        System.out.println(t);
        assertEquals(expected, t.toString());
    }

    @Test
    public void testRP2() throws Exception {
        String input = "(%@ (%@ @%) @%)";
        commonTesterRP(input, "(%`@ (%`@ `@%) `@%)");
    }

    @Test
    public void testRP3() throws Exception {
        String input = "(%hello (%nested (%$1 nested $5%) world%) world (%$7 other%)%)";
        commonTesterRP(input);
    }

    private void commonTesterRP(String input) {
        Lexer lexer = new SimpleStringTokenizer();
        Template t = Template.parse(input, lexer);
        System.out.println(t);
        assertEquals(input, t.toString());
    }

    @Test
    public void testRP4_1() throws Exception {
        String input = "(%hello@`%)%)";
        commonTesterRP(input, "(%hello`@`%)%)");
    }

    @Test
    public void testRP4() throws Exception {
        String input = "(%hello@`%) (%nested (%$4 nes@ted``x @%) @%) world (%neste`(% @ other%)%)";
        commonTesterRP(input, "(%hello`@`%) (%nested (%$4 nes`@ted``x `@%) `@%) world (%neste`(% `@ other%)%)");
    }

    @Test
    public void testRP5() throws Exception {
        String input = "(%%)";
        commonTesterRP(input);
    }

    @Test
    public void testRP12() throws Exception {
        String input = "(%$1 nested $2%)";
        commonTesterRP(input);
    }

    @Test
    public void testRP22() throws Exception {
        String input = "(%$1 (%$1 $1%) $5%)";
        commonTesterRP(input);
    }

    @Test
    public void testRP21() throws Exception {
        String input = "(%$1 (%$$1 $1%) $5%)";
        commonTesterRP(input);
    }

    @Test
    public void testRP32() throws Exception {
        String input = "(%hello (%nested (%$1 nested $5%) world%) world (%$7 other%)%)";
        commonTesterRP(input);
    }

    @Test
    public void testRP4_12() throws Exception {
        String input = "(%hello`$3`%)%)";
        commonTesterRP(input);
    }

    @Test
    public void testRP42() throws Exception {
        String input = "(%hello`$2`%) (%nested (%$4 nes`$ted``x @%) @%) world (%neste`(% @ other%)%)";
        commonTesterRP(input, "(%hello`$2`%) (%nested (%$4 nes`$ted``x `@%) `@%) world (%neste`(% `@ other%)%)");
    }

    @Test
    public void testPrint1() throws Exception {
        String input = "(%hello`$3`%)`$`$1 `@_ `@ `@*_%)";
        UntypedTree t = UntypedTree.parse(input, new SimpleStringTokenizer());
        assertEquals(input, t.toString());
    }

    @Test
    public void testPrint2() throws Exception {
        String input = "(%hello @_ @*_%)";
        UntypedTree t = UntypedTree.parse(input, new SimpleStringTokenizer());
        assertEquals("(%hello `@_ `@*_%)", t.toString());
    }

    @Test
    public void testPrint3() throws Exception {
        String input = "(%hello $1 $$1 $*1 %)";
        UntypedTree t = UntypedTree.parse(input, new SimpleStringTokenizer());
        assertEquals("(%hello `$1 `$`$1 `$*1%)", t.toString());
    }
}
