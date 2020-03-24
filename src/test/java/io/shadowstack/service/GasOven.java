package io.shadowstack.service;

import io.shadowstack.model.DoughDTO;
import io.shadowstack.model.PizzaDTO;

public class GasOven {
    public PizzaDTO bake(DoughDTO dough, String[] toppingNames) {
        PizzaDTO pizza = new PizzaDTO();
        pizza.setSizeName(dough.getAmountName());
        pizza.setToppingNames(toppingNames);
        return pizza;
    }
}
