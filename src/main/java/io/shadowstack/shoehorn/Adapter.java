package io.shadowstack.shoehorn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static io.shadowstack.shoehorn.Fluently.*;

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
        private List<MethodRouter.Builder> deriveMethodRoutersFromAnnotations() throws AdapterException {
            List<MethodRouter.Builder> methodRouterBuilders = new ArrayList<>();
            Class<?> adaptedClass = this.adaptedInstance.getClass();
            // Look for methods on the adapted instance that mimic a method of the exposed interface
            for (Method adapteeMethod : adaptedClass.getDeclaredMethods()) {
                String fullMethodName = adaptedClass.getCanonicalName() + "." + adapteeMethod.getName();
                Mimic mimicAnnotation = adapteeMethod.getAnnotation(Mimic.class);
                if (mimicAnnotation != null) {
                    Out outAnnotation = adapteeMethod.getAnnotation(Out.class);
                    // If no annotation exists to specify return type conversion, we assume void return
                    ArgumentConverter converter = new VoidConverter();
                    Class<?> toClass = Void.class;
                    if (outAnnotation != null) {
                        // Get the converter for the return type. This converts from the return type of the adapted
                        // instance to the return type of the exposed method which it mimics.
                        converter = ArgumentConverter.getInstanceFor(outAnnotation);
                        toClass = outAnnotation.to();
                    }
                    Class<?> type = mimicAnnotation.type();
                    if (type.equals(this.exposedInterface)) { // this method mimics one on the exposed interface
                        // Begin building the router to forward calls against the exposed method to our mimicking method
                        MethodRouter.Builder routerBuilder = method(mimicAnnotation.method()).to(adapteeMethod.getName());
                        List<ArgumentConversion> conversionBuilders = new ArrayList<>();
                        Annotation[][] allParameterAnnotations = adapteeMethod.getParameterAnnotations();
                        Class<?>[] parameterTypes = adapteeMethod.getParameterTypes();
                        int i = 0;
                        // Collect all the converters for the input arguments
                        for (Annotation[] parameterAnnotations : allParameterAnnotations) {
                            Class<?> parameterClass = parameterTypes[i++];
                            for (Annotation annotation : parameterAnnotations) {
                                if (annotation instanceof In) {
                                    In argumentAnnotation = (In) annotation;
                                    ArgumentConverter[] converters = ArgumentConverter.getInstancesFor(argumentAnnotation);
                                    for(int j=0;j<converters.length;j++) {
                                        ArgumentConverter ac = converters[j];
                                        // Here, we're converting from an input type of the exposed interface into
                                        // an input type of the adapted instance's method. This conversion goes in
                                        // the opposite direction of our output conversion.
                                        conversionBuilders.add(
                                            Fluently.convert(
                                                argumentAnnotation.from()[j]
                                            )
                                            .to(parameterClass)
                                            .using(ac)
                                        );
                                    }
                                }
                            }
                        }
                        routerBuilder.consuming(conversionBuilders.toArray(new ArgumentConversion[0]));
                        Class<?> returnType = adapteeMethod.getReturnType();
                        routerBuilder.producing(Fluently.convert(returnType).to(toClass).using(converter));
                        // Add to the router any advice interceptors annotated on this method
                        AdapterAdvice[] advisors = adapteeMethod.getAnnotationsByType(AdapterAdvice.class);
                        if(advisors != null && advisors.length > 0) {
                            for(AdapterAdvice advice : advisors) {
                                try {
                                    switch(advice.pointcut()) {
                                        case BEFORE: {
                                            routerBuilder.before(advice.interceptor().getConstructor().newInstance());
                                            break;
                                        }
                                        case AFTER: {
                                            routerBuilder.after(advice.interceptor().getConstructor().newInstance());
                                            break;
                                        }
                                    }

                                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                    String msg = "Couldn't add adapter advice %s to method %s. Is it missing a default constructor?";
                                    throw new AdapterException(String.format(msg, advice.interceptor().getSimpleName(), fullMethodName), e);
                                }
                            }
                        }
                        methodRouterBuilders.add(routerBuilder);
                    }
                }
            }
            return methodRouterBuilders;
        }

        @SuppressWarnings("unchecked")
        public T build() throws AdapterException {
            if(this.adaptedInstance == null) {
                throw new AdapterException("Null adapted instance.");
            }
            if(this.exposedInterface == null) {
                throw new AdapterException("Null target class.");
            }
            if(this.methodRouters.size() == 0) {
                log.info("No method routers provided, attempting to derive from adapted instance annotations...");
                try {
                    List<MethodRouter.Builder> methodRouterBuilders = this.deriveMethodRoutersFromAnnotations();
                    this.routing(methodRouterBuilders.toArray(new MethodRouter.Builder[0]));
                } catch(AdapterException e) {
                    throw e;
                } catch (NoSuchMethodException e) {
                    throw new AdapterException(e);
                }
            }

            return (T) Enhancer.create(this.exposedInterface, new Adapter(this.adaptedInstance, this.methodRouters));
        }
    }
}
