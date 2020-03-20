package org.shoehorn;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface InterceptAction {
    Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy);
}
