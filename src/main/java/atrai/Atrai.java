package atrai;

import atrai.antlr.GenericAntlrToUntypedTree;
import atrai.core.*;

/**
 *
 * Useful methods for matching, replacement, and parsing
 *
 * Created by ksen on 2/19/17.
 */
public class Atrai {
    /**
     * Matches and prints all occurrences of pattern in source
     * Matches and prints sub untyped trees from bottom to top and left to right
     *
     * @param source a serialized untyped tree
     * @param pattern a pattern
     */
    public static void matchAll(String source, String pattern) {
        Lexer lexer = new SimpleStringTokenizer();
        new Visitor(lexer).addObserverPost(pattern, c->{
            System.out.print("Match found: ");
            System.out.println(c[0]);
        }).parseAndVisit(source);
    }

    /**
     * Matches source exactly against pattern
     *
     * @param source a serialized untyped tree
     * @param pattern a pattern
     * @return the capture array
     */
    public static Object[] matchExact(String source, String pattern) {
        Lexer lexer = new SimpleStringTokenizer();
        Pattern p = Pattern.parse(pattern, lexer);
        UntypedTree st = UntypedTree.parse(source, lexer);
        return p.match(st);
    }

    /**
     * Matches and replaces all occurrences of {@code pattern} with {@code template} in {@code source}
     * Matches and replaces sub untyped trees from bottom to top and left to right
     *
     * @param source a serialized untyped tree
     * @param pattern a pattern
     * @param template template used for creating replacement strings
     * @return the modified untyped tree
     */
    public static UntypedTree replaceAll(String source, String pattern, String template) {
        Lexer lexer = new SimpleStringTokenizer();
        return new Visitor(lexer).addTransformerPost(pattern, template).parseAndVisit(source);
    }

    /**
     * Matches source exactly against pattern and replaces the source with the template suitably populated with the
     * captures.
     *
     * @param source a serialized untyped tree
     * @param pattern a pattern
     * @param template template used for creating replacement strings
     * @return the modified untyped tree
     */
    public static Object replaceExact(String source, String pattern, String template) {
        Lexer lexer = new SimpleStringTokenizer();
        Pattern p = Pattern.parse(pattern, lexer);
        Template t = Template.parse(template, lexer);
        UntypedTree st = UntypedTree.parse(source, lexer);
        return t.replace(p.match(st));
    }

    /**
     * Matches and prints all occurrences of pattern in source
     * Matches and prints sub untyped trees from bottom to top and left to right
     *
     * @param source an untyped tree
     * @param pattern a pattern
     */
    public static void matchAll(UntypedTree source, String pattern) {
        Lexer lexer = new SimpleStringTokenizer();
        (new Visitor(lexer).addObserverPost(pattern, c->{
            System.out.print("Match found: ");
            System.out.println(c[0]);
        })).visit(source);
    }

    /**
     * Matches source exactly against pattern
     *
     * @param source an untyped tree
     * @param pattern a pattern
     * @return the capture array
     */
    public static Object[] matchExact(UntypedTree source, String pattern) {
        Lexer lexer = new SimpleStringTokenizer();
        Pattern p = Pattern.parse(pattern, lexer);
        return p.match(source);
    }

    /**
     * Matches and replaces all occurrences of {@code pattern} with {@code template} in {@code source}.
     * Never modifies {@code source}
     * Matches and replaces sub untyped trees from bottom to top and left to right.
     *
     * @param source an untyped tree
     * @param pattern a pattern
     * @param template template used for creating replacement strings
     * @return the modified untyped tree
     */
    public static UntypedTree replaceAll(UntypedTree source, String pattern, String template) {
        Lexer lexer = new SimpleStringTokenizer();
        return (new Visitor(lexer).addTransformerPost(pattern, template)).visit(source);
    }

