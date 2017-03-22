package atrai.core;

/**
 * Interface for lexers from which {@link Tree}s can be built.
 */
public interface Lexer {
    /**
     * The string to be tokenized using this lexer
     * @param input
     */
    public void setStream(String input);

    /**
     * Returns the next token from the input string, or {@code null} if no more token is left
     * @return
     */
    public Object getNextToken();
}
