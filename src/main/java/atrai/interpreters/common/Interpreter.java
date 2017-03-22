package atrai.interpreters.common;

import atrai.core.Tree;
import atrai.core.UntypedTree;
import atrai.interpreters.COMPILEDFUN.FunCompiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * An abstract class that should be implemented ny any interpreter
 *
 * @author Koushik Sen
 */
public abstract class Interpreter {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (args.length != 3) {
            System.err.println("Usage: java -jar JAR [parse|interpret] fully-qualified-interpreter-class-name file-name");
            return;
        }

        String command = args[0];
        if (!Objects.equals(command, "parse") && !Objects.equals(command, "interpret")) {
            System.err.println("Usage: java -jar JAR [parse|interpret] interpreter-class-name file-name");
            return;
        }

        Class classDefinition = Class.forName(args[1]);
        Constructor cons = classDefinition.getConstructor();
        Interpreter interpreter = (Interpreter) cons.newInstance();

        UntypedTree tree = interpreter.parseFile(args[2]);
        if (command.equals("parse")) {
            System.out.println(tree.toIndentedString());
        } else if (command.equals("interpret")) {
            if (interpreter instanceof FunCompiler) {
                ((FunCompiler) interpreter).setName("StaffSolution");
            }
            Object result = interpreter.interpret(tree);
            if (result instanceof Tree) {
                result = ((Tree) result).toIndentedString();
            } else if (result instanceof CodeTemplate) {
                result = ((CodeTemplate) result).generateCode();
            }
            System.out.println(result);
        }
    }

    /**
     * Parses the file and returns the corresponding untyped tree
     *
     * @param fileName file to be parsed
     * @return untyped tree obtained by parsing the {@code fileName}
     */
    public abstract UntypedTree parseFile(String fileName);

    /**
     * Interprets the untyped tree
     *
     * @param st the untyped tree to be interpreted
     * @return the result of interpretation
     */
    public abstract Object interpret(UntypedTree st);

    /**
     * Parses and interprets the argument string
     *
     * @param sourceString string to be parsed and interpreted
     * @return the result of interpretation
     */
    public Object interpretString(String sourceString) {
        return interpret(parseString(sourceString));
    }

    /**
     * Parses the input string and returns the corresponding untyped tree
     *
     * @param sourceString string to parsed
     * @return untyped tree obtained by parsing the {@code sourceString}
     */
    public abstract UntypedTree parseString(String sourceString);

    /**
     * Parses and interprets the file passed as argument
     *
     * @param filename file to be parsed and interpreted
     * @return the result of interpretation
     */
    public Object interpretFile(String filename) {
        return interpret(parseFile(filename));
    }

}
