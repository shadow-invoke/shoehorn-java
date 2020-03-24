package io.shadowstack.service;

import io.shadowstack.model.Dough;
import io.shadowstack.model.Topping;
import io.shadowstack.model.Pizza;

public class WoodOven {
    public Pizza cook(Dough dough, Topping[] toppings) {
        Pizza pizza = new Pizza();
        pizza.setSize(dough.getAmount());
        pizza.setToppings(toppings);
        return pizza;
    }

    public String foo() {
        return "bar";
    }
}
