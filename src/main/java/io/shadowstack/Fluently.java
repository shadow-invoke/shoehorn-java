package io.shadowstack;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;

@UtilityClass
public class Fluently {
    public static Adapter.OuterBuilder shoehorn(Object adapted) {
        return new Adapter.OuterBuilder(adapted);
    }

    public static MethodRouter.Builder method(String name) {
        return new MethodRouter.Builder(name);
    }

    public static MethodRouter.DumbBuilder method(Method method) {
        return new MethodRouter.DumbBuilder(method);
    }

    public static <T> ArgumentConversion.InitialBuilder<T> convert(Class<T> clazz) {
        return new ArgumentConversion.InitialBuilder<>(clazz);
    }

    public static <T> MethodCapture<T> reference(Class<T> clazz) {
        return new MethodCapture<>(clazz);
    }
}
