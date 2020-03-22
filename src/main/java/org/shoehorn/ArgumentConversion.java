package org.shoehorn;

import lombok.*;

@Data
@AllArgsConstructor
public class ArgumentConversion<FC, TC> {
    private final Class<FC> from;
    private final Class<TC> to;
    private final ArgumentConverter<FC, TC> argumentConverter;

    public TC convert(FC from) throws AdapterException {
        return argumentConverter.convert(from);
    }

    public void convert(FC from, TC to) throws AdapterException {
        argumentConverter.convert(from, to);
    }

    public static class Builder {
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Initial<FI> {
            private Class<FI> from;

            public Initial(Class<FI> from) {
                this.from = from;
            }

            public <T> Final<FI, T> to(Class<T> to) {
                return new Final<>(this.from, to);
            }
        }

        @AllArgsConstructor
        public static class Final<FF, TF> {
            private Class<FF> from;
            private Class<TF> to;

            public ArgumentConversion<FF, TF> with(ArgumentConverter<FF, TF> argumentConverter) throws AdapterException {
                if (argumentConverter == null) {
                    throw new AdapterException("Null argumentConverter.");
                }
                return new ArgumentConversion<>(this.from, this.to, argumentConverter);
            }
        }
    }
}
