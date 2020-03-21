package org.shoehorn.service;

import org.shoehorn.model.DoughDTO;
import org.shoehorn.model.PizzaDTO;

public class GasOven {
    public PizzaDTO bake(DoughDTO dough, String[] toppingNames) {
        PizzaDTO pizza = new PizzaDTO();
        pizza.setSizeName(dough.getAmountName());
        pizza.setToppingNames(toppingNames);
        return pizza;
    }
}
