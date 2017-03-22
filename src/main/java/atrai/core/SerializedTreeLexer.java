package atrai.core;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;

/**
 * A lexer (but not a {@link Lexer}) for untyped trees.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
class SerializedTreeLexer {
    public static final int EOF = -1;
    private final int escapeChar;
    private final TrieNode trie;
    private IntStack availableChars;
    private int charIndex;
    private String stream;

    /**
     * Create a new tree lexer with the given escape code.
     *
     * @param escapeChar the escape character to use. A good default is -2.
     */
    SerializedTreeLexer(int escapeChar) {
        this.escapeChar = escapeChar;
        this.trie = new TrieNode();
    }

    /**
     * Set the string to tokenize
     *
     * @param stream the string to tokenize
     */
    void setStream(String stream) {
        this.stream = stream;
        this.charIndex = 0;
        this.availableChars = new IntArrayList();
    }

    /**
     * Add a token to the recognizer
     *
     * @param ID     token id to return
     * @param lexeme string to recognize
     */
    void addString(int ID, String lexeme) {
        this.addStringAux(ID, lexeme);
    }

    private void addStringAux(int ID, String lexeme) {
        int len = lexeme.length();
        TrieNode current = this.trie;

        for (int i = 0; i < len; i++) {
            char c = lexeme.charAt(i);
            current = current.getOrCreate(c);
        }
        current.ID = ID;
        current.isSet = true;
    }

    /**
     * Get the next token from the stream
     *
     * @return the id of the detected token (negative) or the
     * character code of the next character in the stream (non-negative)
     */
    int nextToken() {
        int inp;
        inp = this.readChar();
        if (inp == SerializedTreeLexer.EOF) {
            return inp;
        }
        if (inp == this.escapeChar) {
            inp = this.readChar();
            return inp;
        }
        int oldCharIndex = this.charIndex;
        int firstInp = inp;
        TrieNode prev = null;
        TrieNode ret = this.trie.get(inp);
        while (ret != null) {
            inp = this.readChar();
            prev = ret;
            ret = ret.get(inp);
        }
        if (prev != null && prev.isSet) {
            this.pushChar(inp);
            return prev.ID;
        } else {
            this.charIndex = oldCharIndex;
            return firstInp;
        }
    }

    private int readChar() {
        int inp;
        if (this.availableChars.isEmpty()) {
            if (stream.length() <= this.charIndex) {
                inp = SerializedTreeLexer.EOF;
            } else {
                inp = this.stream.charAt(this.charIndex);
            }
            this.charIndex++;
        } else {
            inp = this.availableChars.pop();
        }
        return inp;
    }

    private void pushChar(int c) {
        this.availableChars.push(c);
    }

//    public String scannedPrefix() {
//        return this.stream.substring(0, this.charIndex - 1);
//    }
}
