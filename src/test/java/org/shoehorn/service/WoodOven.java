package org.shoehorn.service;

import org.shoehorn.model.Dough;
import org.shoehorn.model.Pizza;
import org.shoehorn.model.Topping;

public class WoodOven {
    public Pizza cook(Dough dough, Topping[] toppings) {
        Pizza pizza = new Pizza();
        pizza.setSize(dough.getAmount());
        pizza.setToppings(toppings);
        return pizza;
    }
}
