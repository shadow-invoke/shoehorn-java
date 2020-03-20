package org.shoehorn;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

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

        @Slf4j
        @AllArgsConstructor
        public static class Final<FF, TF> {
            private Class<FF> from;
            private Class<TF> to;

            public ArgumentConversion<FF, TF> with(ArgumentConverter<FF, TF> argumentConverter) {
                if (argumentConverter == null) {
                    log.warn("Null argumentConverter, returning null conversion.");
                    return null;
                }
                if (this.from == null || this.to == null) {
                    String msg = "Bad from class (%s) or to class (%s), returning null conversion.";
                    log.warn(String.format(msg, this.from, this.to));
                    return null;
                }
                return new ArgumentConversion<>(this.from, this.to, argumentConverter);
            }
        }
    }
}
