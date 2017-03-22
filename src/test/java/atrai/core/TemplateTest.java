package atrai.core;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ksen on 2/17/17.
 */
public class TemplateTest {

    private Object replace(String pattern, String template, String source) {
        Lexer lexer = new SimpleStringTokenizer();
        UntypedTree s = UntypedTree.parse(source, lexer);
        Pattern p = Pattern.parse(pattern, lexer);
        Template t = Template.parse(template, lexer);
        return t.replace(p.match(s));

    }

    @Test
    public void test1() throws Exception {
        String pattern = "@_";
        String source = "(%hello world%)";
        String template = "(% begin $$1 end %)";
        Object t = replace(pattern, template, source);
        assertEquals("(%begin hello world end%)", t.toString());
        System.out.println(t);
    }

    @Test
    public void test2() throws Exception {
        String pattern = "(% @_ @_ %)";
        String source = "(%hello (%world X%)%)";
        String template = "(% begin $1 $2 end %)";
        Object t = replace(pattern, template, source);
        System.out.println(t);
        assertEquals("(%begin hello (%world X%) end%)", t.toString());
    }

    @Test
    public void test3() throws Exception {
        String pattern = "(% @_ @_ %)";
        String source = "(%hello (%world X%)%)";
        String template = "(% begin `$1 $2 `$3_ end %)";
        Object t = replace(pattern, template, source);
        System.out.println(t);
        assertEquals("(%begin `$1 (%world X%) `$3_ end%)", t.toString());
    }

    @Test
    public void test4() throws Exception {
        String pattern = "(% @_ (%world @*_%) %)";
        String source = "(%hello (%world X Y Z%)%)";
        String template = "(% begin `$1 $1 $*2 `$3_ end %)";
        Object t = replace(pattern, template, source);
        System.out.println(t);
        assertEquals("(%begin `$1 hello X Y Z `$3_ end%)", t.toString());
    }

    @Test
    public void test5() throws Exception {
        String template = "(%begin `$1 $1 $*2 `$3_ `$*4 end%)";
        Template t = Template.parse(template, new SimpleStringTokenizer());
        assertEquals("(%begin `$1 $1 $*2 `$3_ `$*4 end%)", t.toString());
    }


    @Test (expected = RuntimeException.class)
    public void test6() throws Exception {
        String pattern = "(% @_ (%world @*_%) %)";
        String source = "(%hello (%world X Y Z%)%)";
        String template = "(% begin `$1 $1 $*1 `$3_ end %)";
        Object t = replace(pattern, template, source);
        System.out.println(t);
        assertEquals("(%begin `$1 hello X Y Z `$3_ end%)", t.toString());
    }

    @Test (expected = RuntimeException.class)
    public void test7() throws Exception {
        String pattern = "(%hello @_%)";
        String source = "(%hello world%)";
        String template = "(% begin $$1 end %)";
        Object t = replace(pattern, template, source);
        assertEquals("(%begin hello world end%)", t.toString());
        System.out.println(t);
    }

    @Test (expected = RuntimeException.class)
    public void test8() throws Exception {
        String template = "begin $2";
        Template t = Template.parse(template, new SimpleStringTokenizer());
    }

    @Test
    public void test9() throws Exception {
        String pattern = "@_";
        String source = "(%hello (%world X%)%)";
        String template = "$1";
        Object t = replace(pattern, template, source);
        System.out.println(t);
        assertEquals("(%hello (%world X%)%)", t.toString());
    }

    @Test
    public void test10() throws Exception {
        String pattern = "@_";
        String source = "(%hello (%world X%)%)";
        String template = "hello";
        Object t = replace(pattern, template, source);
        System.out.println(t);
        assertEquals("hello", t.toString());
    }

    @Test (expected = RuntimeException.class)
    public void test11() throws Exception {
        String template = "(%begin $%)";
        Template t = Template.parse(template, new SimpleStringTokenizer());
    }

