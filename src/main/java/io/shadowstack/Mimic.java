package io.shadowstack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mimic {
    /**
     * The type containing the method being mimicked; the exposed interface type.
     * @return Parameterized class of exposed interface type.
     */
    Class<?> type();

    /**
     * The name of the method within the exposed interface type to be mimicked by the annotated method.
     * @return The unqualified, simple name of the mimicked method.
     */
    String method();
}
