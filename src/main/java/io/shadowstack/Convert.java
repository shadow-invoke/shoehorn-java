package io.shadowstack;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Convert {
    /**
     * Gets the class to which the following argument or return type will be converted
     * @return The parameterized class
     */
    Class<?> to();

    /**
     * Gets the type of the ArgumentConverter used for this conversion
     * @return The parameterized ArgumentConverter class
     */
    Class<? extends ArgumentConverter<?, ?>> use();

    /**
     * The name of the static factory method on the ArgumentConverter class to be invoked to obtain an instance
     * @return The name of the method (default empty String indicates that name-by-convention should be used)
     */
    String factoryMethod() default "";

    /**
     * The name of the static singleton instance member on the ArgumentConverter class to be accessed for conversions
     * @return The name of the member (default empty String indicates that name-by-convention should be used)
     */
    String singletonInstance() default "";
}
