package atrai.interpreters.runtime;



//begin 1
class Value {
    public int i;
    public boolean b;
    public Lambda l;
    public String s;
}

abstract class Lambda {
    public static final int STACK_SIZE = 128;
    public static final int FRAME_SIZE = 32;
    public static Value[] stack;
    public static int sp = 0;
    public static Value a0 = new Value();
    public static Value t1 = new Value();
    public static Value t2 = new Value();
    public static Value t3 = new Value();
    public static Value t4 = new Value();
    static {
        stack = new Value[STACK_SIZE];
        for (int i=0; i<STACK_SIZE; i++) {
            stack[i] = new Value();
        }
    }
    public Value[] closure;

    public Lambda() {}

    public Lambda(Value[] frame, int fp) {
        this.closure = new Value[fp];
        System.arraycopy(frame, 0, this.closure, 0, fp);
    }

    public abstract void apply();
}

//end 1

class Main
        extends Lambda {
    public Main
//begin 2
(Value[] env, int fp) {
        super(env, fp);
    }

    public void apply() {
        Value[] frame = new Value[FRAME_SIZE];
        int fp = closure.length;
        System.arraycopy(closure, 0, frame, 0, closure.length);
        int label = 0;
        while(true) {
            switch(label) {
                case 0:
//end 2
//begin 3
                    return;
            }
        }
    }
}
//end 3

public class
Runtime
//begin 4
{
    public static int execute(String[] args) {
        (new Main(new Value[Lambda.FRAME_SIZE],0)).apply();
        return Lambda.a0.i;
    }

    public static void main(String[] args) {
        (new Main(new Value[Lambda.FRAME_SIZE],0)).apply();
    }
}
//end 4

