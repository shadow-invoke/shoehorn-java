package io.shadowstack;

public interface MethodForwardingInterceptor {
    /**
     * Intercept a call forwarded by a MethodRouter. This is guaranteed to be called after consumed arguments have been
     * converted and before the produced result is converted, but may be called before or after the actual call to the
     * adapted instance's method has been made. If it returns any non-null value, this will replace or supercede any
     * result returned by the adapted instance.
     * @param inputs The inputs passed by the caller to the exposed interface, converted into a set of arguments
     *               accepted by the adapted instance.
     * @param adaptedInstance The adapted instance itself.
     * @param result If the adapted instance's method has been called, this will be the result (if any) returned by it.
     *               If the adapted instance has not yet been invoked, this will be null.
     * @return A result of the type produced by the adapted instance. If the return type of the method is void, this
     *         value will be ignored. Otherwise, any non-null value will replace whatever would have been returned
     *         by the adapted instance. If this interceptor is called before the method is invoked on the adapted
     *         instance, then a non-null result will prevent that invocation from ever occurring.
     * @throws AdapterException
     */
    Object intercept(final Object[] inputs, final Object adaptedInstance, final Object result) throws AdapterException;
}
