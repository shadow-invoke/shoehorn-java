package io.shadowstack.shoehorn;

import io.shadowstack.shoehorn.model.*;
import io.shadowstack.shoehorn.service.*;
import org.junit.jupiter.api.Test;
import static io.shadowstack.shoehorn.Fluently.method;
import static io.shadowstack.shoehorn.Fluently.shoehorn;
import static io.shadowstack.shoehorn.Fluently.convert;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMethodForwardingInterceptor {
    @Test
    public void testAfter() throws AdapterException, NoSuchMethodException {
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

    @Test
    public void testRouterBuildsWithRedundantBefore() throws AdapterException, NoSuchMethodException {
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
                                    return new PizzaDTO("MEDIUM", new String[]{"PINEAPPLE", "HAM"});
                                })
                                .before((inputs, instance, result) -> {
                                    return new PizzaDTO("MEDIUM", new String[]{"PEPPERONI", "SAUSAGE"});
                                })
                )
                .build();
        Pizza cooked = woodOven.cook(new Dough(Size.LARGE), new Topping[]{Topping.PEPPERONI});
        // Confirm that our latest interceptor superceded the adapted instance's result
        PizzaDTO baked = gasOven.bake(new DoughDTO("MEDIUM"), new String[]{"PEPPERONI", "SAUSAGE"});
        Pizza expected = PizzaDTOConverter.INSTANCE.convert(baked);
        assertEquals(expected, cooked);
    }

    @Test
    public void testRouterBuildsWithRedundantAfter() throws AdapterException, NoSuchMethodException {
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
                                .after((inputs, instance, result) -> {
                                    return new PizzaDTO("MEDIUM", new String[]{"PEPPERONI", "SAUSAGE"});
                                })
                                .after((inputs, instance, result) -> {
                                    return new PizzaDTO("MEDIUM", new String[]{"PINEAPPLE", "HAM"});
                                })
                )
                .build();
        Pizza cooked = woodOven.cook(new Dough(Size.LARGE), new Topping[]{Topping.PEPPERONI});
        // Confirm that our latest interceptor superceded the adapted instance's result
        PizzaDTO baked = gasOven.bake(new DoughDTO("MEDIUM"), new String[]{"PINEAPPLE", "HAM"});
        Pizza expected = PizzaDTOConverter.INSTANCE.convert(baked);
        assertEquals(expected, cooked);
    }
}
