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
    static ArgumentConverter getInstanceFor(Out outAnnotation) throws AdapterException {
        Class<? extends ArgumentConverter<?, ?>> useClass = outAnnotation.with();
        Logger log = LoggerFactory.getLogger(useClass);
        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
        MethodType mt = MethodType.methodType(ArgumentConverter.class);
        if(!outAnnotation.factoryMethod().isEmpty()) {
            try {
                MethodHandle instanceHandle = publicLookup.findStatic(useClass, outAnnotation.factoryMethod(), mt);
                return (ArgumentConverter) instanceHandle.invoke();
            } catch (Throwable t) {
                String msg = "No invokable static factory method %s() found for class %s.";
                throw new AdapterException(String.format(msg, outAnnotation.factoryMethod(), useClass.getSimpleName()));
            }

        }
        if(!outAnnotation.singletonMember().isEmpty()) {
            try {
                Field f = useClass.getDeclaredField(outAnnotation.singletonMember());
                f.setAccessible(true);
                if(f.isAccessible()) {
                    return (ArgumentConverter) f.get(null);
                }
            } catch (Throwable t) {
                String msg = "No accessible static member %s() found in class %s.";
                throw new AdapterException(String.format(msg, outAnnotation.singletonMember(), useClass.getSimpleName()));
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

    @SuppressWarnings("rawtypes")
    static ArgumentConverter[] getInstancesFor(In inAnnotation) throws AdapterException {
        if(inAnnotation.from().length != inAnnotation.with().length) {
            throw new AdapterException("In annotation defined with different numbers of 'to' classes and 'use' converters.");
        }
        if(inAnnotation.factoryMethods().length > 0 && inAnnotation.factoryMethods().length != inAnnotation.from().length) {
            throw new AdapterException("In annotation defined with different numbers of 'to' classes and factory methods.");
        }
        if(inAnnotation.singletonMembers().length > 0 && inAnnotation.singletonMembers().length != inAnnotation.from().length) {
            throw new AdapterException("In annotation defined with different numbers of 'to' classes and singleton members.");
        }
        ArgumentConverter[] converters = new ArgumentConverter[inAnnotation.from().length];
        for(int i = 0; i< inAnnotation.from().length; i++) {
            Class<? extends ArgumentConverter<?, ?>> useClass = inAnnotation.with()[i];
            Logger log = LoggerFactory.getLogger(useClass);
            MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
            MethodType mt = MethodType.methodType(ArgumentConverter.class);
            if(inAnnotation.factoryMethods().length > i && !inAnnotation.factoryMethods()[i].isEmpty()) {
                try {
                    MethodHandle instanceHandle = publicLookup.findStatic(useClass, inAnnotation.factoryMethods()[i], mt);
                    converters[i] = (ArgumentConverter) instanceHandle.invoke();
                } catch (Throwable t) {
                    String msg = "No invokable static factory method %s() found for class %s.";
                    throw new AdapterException(String.format(msg, inAnnotation.factoryMethods()[i], useClass.getSimpleName()));
                }
            }
            else if(inAnnotation.singletonMembers().length > i && !inAnnotation.singletonMembers()[i].isEmpty()) {
                try {
                    Field f = useClass.getDeclaredField(inAnnotation.singletonMembers()[i]);
                    f.setAccessible(true);
                    if(f.isAccessible()) {
                        converters[i] = (ArgumentConverter) f.get(null);
                    }
                } catch (Throwable t) {
                    String msg = "No accessible static member %s() found in class %s.";
                    throw new AdapterException(String.format(msg, inAnnotation.singletonMembers()[i], useClass.getSimpleName()));
                }
            }
            else {
                // No factory method or singleton instance was specified, continue with named-by-convention...
                // First check for a getInstance() or instance() static method
                try {
                    MethodHandle instanceHandle = publicLookup.findStatic(useClass, "getInstance", mt);
                    converters[i] = (ArgumentConverter) instanceHandle.invoke();
                } catch (Throwable t) {
                    log.warn("No invokable static factory method getInstance() found for this class.");
                }
                try {
                    MethodHandle instanceHandle = publicLookup.findStatic(useClass, "instance", mt);
                    converters[i] = (ArgumentConverter) instanceHandle.invoke();
                } catch (Throwable t) {
                    log.warn("No invokable static factory method instance() found for this class.");
                }
                // Next, check for a static member named INSTANCE
                try {
                    Field f = useClass.getDeclaredField("INSTANCE");
                    f.setAccessible(true);
                    if (f.isAccessible()) {
                        converters[i] = (ArgumentConverter) f.get(null);
                    }
                } catch (Throwable t) {
                    log.warn("No accessible static member INSTANCE found for this class.");
                }
            }
            if(converters[i] == null) {
                String msg = "Class %s has no static, invokable factory method named 'getInstance' or 'instance', " +
                             "nor does it have an accessible, static member named 'INSTANCE'. No instance of this " +
                             "ArgumentConverter can be obtained.";
                throw new AdapterException(String.format(msg, useClass.getSimpleName()));
            }
        }
        return converters;
    }
}
