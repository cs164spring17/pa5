package examples.recursivedescent;

import java.util.LinkedList;

/**
 * Created by ksen on 2/1/17.
 */

class Node {
    private final LinkedList<Object> children;
    private final String type;

    public Node(String type) {
        this.type = type;
        children = new LinkedList<>();
    }

    public void addChild(Object child) {
        children.addLast(child);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", type, children);
    }
}

public class SimpleParserWithParseTree {
    /**
     * Creates a parse tree while parsing.  Though the grammar specifies '+' and '*' to be right-associative, we create the
     * nodes of a tree in expr() and term() in a way such that '+' and '*' become left-associative.
     * <p>
     * Grammar: (+ amd * are right-associative)
     * expr -> expr '+' term | term
     * term -> term '*' atom | atom
     * atom -> '(' expr ')' | INT
     * <p>
     * <p>
     * Equivalent grammar: creation of parse tree ensures left-associativity
     * expr -> term ('+' term)*
     * atom -> atom ('*' atom)*
     * atom -> '(' expr ')' | INT
     */

    private int pos;
    private char[] input;
    private char token;

    public static void main(String[] args) {
        SimpleParserWithParseTree parser = new SimpleParserWithParseTree();
        Node tree = parser.parse("3+50*1");
        System.out.println(tree);
    }

    private char getToken() {
        if (pos < input.length) {
            return input[pos++];
        } else {
            return 0;
        }
    }

    private void advance() {
        token = getToken();
        System.out.println("Token: " + token);
    }

    private void match(char t) {
        if (token == t) {
            advance();
        } else {
            throw new RuntimeException("Unexpected token: " + token + ". Expecting: " + t + ".");
        }
    }

    public Node parse(String input) {
        this.pos = 0;
        this.input = input.toCharArray();
        advance();
        return expr();
    }

    private Node expr() {
        Node tmp1 = term();
        Node tmp2;
        while (token == '+') {
            advance();
            tmp2 = term();

            Node tmp = new Node("expr");
            tmp.addChild(tmp1);
            tmp.addChild("+");
            tmp.addChild(tmp2);
            tmp1 = tmp;
        }
        return tmp1;
    }

    private Node term() {
        Node tmp1 = atom();
        Node tmp2;
        while (token == '*') {
            advance();
            tmp2 = atom();

            Node tmp = new Node("term");
            tmp.addChild(tmp1);
            tmp.addChild("*");
            tmp.addChild(tmp2);
            tmp1 = tmp;
        }
        return tmp1;
    }

    private Node atom() {
        if (token == '(') {
            advance();
            Node tmp = new Node("atom");
            tmp.addChild('(');
            tmp.addChild(expr());
            match(')');
            tmp.addChild(')');
            return tmp;
        } else if (token >= '0' && token <= '9') {
            String num = token + "";
            advance();
            while (token >= '0' && token <= '9') {
                num += token;
                advance();
            }
            Node tmp = new Node("atom");
            tmp.addChild(num);
            return tmp;
        } else {
            throw new RuntimeException("Unexpected token: " + token + ". Expecting: a digit or '('.");
        }
    }
}
