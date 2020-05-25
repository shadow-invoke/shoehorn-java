package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.model.Dough;
import io.shadowstack.shoehorn.model.Topping;
import io.shadowstack.shoehorn.model.Pizza;

public class WoodOven {
    public Pizza cook(Dough dough, Topping[] toppings) {
        Pizza pizza = new Pizza();
        pizza.setSize(dough.getAmount());
        pizza.setToppings(toppings);
        return pizza;
    }

    @SuppressWarnings({"UnusedReturnValue", "SameReturnValue"})
    public String foo() {
        return "bar";
    }
}
