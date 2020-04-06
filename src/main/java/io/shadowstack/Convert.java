package io.shadowstack;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Convert {
    Class<?> to();
    Class<? extends ArgumentConverter<?, ?>> use();
}
