package io.shadowstack.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Pizza {
    private Size size;
    private Topping[] toppings;
}
