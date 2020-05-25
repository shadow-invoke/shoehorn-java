package io.shadowstack.shoehorn.model;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class VirtualCart {
    private List<VirtualProduct> products = new ArrayList<>();
}
