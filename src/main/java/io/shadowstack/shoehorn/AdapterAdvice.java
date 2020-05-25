package io.shadowstack.shoehorn;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AdapterAdvice.List.class)
public @interface AdapterAdvice {
    Pointcut pointcut();
    Class<? extends MethodForwardingInterceptor> interceptor();
    // TODO: More pointcuts like "before adapted instance call", "after result conversion", etc.
    static enum Pointcut {
        BEFORE,
        AFTER
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        AdapterAdvice[] value();
    }
}
