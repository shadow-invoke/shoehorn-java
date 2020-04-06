package io.shadowstack;

import io.shadowstack.model.*;
import io.shadowstack.service.*;
import org.junit.jupiter.api.Test;
import static io.shadowstack.Fluently.method;
import static io.shadowstack.Fluently.shoehorn;
import static io.shadowstack.Fluently.convert;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMethodForwardingInterceptor {
    @Test
    public void testAfter() throws AdapterException, NoSuchMethodException {
        // We're going to make pizza in a gas oven and pass it off as a wood-fired pizza
        GasOven gasOven = new GasOven();
        WoodOven woodOven = shoehorn(gasOven)
                .into(WoodOven.class)
                .routing(
                        Fluently.method("cook")
                                .to("bake")
                                .consuming(
                                        convert(Dough.class)
                                                .to(DoughDTO.class)
                                                .using(DoughConverter.INSTANCE),
                                        convert(Topping[].class)
                                                .to(String[].class)
                                                .using(ToppingsConverter.INSTANCE)
                                )
                                .producing(
                                        convert(PizzaDTO.class)
                                                .to(Pizza.class)
                                                .using(PizzaDTOConverter.INSTANCE)
                                )
                                .after((inputs, instance, result) -> {
                                    // You're getting sausage whether you ordered it or not
                                    ((PizzaDTO)result).setToppingNames(new String[]{"PEPPERONI", "SAUSAGE"});
                                    return result;
                                })
                )
                .build();
        Pizza cooked = woodOven.cook(new Dough(Size.LARGE), new Topping[]{Topping.PEPPERONI});
        // Confirm that our interceptor added sausage
        PizzaDTO baked = gasOven.bake(new DoughDTO("LARGE"), new String[]{"PEPPERONI", "SAUSAGE"});
        Pizza expected = PizzaDTOConverter.INSTANCE.convert(baked);
        assertEquals(expected, cooked);
    }

    @Test
    public void testBefore() throws AdapterException, NoSuchMethodException {
        // We're going to make pizza in a gas oven and pass it off as a wood-fired pizza
        GasOven gasOven = new GasOven();
        WoodOven woodOven = Fluently.shoehorn(gasOven)
                .into(WoodOven.class)
                .routing(
                        method("cook")
                                .to("bake")
                                .consuming(
                                        convert(Dough.class)
                                                .to(DoughDTO.class)
                                                .using(DoughConverter.INSTANCE),
                                        convert(Topping[].class)
                                                .to(String[].class)
                                                .using(ToppingsConverter.INSTANCE)
                                )
                                .producing(
                                        convert(PizzaDTO.class)
                                                .to(Pizza.class)
                                                .using(PizzaDTOConverter.INSTANCE)
                                )
                                .before((inputs, instance, result) -> {
                                    // Whatever you ordered, Hawaiian is better
                                    return new PizzaDTO("MEDIUM", new String[]{"PINEAPPLE", "HAM"});
                                })
                )
                .build();
        Pizza cooked = woodOven.cook(new Dough(Size.LARGE), new Topping[]{Topping.PEPPERONI});
        // Confirm that our interceptor superceded the adapted instance's result
        PizzaDTO baked = gasOven.bake(new DoughDTO("MEDIUM"), new String[]{"PINEAPPLE", "HAM"});
        Pizza expected = PizzaDTOConverter.INSTANCE.convert(baked);
        assertEquals(expected, cooked);
    }
}
