package io.shadowstack;

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
    /**
     * The adapted instance to which method invocations will be routed.
     */
    private final Object adaptedInstance;
    /**
     * Method routing specifications, including consume/produce converters and forwarding hooks.
     */
    private final Map<Method, MethodRouter> methodRouters;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        MethodRouter router = methodRouters.get(method);
        if(router == null) {
            String msg = "No method routing specified for method %s of class %s";
            String cls = this.adaptedInstance.getClass().getSimpleName();
            throw new AdapterException(String.format(msg, method.getName(), cls));
        }
        return router.forward(args, this.adaptedInstance);
    }

    @AllArgsConstructor
    public static class OuterBuilder {
        private final Object adaptedInstance;

        /**
         * Specify the exposed interface for this adapter.
         * @param exposedInterface Class instance of the exposed interface (can also be a concrete or abstract class).
         * @param <T> Type parameter (unbounded).
         * @return An InnerBuilder of the appropriate generic type.
         */
        public <T> InnerBuilder<T> into(Class<T> exposedInterface) {
            return new InnerBuilder<>(this.adaptedInstance, exposedInterface);
        }
    }

    public static class InnerBuilder<T> {
        private final Object adaptedInstance;
        private final Class<T> exposedInterface;
        private Map<Method, MethodRouter> methodRouters = new HashMap<>();

        public InnerBuilder(Object adaptedInstance, Class<T> exposedInterface) {
            this.adaptedInstance = adaptedInstance;
            this.exposedInterface = exposedInterface;
        }

        public InnerBuilder<T> routing(MethodRouter.Builder... builders) throws AdapterException, NoSuchMethodException {
            if(builders != null && builders.length > 0) {
                for(MethodRouter.Builder builder : builders) {
                    MethodRouter router = builder.build(this.exposedInterface, this.adaptedInstance.getClass());
                    methodRouters.put(router.getMethodFrom(), router);
                }
            }
            return this;
        }

        public T build() throws AdapterException {
            if(this.adaptedInstance == null) {
                throw new AdapterException("Null adapted instance.");
            }
            if(this.exposedInterface == null) {
                throw new AdapterException("Null target class.");
            }
            if(this.methodRouters.size() == 0) {
                throw new AdapterException("No method routers passed to adapter builder.");
            }

            return (T) Enhancer.create(this.exposedInterface, new Adapter(this.adaptedInstance, this.methodRouters));
        }
    }
}
