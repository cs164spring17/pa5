package examples.simplehandwrittenlexer;


import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ksen on 1/23/17 at 2:12 pm
 * For CS 164.  Should not be used in any software.
 */
public class InefficientLexer {
    private List<Pair> spec;

    public static void main(String[] args) {
        InefficientLexer lexer = new InefficientLexer();
        lexer.initializeSpec();
        lexer.lex("if x1 + x2 1 else 2");
    }

    private void initializeSpec() {
        spec = new LinkedList<>();
        spec.add(new Pair("if", TOKEN.KEYWORD));
        spec.add(new Pair("else", TOKEN.KEYWORD));
        spec.add(new Pair("\\s+", TOKEN.WHITESPACE));
        spec.add(new Pair("\\+", TOKEN.PLUS));
        spec.add(new Pair("\\-", TOKEN.MINUS));
        spec.add(new Pair("[0-9]+", TOKEN.NUMBER));
        spec.add(new Pair("[a-zA-Z_][a-zA-Z0-9_]*", TOKEN.IDENTIFIER));
    }

    private void lex(String rest) {
        while (rest.length() > 0) {
            String lexeme = "";
            TOKEN token = TOKEN.ERROR;
            for (Pair rule : spec) {
                String tmp = matchMaximalPrefix(rule.regex, rest);
                if (tmp.length() > lexeme.length()) {
                    lexeme = tmp;
                    token = rule.token;
                }
            }
            if (lexeme.length() > 0) {
                System.out.println("(" + lexeme + "," + token + ")");
                rest = rest.substring(lexeme.length());
            } else {
                System.out.println("(" + rest.charAt(0) + ",ERROR)");
                rest = rest.substring(1);
            }
        }
    }

    /**
     * Finds the maximal prefix of input that matches the regex.
     * If no such prefix is found, it output the empty string.
     *
     * @param regex The regex to use
     * @param input The input to match
     * @return the maximal matching prefix
     */
    private String matchMaximalPrefix(String regex, String input) {
        Pattern r = Pattern.compile("^(" + regex + ")(.*)");
        Matcher m = r.matcher(input);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    enum TOKEN {
        IDENTIFIER,
        NUMBER,
        KEYWORD,
        PLUS,
        MINUS,
        WHITESPACE,
        ERROR
    }

    class Pair {
        final String regex;
        final TOKEN token;

        public Pair(String regex, TOKEN token) {
            this.regex = regex;
            this.token = token;
        }
    }
}
