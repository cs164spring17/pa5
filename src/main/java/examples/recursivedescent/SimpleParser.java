package examples.recursivedescent;

/**
 * Created by ksen on 2/1/17.
 */
public class SimpleParser {
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
        parser.parse("3+50*1");
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

    public void parse(String input) {
        this.pos = 0;
        this.input = input.toCharArray();
        advance();
        expr();
        if (pos != this.input.length) {
            throw new RuntimeException("Parsing failed to process entire input string");
        }
    }

    private void expr() {
        term();
        while (token == '+') {
            advance();
            term();
        }
    }

    private void term() {
        atom();
        while (token == '*') {
            advance();
            atom();
        }
    }

    private void atom() {
        if (token == '(') {
            advance();
            expr();
            match(')');
        } else if (token >= '0' && token <= '9') {
            advance();
            while (token >= '0' && token <= '9') {
                advance();
            }
        } else {
            throw new RuntimeException("Unexpected token: " + token + ". Expecting: a digit or '('.");
        }
    }
}
