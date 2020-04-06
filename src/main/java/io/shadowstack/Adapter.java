package io.shadowstack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.shadowstack.Fluently.*;
import static io.shadowstack.Fluently.convert;

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

    @Slf4j
    public static class InnerBuilder<T> {
        private final Object adaptedInstance;
        private final Class<T> exposedInterface;
        private final Map<Method, MethodRouter> methodRouters = new HashMap<>();

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

        @SuppressWarnings({"unchecked", "rawtypes"})
        public T build() throws AdapterException {
            if(this.adaptedInstance == null) {
                throw new AdapterException("Null adapted instance.");
            }
            if(this.exposedInterface == null) {
                throw new AdapterException("Null target class.");
            }
            if(this.methodRouters.size() == 0) {
                log.info("No method routers provided, attempting to derive from annotations...");
                try {
                    List<MethodRouter.Builder> methodRouterBuilders = new ArrayList<>();
                    for (Method adapteeMethod : this.adaptedInstance.getClass().getDeclaredMethods()) {
                        Mimic mimicAnnotation = adapteeMethod.getAnnotation(Mimic.class);
                        Convert convertAnnotation = adapteeMethod.getAnnotation(Convert.class);
                        if (mimicAnnotation != null && convertAnnotation != null) {
                            Class<?> type = mimicAnnotation.type();
                            if (type.equals(this.exposedInterface)) {
                                // this method on from mimics a method on to
                                MethodRouter.Builder routerBuilder = method(adapteeMethod.getName())
                                                                        .to(mimicAnnotation.method());
                                List<ArgumentConversion> cbs = new ArrayList<>();
                                Annotation[][] allParameterAnnotations = adapteeMethod.getParameterAnnotations();
                                Class<?>[] parameterTypes = adapteeMethod.getParameterTypes();
                                int i = 0;
                                for (Annotation[] parameterAnnotations : allParameterAnnotations) {
                                    Class<?> parameterClass = parameterTypes[i++];
                                    for (Annotation annotation : parameterAnnotations) {
                                        if (annotation instanceof Convert) {
                                            Convert argumentAnnotation = (Convert) annotation;
                                            Class<? extends ArgumentConverter<?, ?>> clazz = argumentAnnotation.use();
                                            ArgumentConverter ac = ArgumentConverter.getInstanceOf(clazz);
                                            // This is a little counter-intuitive. Here, we're converting the
                                            // input of the exposed interface into the type of the adapted
                                            // instance's method. So the opposite of the direction we're
                                            // going with the output.
                                            cbs.add(
                                                convert(
                                                    argumentAnnotation.to()
                                                )
                                                .to(parameterClass)
                                                .using(ac)
                                            );
                                        }
                                    }
                                }
                                routerBuilder.consuming(cbs.toArray(new ArgumentConversion[0]));
                                Class<?> rt = adapteeMethod.getReturnType();
                                Class<? extends ArgumentConverter<?, ?>> clazz = convertAnnotation.use();
                                ArgumentConverter ac = ArgumentConverter.getInstanceOf(clazz);
                                routerBuilder.producing(convert(rt).to(convertAnnotation.to()).using(ac));
                                methodRouterBuilders.add(routerBuilder);
                            }
                        }
                    }
                    this.routing(methodRouterBuilders.toArray(new MethodRouter.Builder[0]));
                } catch(Throwable t){
                    throw new AdapterException(t);
                }
            }

            return (T) Enhancer.create(this.exposedInterface, new Adapter(this.adaptedInstance, this.methodRouters));
        }
    }
}
