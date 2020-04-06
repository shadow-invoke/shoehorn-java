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
    static ArgumentConverter getInstanceOf(Class<? extends ArgumentConverter> clazz) throws AdapterException {
        Logger log = LoggerFactory.getLogger(clazz);
        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
        MethodType mt = MethodType.methodType(ArgumentConverter.class);
        // First check for a getInstance() or instance() static method
        try {
            MethodHandle instanceHandle = publicLookup.findStatic(clazz, "getInstance", mt);
            return (ArgumentConverter) instanceHandle.invoke();
        } catch (Throwable t) {
            log.warn("No invokable static factory method getInstance() found for this class.", t);
        }
        try {
            MethodHandle instanceHandle = publicLookup.findStatic(clazz, "instance", mt);
            return (ArgumentConverter) instanceHandle.invoke();
        } catch (Throwable t) {
            log.warn("No invokable static factory method instance() found for this class.", t);
        }
        // Next, check for a static member named INSTANCE
        try {
            Field f = clazz.getDeclaredField("INSTANCE");
            f.setAccessible(true);
            if(f.isAccessible()) {
                return (ArgumentConverter) f.get(null);
            }
        } catch (Throwable t) {
            log.warn("No accessible static member INSTANCE found for this class.", t);
        }

        String msg = "Class %s has no static, invokable factory method named 'getInstance' or 'instance', " +
                     "nor does it have an accessible, static member named 'INSTANCE'. No instance of this " +
                     "ArgumentConverter can be obtained.";
        throw new AdapterException(String.format(msg, clazz.getSimpleName()));
    }
}
