package atrai.interpreters.common;

import atrai.core.InternalNode;
import atrai.core.UntypedTree;

import javax.tools.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Created by ksen on 3/8/17.
 */

class LambdaCode {
    String pre;
    String mid;
    String post;

    public LambdaCode(String pre, String mid, String post) {
        this.pre = pre;
        this.mid = mid;
        this.post = post;
    }
}

public class CodeTemplate {
    /**
     * Should be modified if you want to implement the main driver differently.
     */
    public static String mainClassCode = "" +
            "{\n" +
            "    public static int execute(String[] args) {\n" +
            "        (new Main(new Value[Lambda.FRAME_SIZE],0)).apply();\n" +
            "        return Lambda.a0.i;\n" +
            "    }\n" +
            "\n" +
            "    public static void main(String[] args) {\n" +
            "        (new Main(new Value[Lambda.FRAME_SIZE],0)).apply();\n" +
            "    }\n" +
            "}\n";
    private static String region1 = "" +
            "class Value {\n" +
            "    public int i;\n" +
            "    public boolean b;\n" +
            "    public Lambda l;\n" +
            "    public String s;\n" +
            "}\n" +
            "\n" +
            "abstract class Lambda {\n" +
            "    public static final int STACK_SIZE = 128;\n" +
            "    public static final int FRAME_SIZE = 32;\n" +
            "    public static Value[] stack;\n" +
            "    public static int sp = 0;\n" +
            "    public static Value a0 = new Value();\n" +
            "    public static Value t1 = new Value();\n" +
            "    public static Value t2 = new Value();\n" +
            "    public static Value t3 = new Value();\n" +
            "    public static Value t4 = new Value();\n" +
            "    static {\n" +
            "        stack = new Value[STACK_SIZE];\n" +
            "        for (int i=0; i<STACK_SIZE; i++) {\n" +
            "            stack[i] = new Value();\n" +
            "        }\n" +
            "    }\n" +
            "    public Value[] closure;\n" +
            "\n" +
            "    public Lambda() {}\n" +
            "\n" +
            "    public Lambda(Value[] frame, int fp) {\n" +
            "        this.closure = new Value[fp];\n" +
            "        System.arraycopy(frame, 0, this.closure, 0, fp);\n" +
            "    }\n" +
            "\n" +
            "    public abstract void apply();\n" +
            "}\n" +
            "\n";
    private static String defaultLambdaPre = "" +
            "(Value[] env, int fp) {\n" +
            "        super(env, fp);\n" +
            "    }\n" +
            "\n" +
            "    public void apply() {\n" +
            "        Value[] frame = new Value[FRAME_SIZE];\n" +
            "        int fp = closure.length;\n" +
            "        System.arraycopy(closure, 0, frame, 0, closure.length);\n" +
            "        int label = 0;\n" +
            "        while(true) {\n" +
            "            switch(label) {\n" +
            "                case 0:\n";
    private static String defaultLambdaPost = "" +
            "                    return;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    private TreeMap<String, LambdaCode> lambdasNameToCode = new TreeMap<>();
    private String name;

    /**
     * Create a code template class for generated code
     *
     * @param name name of the main driver class in the generated code
     */
    public CodeTemplate(String name) {
        this.name = name;
    }

    /**
     * Add a new subclass of Lambda
     *
     * @param name name of the class
     * @param body code to be inserted in the body of the switch statement.  All (% and %) meta-characters are removed before insertion.
     */
    public void addLambda(String name, UntypedTree body) {
        addLambda(name, body, null, null);
    }

    /**
     * Add a new subclass of Lambda
     *
     * @param name name of the class
     * @param body code to be inserted in the body of the switch statement.  All (% and %) meta-characters are removed before insertion.
     * @param pre  code to be inserted before the body in case the defaultLambdaPre is not suitable
     * @param post code to be inserted after the body in case the defaultLambdaPost is not suitable
     */
    public void addLambda(String name, UntypedTree body, String pre, String post) {
        InternalNode n = (InternalNode) body.getRoot();
        addLambda(name, n, pre, post);
    }

    /**
     * Add a new subclass of Lambda
     *
     * @param name name of the class
     * @param node code to be inserted in the body of the switch statement.  All (% and %) meta-characters are removed before insertion.
     * @param pre  code to be inserted before the body in case the defaultLambdaPre is not suitable
     * @param post code to be inserted after the body in case the defaultLambdaPost is not suitable
     */
    public void addLambda(String name, InternalNode node, String pre, String post) {
        StringBuilder code = new StringBuilder();
        node.iterate((child, initial, context) -> {
            InternalNode n2 = (InternalNode) child;
            n2.iterate((child1, initial1, context1) -> {
                code.append(child1).append(' ');
                return null;
            }, null, null);
            code.append('\n');
            return null;
        }, null, null);
        addLambda(name, new LambdaCode(pre, code.toString(), post));
    }

    private void addLambda(String name, LambdaCode code) {
        lambdasNameToCode.put(name, code);
    }

    /**
     * Add a new subclass of Lambda
     *
     * @param name name of the class
     * @param node code to be inserted in the body of the switch statement.  All (% and %) meta-characters are removed before insertion.
     */
    public void addLambda(String name, InternalNode node) {
        addLambda(name, node, null, null);
    }

    /**
     * Generate and write the code to a file, where the name of the file is the name passed to the constructor.  .java is appended to the file name.
     */
    public void writeCode() {
        try {
            PrintWriter out = new PrintWriter(name + ".java");
            out.println(generateCode());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate code and return the code as a String
     *
     * @return
     */
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(region1);
        for (String name : lambdasNameToCode.keySet()) {
            LambdaCode lcode = lambdasNameToCode.get(name);
            sb.append("class ")
                    .append(name)
                    .append(" ")
                    .append(" extends Lambda {\n")
                    .append("    public ")
                    .append(name);
            if (lcode.pre != null) sb.append(lcode.pre);
            else sb.append(defaultLambdaPre);
            sb.append(lcode.mid);
            if (lcode.post != null) sb.append(lcode.post);
            else sb.append(defaultLambdaPost);
        }
        sb.append("public class ").append(name);
        sb.append(mainClassCode);
        return sb.toString();
    }

    /**
     * Must be called after {@code generateCode}. Compiles the generated file.
     *
     * @return true if compilation succeeds.  false otherwise.
     * @throws IOException
     */
    public boolean compileCode() throws IOException {
        File[] files1 = new File[]{new File(name + ".java")}; // input for first compilation task
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files1));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits1);
        if (!task.call()) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                final String message = diagnostic.getMessage(Locale.getDefault());
                final String location = Optional.ofNullable(diagnostic.getSource()).map(FileObject::toUri).map(Object::toString).orElse("unknown");
                System.out.format("Error on line %d in %s: %s%n", diagnostic.getLineNumber(), location, message);
            }
            return false;
        }
        fileManager.close();
        return true;
    }

    /**
     * Must be called after {@code compileCode}.  Executes the compiled class files.
     *
     * @return returns {@code a0.i}.  return value should not be used.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws MalformedURLException
     */
    public int executeCode() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, MalformedURLException {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});
        Class<?> cls = classLoader.loadClass(name);
        Method meth = cls.getMethod("execute", String[].class);
        try {
            return (int) meth.invoke(null, (Object) null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }
}
