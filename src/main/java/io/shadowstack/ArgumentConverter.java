package io.shadowstack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public interface ArgumentConverter<F, T> {
    /**
     * Converts using a newly created instance
     * @param from instance from which to convert
     * @return newly created instance of the target type
     * @throws AdapterException wraps throwables generated during conversion
     */
    T convert(F from) throws AdapterException;

    /**
     * Converts using an existing instance
     * @param from instance from which to convert
     * @param to existing instance of the target type
     * @throws AdapterException wraps throwables generated during conversion
     */
    void convert(F from, T to) throws AdapterException;

    @SuppressWarnings("rawtypes")
    static ArgumentConverter getInstanceFor(Convert convertAnnotation) throws AdapterException {
        Class<? extends ArgumentConverter<?, ?>> useClass = convertAnnotation.use();
        Logger log = LoggerFactory.getLogger(useClass);
        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
        MethodType mt = MethodType.methodType(ArgumentConverter.class);
        if(!convertAnnotation.factoryMethod().isEmpty()) {
            try {
                MethodHandle instanceHandle = publicLookup.findStatic(useClass, convertAnnotation.factoryMethod(), mt);
                return (ArgumentConverter) instanceHandle.invoke();
            } catch (Throwable t) {
                String msg = "No invokable static factory method %s() found for class %s.";
                throw new AdapterException(String.format(msg, convertAnnotation.factoryMethod(), useClass.getSimpleName()));
            }

        }
        if(!convertAnnotation.singletonInstance().isEmpty()) {
            try {
                Field f = useClass.getDeclaredField(convertAnnotation.singletonInstance());
                f.setAccessible(true);
                if(f.isAccessible()) {
                    return (ArgumentConverter) f.get(null);
                }
            } catch (Throwable t) {
                String msg = "No accessible static member %s() found in class %s.";
                throw new AdapterException(String.format(msg, convertAnnotation.singletonInstance(), useClass.getSimpleName()));
            }
        }
        // No factory method or singleton instance was specified, continue with named-by-convention...
        // First check for a getInstance() or instance() static method
        try {
            MethodHandle instanceHandle = publicLookup.findStatic(useClass, "getInstance", mt);
            return (ArgumentConverter) instanceHandle.invoke();
        } catch (Throwable t) {
            log.warn("No invokable static factory method getInstance() found for this class.");
        }
        try {
            MethodHandle instanceHandle = publicLookup.findStatic(useClass, "instance", mt);
            return (ArgumentConverter) instanceHandle.invoke();
        } catch (Throwable t) {
            log.warn("No invokable static factory method instance() found for this class.");
        }
        // Next, check for a static member named INSTANCE
        try {
            Field f = useClass.getDeclaredField("INSTANCE");
            f.setAccessible(true);
            if(f.isAccessible()) {
                return (ArgumentConverter) f.get(null);
            }
        } catch (Throwable t) {
            log.warn("No accessible static member INSTANCE found for this class.");
        }

        String msg = "Class %s has no static, invokable factory method named 'getInstance' or 'instance', " +
                     "nor does it have an accessible, static member named 'INSTANCE'. No instance of this " +
                     "ArgumentConverter can be obtained.";
        throw new AdapterException(String.format(msg, useClass.getSimpleName()));
    }
}
