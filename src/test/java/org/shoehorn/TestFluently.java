package org.shoehorn;

import org.junit.Test;
import org.shoehorn.model.*;
import org.shoehorn.service.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.shoehorn.Fluently.*;

public class TestFluently {
    @Test
    public void testNone() throws AdapterException, NoSuchMethodException {
        GasOven gasOven = new GasOven();
        WoodOven woodOven = shoehorn(gasOven)
                                .into(WoodOven.class)
                                .routing(
                                        method("cook")
                                                .to("bake")
                                                .consuming(
                                                        convert(Dough.class)
                                                                .to(DoughDTO.class)
                                                                .with(DoughConverter.INSTANCE),
                                                        convert(Topping[].class)
                                                                .to(String[].class)
                                                                .with(ToppingsConverter.INSTANCE)
                                                )
                                                .producing(
                                                        convert(PizzaDTO.class)
                                                                .to(Pizza.class)
                                                                .with(PizzaDTOConverter.INSTANCE)
                                                )
                                )
                                .build();
        PizzaDTO baked = gasOven.bake(new DoughDTO("LARGE"), new String[]{"PEPPERONI"});
        Pizza expected = PizzaDTOConverter.INSTANCE.convert(baked);
        Pizza cooked = woodOven.cook(new Dough(Size.LARGE), new Topping[]{Topping.PEPPERONI});
        assertEquals(expected, cooked);
    }
}
