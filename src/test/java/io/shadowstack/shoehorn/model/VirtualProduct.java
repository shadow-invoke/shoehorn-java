package io.shadowstack.shoehorn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VirtualProduct {
    private String name;
    private double price;
    private int warehouseId;
    private int customerId;
}
