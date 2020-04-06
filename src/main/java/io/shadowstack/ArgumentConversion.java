package io.shadowstack;

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

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class InitialBuilder<FI> {
        private Class<FI> from;

        public InitialBuilder(Class<FI> from) {
            this.from = from;
        }

        public <T> FinalBuilder<FI, T> to(Class<T> to) {
            return new FinalBuilder<>(this.from, to);
        }
    }

    @AllArgsConstructor
    public static class FinalBuilder<FF, TF> {
        private Class<FF> from;
        private Class<TF> to;

        public ArgumentConversion<FF, TF> using(ArgumentConverter<FF, TF> argumentConverter) throws AdapterException {
            if (argumentConverter == null) {
                throw new AdapterException("Null argumentConverter.");
            }
            return new ArgumentConversion<>(this.from, this.to, argumentConverter);
        }
    }
}
