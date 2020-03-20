package org.shoehorn;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Data
public class Adapter implements MethodInterceptor {
    private final Object adapted;
    private InterceptAction beforeCall;
    private InterceptAction afterCall;

    public Adapter(Object adapted) {
        this.adapted = adapted;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return null;
    }

    @AllArgsConstructor
    public static class OuterBuilder {
        private final Object adaptedInstance;

        public InnerBuilder<?> as(Class<?> asClass) {
            return new InnerBuilder<>(this.adaptedInstance, asClass);
        }
    }

    public static class InnerBuilder<T> {
        private final Object adaptedInstance;
        private final Class<T> intoClass;

        public InnerBuilder(Object adaptedInstance, Class<T> intoClass) {
            this.adaptedInstance = adaptedInstance;
            this.intoClass = intoClass;
        }

        public T build() throws AdapterException {
            if(this.adaptedInstance == null) {
                throw new AdapterException("Null adapted instance.");
            }
            if(this.intoClass == null) {
                throw new AdapterException("Null target class.");
            }
            if(!this.intoClass.isAssignableFrom(this.adaptedInstance.getClass())) {
                String msg = "Incompatible target class %s and adapted instance type %s";
                throw new AdapterException(String.format(msg, this.intoClass, this.adaptedInstance.getClass()));
            }

            return (T)Enhancer.create(this.intoClass, new Adapter(this.adaptedInstance));
        }
    }
}
