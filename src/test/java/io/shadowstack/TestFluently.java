package io.shadowstack;

import io.shadowstack.model.*;
import io.shadowstack.service.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static io.shadowstack.Fluently.reference;
import static io.shadowstack.Fluently.method;
import static io.shadowstack.Fluently.shoehorn;
import static io.shadowstack.Fluently.convert;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"CodeBlock2Expr", "ConstantConditions"})
public class TestFluently {
    @Test
    public void testShoehorn() throws AdapterException, NoSuchMethodException {
        // We're going to make pizza in a gas oven and pass it off as a wood-fired pizza
        GasOven gasOven = new GasOven();
        WoodOven woodOven = shoehorn(gasOven)
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
                                )
                                .build();
        PizzaDTO baked = gasOven.bake(new DoughDTO("LARGE"), new String[]{"PEPPERONI"});
        Pizza expected = PizzaDTOConverter.INSTANCE.convert(baked);
        Pizza cooked = woodOven.cook(new Dough(Size.LARGE), new Topping[]{Topping.PEPPERONI});
        assertEquals(expected, cooked);
        // That worked so well we're now going to microwave a calzone and call that a wood-fired pizza
        Microwave microwave = new Microwave();
        woodOven = shoehorn(microwave)
                        .into(WoodOven.class)
                        .routing(
                                // This time we're going to get all fancy with MethRef-style references.
                                method(
                                    reference(WoodOven.class)
                                        .from(
                                            (oven) -> oven.cook(null, null) // pass whatever
                                        )
                                )
                                    .to(
                                        reference(Microwave.class)
                                            .from(
                                                micro -> micro.heat(null) // pass whatever
                                            )
                                    )
                                    // Example of how multiple inputs of the exposed type can be converted
                                    // into a single input of the adapted instance.
                                    .consuming(
                                            convert(Dough.class)
                                                    .to(Calzone.class)
                                                    .using(CalzoneFromDoughConverter.INSTANCE),
                                            convert(Topping[].class)
                                                    .to(Calzone.class)
                                                    .using(CalzoneFromToppingsConverter.INSTANCE)
                                    )
                                    .producing(
                                            convert(Calzone.class)
                                                    .to(Pizza.class)
                                                    .using(PizzaFromCalzoneConverter.INSTANCE)
                                    )
                        )
                        .build();
        cooked = woodOven.cook(new Dough(Size.LARGE), new Topping[]{Topping.PEPPERONI});
        assertEquals(expected, cooked);
        // Calling a method with no routing defined should generate an exception
        assertThrows(AdapterException.class, woodOven::foo);
    }

    @Test
    public void testBadInputCases() throws AdapterException, NoSuchMethodException {
        // Try null adapted instance
        Adapter.InnerBuilder<WoodOven> builder = shoehorn(null).into(WoodOven.class);
        assertThrows(AdapterException.class, builder::build);
        // Try null exposed type
        builder = shoehorn(new GasOven()).into(null);
        assertThrows(AdapterException.class, builder::build);
        // Try method router with no convert/consume specifications
        final Adapter.InnerBuilder<WoodOven> lambdaBuilder = builder;
        assertThrows(AdapterException.class, () -> {
            lambdaBuilder.routing(method("cook").to("bake"));
        });
        // Try method router with no produce/convert specifications
        assertThrows(AdapterException.class, () -> {
            lambdaBuilder
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
                    );
        });
        // Try method router with empty destination method
        assertThrows(AdapterException.class, () -> {
                    lambdaBuilder
                            .routing(
                                    method("cook")
                                            .to("")
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
                            );
                });
        // Try method router with empty source method
        assertThrows(AdapterException.class, () -> {
                    lambdaBuilder
                            .routing(
                                    method("")
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
                            );
                });
        // Try method router with null destination method
        assertThrows(AdapterException.class, () -> {
            lambdaBuilder
                    .routing(
                            method("cook")
                                    .to(null)
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
                    );
        });
        assertThrows(AdapterException.class, () -> {
            lambdaBuilder
                    .routing(
                            method(
                                    reference(WoodOven.class)
                                            .from(
                                                    (oven) -> oven.cook(null, null) // pass whatever
                                            )
                            )
                            .to((Method)null)
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
                    );
        });
        // Try method router with mismatched source method
        assertThrows(RuntimeException.class, () -> {
            lambdaBuilder
                    .routing(
                            method((Method)null)
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
                    );
        });
        // Try method router with null source method
        assertThrows(AdapterException.class, () -> {
            lambdaBuilder
                    .routing(
                            method((Method)null)
                                .to(
                                    reference(GasOven.class)
                                        .from(
                                            (oven) -> oven.bake(null, null)
                                        )
                                )
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
                    );
        });
        assertThrows(AdapterException.class, () -> {
            lambdaBuilder
                    .routing(
                            method((String)null)
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
                    );
        });
        // Try method router with empty convert/consume handlers
        assertThrows(AdapterException.class, () -> {
            lambdaBuilder
                    .routing(
                        method("cook")
                            .to("")
                            .consuming()
                            .producing(
                                    convert(PizzaDTO.class)
                                            .to(Pizza.class)
                                            .using(PizzaDTOConverter.INSTANCE)
                            )
                    );
        });
        // Try creating a bad conversion handler
        assertThrows(AdapterException.class, () -> {
            Fluently.convert(Dough.class)
                    .to(DoughDTO.class)
                    .using(null);
        });
        // Try forwarding invalid inputs through a valid router
        final MethodRouter router = method("cook")
                                        .to("heat")
                                        .consuming(
                                            convert(Dough.class)
                                                    .to(Calzone.class)
                                                    .using(CalzoneFromDoughConverter.INSTANCE),
                                            convert(Topping[].class)
                                                    .to(Calzone.class)
                                                    .using(CalzoneFromToppingsConverter.INSTANCE)
                                        )
                                        .producing(
                                            convert(Calzone.class)
                                                    .to(Pizza.class)
                                                    .using(PizzaFromCalzoneConverter.INSTANCE)
                                        ).build(WoodOven.class, Microwave.class);
        assertThrows(AdapterException.class, () -> router.forward(null, new Microwave()));
        assertThrows(AdapterException.class, () -> router.forward(new Object[1], null));
    }

    @Test
    public void testRouterBuildsWithRedundantSetters() throws AdapterException, NoSuchMethodException {
        final MethodRouter router = method("cook")
                .to("heat")
                .consuming(
                        convert(Dough.class)
                                .to(Calzone.class)
                                .using(CalzoneFromDoughConverter.INSTANCE),
                        convert(Topping[].class)
                                .to(Calzone.class)
                                .using(CalzoneFromToppingsConverter.INSTANCE)
                )
                .consuming(
                        convert(Dough.class)
                                .to(Calzone.class)
                                .using(CalzoneFromDoughConverter.INSTANCE),
                        convert(Topping[].class)
                                .to(Calzone.class)
                                .using(CalzoneFromToppingsConverter.INSTANCE)
                )
                .producing(
                        convert(Calzone.class)
                                .to(Pizza.class)
                                .using(PizzaFromCalzoneConverter.INSTANCE)
                )
                .producing(
                        convert(Calzone.class)
                                .to(Pizza.class)
                                .using(PizzaFromCalzoneConverter.INSTANCE)
                ).build(WoodOven.class, Microwave.class);
    }
}
