package org.shoehorn;

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
    private final Method methodTo;
    private final ArgumentConversion<Object, Object> producingTo;

    public Object forward(Object[] inputs, Object destination) throws AdapterException, InvocationTargetException, IllegalAccessException {
        if(inputs == null || destination == null) {
            throw new AdapterException("Null inputs or destination.");
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
                currentSourceInput = inputs[currentSourceInputIndex++];
            }
            if(currentDestinationInput == null || !currentDestinationInput.getClass().equals(conversion.getTo())) {
                currentDestinationInput = conversion.convert(currentSourceInput);
                destinationInputs.add(currentDestinationInput);
            } else {
                conversion.convert(currentSourceInput, currentDestinationInput);
            }
        }

        Object destinationResult = this.methodTo.invoke(destination, destinationInputs.toArray());
        return this.producingTo.convert(destinationResult);
    }

    public static class Builder {
        private final String methodFrom;
        private String methodTo;
        private ArgumentConversion<Object, Object>[] consumingFrom;
        private ArgumentConversion<Object, Object> producingTo;

        public Builder(String methodFrom) {
            this.methodFrom = methodFrom;
        }

        public Builder to(String methodTo) {
            this.methodTo = methodTo;
            return this;
        }

        public Builder consuming(ArgumentConversion<Object, Object>... conversions) {
            this.consumingFrom = conversions;
            return this;
        }

        public Builder producing(ArgumentConversion<Object, Object> conversion) {
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
                    classTo.getMethod(this.methodTo, uniqueOut.toArray(new Class<?>[uniqueIn.size()])),
                    this.producingTo
            );
        }
    }
}
