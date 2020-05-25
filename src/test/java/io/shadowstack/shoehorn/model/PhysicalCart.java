package io.shadowstack.shoehorn.model;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class PhysicalCart {
    private List<PhysicalProduct> products = new ArrayList<>();
}
