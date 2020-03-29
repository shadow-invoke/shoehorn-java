package io.shadowstack;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class MethodRouter {
    private final Method methodFrom;
    private final ArgumentConversion<Object, Object>[] consumingFrom;
    private final MethodForwardingInterceptor beforeForwarding;
    private final Method methodTo;
    private final ArgumentConversion<Object, Object> producingTo;
    private final MethodForwardingInterceptor afterForwarding;

    /**
     * Forward a method invocation from the exposed interface to the adapted instance, converting the arguments
     * consumed by the adapted instance as specified and converting the result produced by it as specified.
     * @param inputs The arguments passed to the exposed interface by the caller, to be converted into
     *               a set of arguments accepted by the adapted instance.
     * @param adaptedInstance The adapted instance itself.
     * @return The result produced by the adapted instance, to be converted into an instance of the type
     *         returned by the exposed interface.
     * @throws AdapterException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object forward(Object[] inputs, Object adaptedInstance) throws AdapterException, InvocationTargetException, IllegalAccessException {
        if(inputs == null || adaptedInstance == null) {
            throw new AdapterException("Null inputs or adapted instance.");
        }

        Object currentSourceInput = null, currentDestinationInput = null;
        int currentSourceInputIndex = -1;
        List<Object> destinationInputs = new ArrayList<>();
        /**
         * If the "from" Class changes type, we're at a new position in the source arguments.
         * If the "to" Class changes type, we're at a new position in the destination arguments.
         */
        for(ArgumentConversion<Object, Object> conversion : this.consumingFrom) {
            if(currentSourceInput == null || !currentSourceInput.getClass().equals(conversion.getFrom())) {
                currentSourceInput = inputs[++currentSourceInputIndex];
            }
            if(currentDestinationInput == null || !currentDestinationInput.getClass().equals(conversion.getTo())) {
                currentDestinationInput = conversion.convert(currentSourceInput);
                destinationInputs.add(currentDestinationInput);
            } else {
                conversion.convert(currentSourceInput, currentDestinationInput);
            }
        }

        Object[] adaptedInstanceInputs = destinationInputs.toArray();

        if(this.beforeForwarding != null) {
            Object forwardingResult = this.beforeForwarding.intercept(adaptedInstanceInputs, adaptedInstance, null);
            if(forwardingResult != null) {
                // Supercede the result that would've been returned by the adapted instance and don't invoke its method.
                return this.producingTo.convert(forwardingResult);
            }
        }

        Object adaptedInstanceResult = this.methodTo.invoke(adaptedInstance, adaptedInstanceInputs);

        if(this.afterForwarding != null) {
            Object forwardingResult = this.afterForwarding.intercept(adaptedInstanceInputs, adaptedInstance, adaptedInstanceResult);
            if(forwardingResult != null) {
                // Replace the result that would've been returned by the adapted instance and don't invoke its method.
                adaptedInstanceResult = forwardingResult;
            }
        }
        return this.producingTo.convert(adaptedInstanceResult);
    }

    public static class Builder {
        private final String methodFrom;
        private String methodTo;
        protected ArgumentConversion[] consumingFrom;
        protected ArgumentConversion producingTo;
        protected MethodForwardingInterceptor beforeForwarding = null;
        protected MethodForwardingInterceptor afterForwarding = null;

        /**
         * Construct a new MethodRouter Builder.
         * @param methodFrom The name of the exposed interface's method from which this is routing.
         */
        public Builder(String methodFrom) {
            this.methodFrom = methodFrom;
        }

        /**
         * Sets the name of the destination method.
         * @param methodTo The name of the adapted instance's method to which this is routing.
         * @return The Builder, for fluency.
         */
        public Builder to(String methodTo) {
            this.methodTo = methodTo;
            return this;
        }

        /**
         * Action to be performed before the method invocation is forwarded.
         * @param interceptor The action to be performed.
         * @return The Builder, for fluency.
         */
        public Builder before(MethodForwardingInterceptor interceptor) {
            this.beforeForwarding = interceptor;
            return this;
        }

        /**
         * Action to be performed after the method invocation is forwarded.
         * @param interceptor The action to be performed.
         * @return The Builder, for fluency.
         */
        public Builder after(MethodForwardingInterceptor interceptor) {
            this.afterForwarding = interceptor;
            return this;
        }

        public Builder consuming(ArgumentConversion... conversions) {
            this.consumingFrom = conversions;
            return this;
        }

        public Builder producing(ArgumentConversion conversion) {
            this.producingTo = conversion;
            return this;
        }

        public MethodRouter build(Class<?> classFrom, Class<?> classTo) throws NoSuchMethodException, AdapterException {
            if(this.consumingFrom == null || this.consumingFrom.length == 0) {
                throw new AdapterException("No consuming conversions passed to MethodRouter.Builder.");
            }
            if( this.producingTo == null) {
                throw new AdapterException("Null producing conversion passed to MethodRouter.Builder.");
            }
            if(this.methodTo == null || this.methodTo.isEmpty()) {
                throw new AdapterException("Empty or null destination method passed to MethodRouter.Builder.");
            }
            if(this.methodFrom == null || this.methodFrom.isEmpty()) {
                throw new AdapterException("Empty or null source method passed to MethodRouter.Builder.");
            }

            /**
             * Argument conversions could be many-to-one or one-to-many. When we see a repeated Class as
             * either input or output, we assume it refers to the same positional argument as before.
             */
            Class<?> lastIn = null, lastOut = null;
            List<Class<?>> uniqueIn = new ArrayList<>(), uniqueOut = new ArrayList<>();
            for(ArgumentConversion<?, ?> conversion : this.consumingFrom) {
                if(lastIn == null || !lastIn.equals(conversion.getFrom())) {
                    uniqueIn.add(conversion.getFrom());
                    lastIn = conversion.getFrom();
                }
                if(lastOut == null || !lastOut.equals(conversion.getTo())) {
                    uniqueOut.add(conversion.getTo());
                    lastOut = conversion.getTo();
                }
            }

            return new MethodRouter(
                    classFrom.getMethod(this.methodFrom, uniqueIn.toArray(new Class<?>[uniqueIn.size()])),
                    this.consumingFrom,
                    this.beforeForwarding,
                    classTo.getMethod(this.methodTo, uniqueOut.toArray(new Class<?>[uniqueOut.size()])),
                    this.producingTo,
                    this.afterForwarding
            );
        }
    }

    public static class DumbBuilder extends Builder {
        private final Method methodFrom;
        private Method methodTo;

        /**
         * Construct a new MethodRouter Builder.
         * @param methodFrom The exposed interface's method from which this is routing.
         */
        public DumbBuilder(Method methodFrom) {
            super(null);
            this.methodFrom = methodFrom;
        }

        /**
         * Sets the name of the destination method.
         * @param methodTo The name of the adapted instance's method to which this is routing.
         * @return The Builder, for fluency.
         */
        public Builder to(Method methodTo) {
            this.methodTo = methodTo;
            return this;
        }

        @Override
        public MethodRouter build(Class<?> classFrom, Class<?> classTo) throws NoSuchMethodException, AdapterException {
            if(this.consumingFrom == null || this.consumingFrom.length == 0) {
                throw new AdapterException("No consuming conversions passed to MethodRouter.Builder.");
            }
            if( this.producingTo == null) {
                throw new AdapterException("Null producing conversion passed to MethodRouter.Builder.");
            }
            if(this.methodTo == null) {
                throw new AdapterException("Empty or null destination method passed to MethodRouter.Builder.");
            }
            if(this.methodFrom == null) {
                throw new AdapterException("Empty or null source method passed to MethodRouter.Builder.");
            }

            return new MethodRouter(
                    this.methodFrom,
                    this.consumingFrom,
                    this.beforeForwarding,
                    this.methodTo,
                    this.producingTo,
                    this.afterForwarding
            );
        }
    }
}
