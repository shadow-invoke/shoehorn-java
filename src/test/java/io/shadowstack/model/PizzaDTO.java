package io.shadowstack.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PizzaDTO {
    private String sizeName;
    private String[] toppingNames;
}
