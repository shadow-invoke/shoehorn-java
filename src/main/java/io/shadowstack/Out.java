package io.shadowstack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Out {
    /**
     * Gets the class(es) to which the following return type will be converted.
     * @return The parameterized target class.
     */
    Class<?> to();

    /**
     * Gets the type(s) of the ArgumentConverter(s) used for conversion of the following return type.
     * @return The parameterized ArgumentConverter class.
     */
    Class<? extends ArgumentConverter<?, ?>> with();

    /**
     * The name of the static factory method on the ArgumentConverter class to be invoked to obtain an
     * instance. There's no need to specify this if the factory method on your ArgumentConverter is named
     * either instance() or getInstance(), as these are the conventional names. Alternatively, the
     * singletonInstance member can be used to specify a static memberr.
     * @return The method name (empty String or zero length indicate that name-by-convention should be used).
     */
    String factoryMethod() default "";

    /**
     * The name of the static singleton instance member on the ArgumentConverter class to be accessed for
     * conversions. There's no need to specify this is the member on your ArgumentConverter is named INSTANCE,
     * as this is the conventional name. Alternatively, the factoryMethod member can be used to specify a static
     * factory method name.
     * @return An ordered array of member names (empty Strings or zero length indicate that name-by-convention should be used).
     */
    String singletonMember() default "";
}
