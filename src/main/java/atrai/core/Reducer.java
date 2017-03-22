package atrai.core;

/**
 * Created by ksen on 2/18/17.
 */
public interface Reducer {
    public Object apply(Object child, Object initialReductionValue, Object environment);
}
