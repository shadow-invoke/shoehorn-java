package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.model.DoughDTO;
import io.shadowstack.shoehorn.model.PizzaDTO;

public class GasOven {
    public PizzaDTO bake(DoughDTO dough, String[] toppingNames) {
        PizzaDTO pizza = new PizzaDTO();
        pizza.setSizeName(dough.getAmountName());
        pizza.setToppingNames(toppingNames);
        return pizza;
    }
}
