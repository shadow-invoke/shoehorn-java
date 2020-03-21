package org.shoehorn.model;

import lombok.Data;

@Data
public class PizzaDTO {
    private String sizeName;
    private String[] toppingNames;
}
