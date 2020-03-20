package org.shoehorn;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Fluently {
    public static Adapter.OuterBuilder shoehorn(Object adapted) {
        return new Adapter.OuterBuilder(adapted);
    }
}
