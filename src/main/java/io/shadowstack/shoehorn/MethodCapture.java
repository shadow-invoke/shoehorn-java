package io.shadowstack.shoehorn;

import lombok.Getter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Captures a Method reference from a given Consumer. Allows us to extract a Method object
 * from a lambda instead of passing the method name as a String. Similar in functionality
 * to Jodd's MethRef (https://jodd.org/ref/methref.html).
 * @param <T> Enclosing type for which the referenced method is a member
 */
public class MethodCapture<T> implements MethodInterceptor {
    @Getter
    private Method lastInvoked = null;
    private final Class<T> clazz;

    public MethodCapture(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        this.lastInvoked = method;
        return null;
    }

    @SuppressWarnings("unchecked")
    public Method from(Consumer<T> caller) {
        T proxy = (T) Enhancer.create(this.clazz, this);
        caller.accept(proxy);
        return this.lastInvoked;
    }
}