    /**
     * Matches source exactly against pattern and replaces the source with the template suitably populated with the
     * captures.
     *
     * @param source a serialized untyped tree
     * @param pattern a pattern
     * @param template template used for creating replacement strings
     * @return the modified untyped tree
     */
    public static Object replaceExact(UntypedTree source, String pattern, String template) {
        Lexer lexer = new SimpleStringTokenizer();
        Pattern p = Pattern.parse(pattern, lexer);
        Template t = Template.parse(template, lexer);
        return t.replace(p.match(source));
    }

    /**
     * Parses the file {@code fname} with the ANTLR grammar {@code grammarName} (which must be fully qualified with the
     * package name).
     *
     * @param fname name of file to be parsed
     * @param grammarName name of the ANTLR grammar.  Corresponding lexer and praser are loaded reflectively.
     * @param startSymbol start symbol in the grammar.  Corresponding method in the parser is loaded reflectively.
     * @param prefix adds the prefix as the first token in any sub serailized tree
     * @return the untyped tree generated by the parser
     */
    public static UntypedTree parseFile(String fname, String grammarName, String startSymbol, String prefix) {
        GenericAntlrToUntypedTree p = new GenericAntlrToUntypedTree();
        return p.parseFileToUntypedTree(prefix, grammarName, startSymbol, fname);
    }

    /**
     * Parses the string {@code source} with the ANTLR grammar {@code grammarName} (which must be fully qualified with the
     * package name).
     *
     * @param source string to be parsed
     * @param grammarName name of the ANTLR grammar.  Corresponding lexer and praser are loaded reflectively.
     * @param startSymbol start symbol in the grammar.  Corresponding method in the parser is loaded reflectively.
     * @param prefix adds the prefix as the first token in any sub serailized tree
     * @return the untyped tree generated by the parser
     */
    public static UntypedTree parseString(String source, String grammarName, String startSymbol, String prefix) {
        GenericAntlrToUntypedTree p = new GenericAntlrToUntypedTree();
        return p.parseStringToUntypedTree(prefix, grammarName, startSymbol, source);
    }

    public static void main(String[] args) {
        if (args[0].equals("matchAll")) {
            if (args.length == 3) {
                matchAll(args[1], args[2]);
            } else {
                System.err.println("Usage: java -jar <JAR> matchAll serialized-tree-string pattern");
            }
        } else
        if (args[0].equals("matchExact")) {
            if (args.length == 3) {
                Object[] captures = matchExact(args[1], args[2]);
                int i = 0;
                for (Object o: captures) {
                    System.out.print(String.format("captures[%d] = %s\n", i, o.toString()));
                    i++;
                }
            } else {
                System.err.println("Usage: java -jar <JAR> matchExact serialized-tree-string pattern");
            }
        } else
        if (args[0].equals("replaceAll")) {
            if (args.length == 4) {
                System.out.println(replaceAll(args[1], args[2], args[3]));
            } else {
                System.err.println("Usage: java -jar <JAR> replaceAll serialized-tree-string pattern template");
            }
        } else
        if (args[0].equals("replaceExact")) {
            if (args.length == 4) {
                System.out.println(replaceExact(args[1], args[2], args[3]));
            } else {
                System.err.println("Usage: java -jar <JAR> replaceExact serialized-tree-string pattern template");
            }
        } else
        if (args[0].equals("parseFile")) {
            if (args.length == 5) {
                System.out.println(parseFile(args[1], args[2], args[3], args[4]));
            } else {
                System.err.println("Usage: java -jar <JAR> parseFile file-name grammar-name start-symbol prefix-token");
            }
        } else
        if (args[0].equals("parseString")) {
            if (args.length == 5) {
                System.out.println(parseString(args[1], args[2], args[3], args[4]));
            } else {
                System.err.println("Usage: java -jar <JAR> parseString source-string grammar-name start-symbol prefix-token");
            }
        } else {
            System.err.println("Usage: java -jar <JAR> (matchAll|MatchExact|replaceAll|replaceExact|parseFile|parseString) help");
        }
    }
}
