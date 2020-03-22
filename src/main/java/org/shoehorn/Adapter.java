package org.shoehorn;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Adapter implements MethodInterceptor {
    private final Object adapted;
    private final Map<Method, MethodRouter> methodRouters;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        MethodRouter router = methodRouters.get(method);
        if(router == null) {
            String msg = "No method routing specified for method %s of class %s";
            throw new AdapterException(String.format(msg, method.getName(), this.adapted.getClass().getSimpleName()));
        }
        return router.forward(args, adapted);
    }

    @AllArgsConstructor
    public static class OuterBuilder {
        private final Object adaptedInstance;

        public <T> InnerBuilder<T> into(Class<T> asClass) {
            return new InnerBuilder<>(this.adaptedInstance, asClass);
        }
    }

    public static class InnerBuilder<T> {
        private final Object adaptedInstance;
        private final Class<T> exposedClass;
        private Map<Method, MethodRouter> methodRouters = new HashMap<>();

        public InnerBuilder(Object adaptedInstance, Class<T> exposedClass) {
            this.adaptedInstance = adaptedInstance;
            this.exposedClass = exposedClass;
        }

        public InnerBuilder<T> routing(MethodRouter.Builder... builders) throws AdapterException, NoSuchMethodException {
            if(builders != null && builders.length > 0) {
                for(MethodRouter.Builder builder : builders) {
                    MethodRouter router = builder.build(exposedClass, this.adaptedInstance.getClass());
                    methodRouters.put(router.getMethodFrom(), router);
                }
            }
            return this;
        }

        public T build() throws AdapterException {
            if(this.adaptedInstance == null) {
                throw new AdapterException("Null adapted instance.");
            }
            if(this.exposedClass == null) {
                throw new AdapterException("Null target class.");
            }
            if(this.methodRouters.size() == 0) {
                throw new AdapterException("No method routers passed to adapter builder.");
            }

            return (T)Enhancer.create(this.exposedClass, new Adapter(this.adaptedInstance, this.methodRouters));
        }
    }
}
