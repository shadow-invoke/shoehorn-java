package org.shoehorn;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Fluently {
    public static Adapter.OuterBuilder shoehorn(Object adapted) {
        return new Adapter.OuterBuilder(adapted);
    }

    public static MethodRouter.Builder method(String name) {
        return new MethodRouter.Builder(name);
    }

    public static <T> ArgumentConversion.InitialBuilder<T> convert(Class<T> clazz) {
        return new ArgumentConversion.InitialBuilder<>(clazz);
    }
}
