package io.shadowstack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface In {
    /**
     * Gets the class(es) from which the following argument will be converted.
     * @return The parameterized classes in an ordered array.
     */
    Class<?>[] from();

    /**
     * Gets the type(s) of the ArgumentConverter(s) used for conversion of the following argument.
     * @return The parameterized ArgumentConverter classes in an ordered array.
     */
    Class<? extends ArgumentConverter<?, ?>>[] with();

    /**
     * The name(s) of the static factory method(s) on the ArgumentConverter class(es) to be invoked to obtain an
     * instance. There's no need to specify this if the factory method on your ArgumentConverter(s) is named
     * either instance() or getInstance(), as these are the conventional names. Alternatively, the
     * singletonInstances member can be used to specify a static member per ArgumentConverter.
     * @return An ordered array of method names (empty Strings or zero length indicate that name-by-convention should be used).
     */
    String[] factoryMethods() default {};

    /**
     * The name(s) of the static singleton instance member(s) on the ArgumentConverter class(es) to be accessed for
     * conversions. There's no need to specify this is the member on your ArgumentConverter(s) is named INSTANCE,
     * as this is the conventional name. Alternatively, the factoryMethods member can be used to specify a static
     * factory method per ArgumentConverter.
     * @return An ordered array of member names (empty Strings or zero length indicate that name-by-convention should be used).
     */
    String[] singletonMembers() default {};
}