    @Test (expected = RuntimeException.class)
    public void test12() throws Exception {
        String template = "(%begin $*%)";
        Template t = Template.parse(template, new SimpleStringTokenizer());
    }

    @Test (expected = RuntimeException.class)
    public void test13() throws Exception {
        String template = "(%begin $$%)";
        Template t = Template.parse(template, new SimpleStringTokenizer());
    }

    @Test (expected = RuntimeException.class)
    public void test14() throws Exception {
        String template = "(%begin $$_%)";
        Template t = Template.parse(template, new SimpleStringTokenizer());
    }

    @Test (expected = RuntimeException.class)
    public void test15() throws Exception {
        String template = "(%begin $**%)";
        Template t = Template.parse(template, new SimpleStringTokenizer());
    }

    @Test
    public void test16() throws Exception {
        String template = "$1";
        Template t = Template.parse(template, new SimpleStringTokenizer());
        assertEquals(template, t.toString());
    }

    @Test
    public void test17() throws Exception {
        String template = "$1";
        Template t = Template.parse(template, new SimpleStringTokenizer());
        assertEquals(template+"\n", t.toIndentedString());
    }

    @Test
    public void test18() throws Exception {
        String template = "$$1";
        Template t = Template.parse(template, new SimpleStringTokenizer());
        assertEquals(template, t.toString());
    }

    @Test
    public void test19() throws Exception {
        String template = "$$1";
        Template t = Template.parse(template, new SimpleStringTokenizer());
        assertEquals(template+"\n", t.toIndentedString());
    }

    @Test
    public void test20() throws Exception {
        String template = "$*1";
        Template t = Template.parse(template, new SimpleStringTokenizer());
        assertEquals(template, t.toString());
    }

    @Test
    public void test21() throws Exception {
        String template = "$*1";
        Template t = Template.parse(template, new SimpleStringTokenizer());
        assertEquals(template+"\n", t.toIndentedString());
    }

    @Test
    public void test22() throws Exception {
        String pattern = "@_";
        String source = "(%hello world%)";
        String template = "@";
        Object t = replace(pattern, template, source);
        assertEquals(template, t.toString());
    }

    @Test
    public void test23() throws Exception {
        String template = "(%@%)";
        Template t = Template.parse(template, new SimpleStringTokenizer());
        assertEquals("(%`@%)", t.toString());
    }

    @Test
    public void test24() throws Exception {
        String template = "@";
        Template t = Template.parse(template, new SimpleStringTokenizer());
        assertEquals("`@", t.toString());
    }

    @Test (expected = RuntimeException.class)
    public void test25() throws Exception {
        String pattern = "@_";
        String source = "(%hello world%)";
        String template = "$2";
        Object t = replace(pattern, template, source);
        assertEquals(template, t.toString());
    }

    @Test (expected = RuntimeException.class)
    public void test26() throws Exception {
        String pattern = "@_";
        String source = "(%hello world%)";
        String template = "$$2";
        Object t = replace(pattern, template, source);
        assertEquals(template, t.toString());
    }

    @Test (expected = RuntimeException.class)
    public void test27() throws Exception {
        String pattern = "(%@*_%)";
        String source = "(%hello world%)";
        String template = "(%$*2%)";
        Object t = replace(pattern, template, source);
        assertEquals(template, t.toString());
    }

    @Test (expected = RuntimeException.class)
    public void test28() throws Exception {
        String pattern = "(%@*_%)";
        String source = "(%hello world%)";
        String template = "$*1";
        Object t = replace(pattern, template, source);
        assertEquals(template, t.toString());
    }

    @Test
    public void test29() throws Exception {
        String pattern = "(% @_ (%world @*_%) %)";
        String source = "(%hello (%world X Y Z%)%)";
        String template = "(% begin $1 $*2 end %)";
        Object t = replace(pattern, template, source);
        System.out.println(t);
        assertEquals("(%begin hello X Y Z end%)", t.toString());
    }

}
