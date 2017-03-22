package atrai.core;

import java.util.StringTokenizer;

/**
 * A lexer for whitespace-delimited strings. Each maximal sequence of non-whitespace characters results in a token.
 * Uses the {@link StringTokenizer} class.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class SimpleStringTokenizer implements Lexer {
    private StringTokenizer tokenizer;

    public void setStream(String sub) {
        tokenizer = new StringTokenizer(sub);
    }

    public String getNextToken() {
        if (tokenizer.hasMoreTokens()) {
            return tokenizer.nextToken();
        } else {
            return null;
        }
    }
}
