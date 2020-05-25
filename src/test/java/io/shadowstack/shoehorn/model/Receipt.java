package io.shadowstack.shoehorn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    private double total;
    private String cashierName;
}
