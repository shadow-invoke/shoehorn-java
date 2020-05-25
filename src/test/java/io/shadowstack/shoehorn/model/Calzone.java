package io.shadowstack.shoehorn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calzone {
    private Size size;
    private Topping[] fillings;
    private Temperature temperature = Temperature.FROZEN;
}
