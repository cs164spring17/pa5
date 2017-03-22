package atrai.core;

/**
 * An interface for parsers of untyped trees.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
class SerializedTreeParser {
    /**
     * The token to use to open a (sub-)tree
     */
    public static final String LBS = "(%";
    /**
     * The token to use to close a (sub-)tree
     */
    public static final String RBS = "%)";
    /**
     * The escape character for tokens in the parser
     */
    public static final char ESC = '`';
    /**
     * The token to use to indicate a wildcard
     */
    public static final String WILDCARD = "@";
    /**
     * The token to use to indicate a capture group
     */
    public static final String WILDCARDCAPTURE = "@_";

    public static final String WILDCARDSTAR = "@*";

    public static final String WILDCARDSTARCAPTURE = "@*_";

    /**
     * The character to use to start a replacement group. Followed by a number to indicate which group.
     */
    public static final String REPLACEPREFIX = "$";

    public static final String REPLACEFLATTENPREFIX = "$$";
    public static final String REPLACEFLATTENPREFIXESC = "$"+ESC+"$";

    public static final String REPLACESTARPREFIX = "$*";


    private static final int LB = -2;
    private static final int RB = LB - 2;
    private static final int WC = RB - 2;
    private static final int WCC = WC - 2;
    private static final int WCS = WCC - 2;
    private static final int WCSC = WCS - 2;
    private static final int RP = WCSC - 2;
    private static final int RFP = RP - 2;
    private static final int RSP = RFP - 2;
    private static final SerializedTreeLexer slexer;
    private static final SerializedTreeLexer escaper;

    static {
        slexer = new SerializedTreeLexer(ESC);
        slexer.addString(SerializedTreeParser.LB, SerializedTreeParser.LBS);
        slexer.addString(SerializedTreeParser.RB, SerializedTreeParser.RBS);
        slexer.addString(SerializedTreeParser.WC, SerializedTreeParser.WILDCARD);
        slexer.addString(SerializedTreeParser.WCC, SerializedTreeParser.WILDCARDCAPTURE);
        slexer.addString(SerializedTreeParser.WCS, SerializedTreeParser.WILDCARDSTAR);
        slexer.addString(SerializedTreeParser.WCSC, SerializedTreeParser.WILDCARDSTARCAPTURE);
        slexer.addString(SerializedTreeParser.RP, SerializedTreeParser.REPLACEPREFIX);
        slexer.addString(SerializedTreeParser.RFP, SerializedTreeParser.REPLACEFLATTENPREFIX);
        slexer.addString(SerializedTreeParser.RSP, SerializedTreeParser.REPLACESTARPREFIX);

        escaper = new SerializedTreeLexer(-1);
        escaper.addString(SerializedTreeParser.LB, SerializedTreeParser.LBS);
        escaper.addString(SerializedTreeParser.RB, SerializedTreeParser.RBS);
        escaper.addString(SerializedTreeParser.WC, SerializedTreeParser.WILDCARD);
        escaper.addString(SerializedTreeParser.WCC, SerializedTreeParser.WILDCARDCAPTURE);
        escaper.addString(SerializedTreeParser.WCS, SerializedTreeParser.WILDCARDSTAR);
        escaper.addString(SerializedTreeParser.WCSC, SerializedTreeParser.WILDCARDSTARCAPTURE);
        escaper.addString(SerializedTreeParser.RP, SerializedTreeParser.REPLACEPREFIX);
        escaper.addString(SerializedTreeParser.RFP, SerializedTreeParser.REPLACEFLATTENPREFIX);
        escaper.addString(SerializedTreeParser.RSP, SerializedTreeParser.REPLACESTARPREFIX);
    }

    /**
     * Given a string, add sufficient escape characters so parsing results in the given string, not in a tree.
     *
     * @param source the string to escape
     * @return the escaped string
     */
    static String escapeString(String source) {
        escaper.setStream(source);
        int token = escaper.nextToken();
        StringBuilder sb = new StringBuilder();
        while (token != SerializedTreeLexer.EOF) {
            if (token == SerializedTreeParser.LB) {
                sb.append(ESC).append(LBS);
            } else if (token == SerializedTreeParser.RB) {
                sb.append(ESC).append(RBS);
            } else if (token == ESC) {
                sb.append(ESC).append(ESC);
            } else if (token == WC) {
                sb.append(ESC);
                sb.append(WILDCARD);
            } else if (token == WCC) {
                sb.append(ESC);
                sb.append(WILDCARDCAPTURE);
            } else if (token == WCS) {
                sb.append(ESC);
                sb.append(WILDCARDSTAR);
            } else if (token == WCSC) {
                sb.append(ESC);
                sb.append(WILDCARDSTARCAPTURE);
            } else if (token == RP) {
                sb.append(ESC);
                sb.append(REPLACEPREFIX);
            } else if (token == RFP) {
                sb.append(ESC);
                sb.append(REPLACEFLATTENPREFIXESC);
            } else if (token == RSP) {
                sb.append(ESC);
                sb.append(REPLACESTARPREFIX);
            } else {
                sb.append((char) token);
            }
            token = escaper.nextToken();
        }
        return sb.toString();
    }

    /**
     * Parse a string using the given lexer, optionally parsing pattern and/or replacement symbols.
     *
     * @param source        the string to parse
     * @param lexer         the lexer to use between special tokens
     * @param isPattern     whether to look for pattern symbols (eg. '@')
     * @param isReplacement whether to look for replacement symbols (eg. '$')
     * @return a builder containing the resulting tree.
     */
    static TreeBuilder parse(String source, Lexer lexer, boolean isPattern, boolean isReplacement) {
        TreeBuilder builder = new TreeBuilder();
        StringBuilder sb = new StringBuilder();

        slexer.setStream(source);
        int token = slexer.nextToken();
        while (token != SerializedTreeLexer.EOF) {
            if (token == SerializedTreeParser.LB) {
                addString(builder, sb, lexer);
                builder.beginSubTree();
                token = slexer.nextToken();
            } else if (token == SerializedTreeParser.RB) {
                addString(builder, sb, lexer);
                builder.endSubTree();
                token = slexer.nextToken();
            } else if (token == SerializedTreeParser.WC) {
                if (isPattern) {
                    addString(builder, sb, lexer);
                    builder.addLeaf(WildcardToken.instance);
                } else {
                    sb.append(WILDCARD);
                }
                token = slexer.nextToken();
            } else if (token == SerializedTreeParser.WCC) {
                if (isPattern) {
                    addString(builder, sb, lexer);
                    builder.addLeaf(WildcardCaptureToken.instance);
                } else {
                    sb.append(WILDCARDCAPTURE);
                }
                token = slexer.nextToken();
            } else if (token == SerializedTreeParser.WCS) {
                if (isPattern) {
                    addString(builder, sb, lexer);
                    builder.addLeaf(WildcardStarToken.instance);
                } else {
                    sb.append(WILDCARDSTAR);
                }
                token = slexer.nextToken();
            } else if (token == SerializedTreeParser.WCSC) {
                if (isPattern) {
                    addString(builder, sb, lexer);
                    builder.addLeaf(WildcardStarCaptureToken.instance);
                } else {
                    sb.append(WILDCARDSTARCAPTURE);
                }
                token = slexer.nextToken();
            } else if (token == SerializedTreeParser.RP || token == SerializedTreeParser.RFP || token == SerializedTreeParser.RSP) {
                if (isReplacement) {
                    int oldToken = token;
                    addString(builder, sb, lexer);
                    StringBuilder number = new StringBuilder();
                    token = slexer.nextToken();
                    while (token >= '0' && token <= '9') {
                        number.append((char) token);
                        token = slexer.nextToken();
                    }
                    if (number.length() > 0) {
                        int num = Integer.parseInt(number.toString());
                        if (oldToken == RP) {
                            builder.addLeaf(new ReplacementToken(num));
                        } else if (oldToken == RFP) {
                            builder.addLeaf(new ReplacementFlattenToken(num));
                        } else {
                            builder.addLeaf(new ReplacementStarToken(num));
                        }
                    } else {
                        throw new RuntimeException("Expecting number after $ or $_ in " + source);
                    }
                } else {
                    if (token == RP) {
                        sb.append(REPLACEPREFIX);
                        token = slexer.nextToken();
                    } else if (token == RFP) {
                        sb.append(REPLACEFLATTENPREFIX);
                        token = slexer.nextToken();
                    } else {
                        sb.append(REPLACESTARPREFIX);
                        token = slexer.nextToken();
                    }
                }
            } else {
                sb.append((char) token);
                token = slexer.nextToken();
            }
        }
        addString(builder, sb, lexer);
        return builder;
    }

    private static void addString(TreeBuilder builder, StringBuilder sb, Lexer lexer) {
        if (sb.length() > 0) {
            String sub = sb.toString();
            lexer.setStream(sub);
            Object leafNode;
            while ((leafNode = lexer.getNextToken()) != null) {
                builder.addLeaf(leafNode);
            }
            sb.delete(0, sb.length());
        }
    }
}
