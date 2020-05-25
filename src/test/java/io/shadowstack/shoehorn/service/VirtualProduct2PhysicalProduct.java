package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.AdapterException;
import io.shadowstack.shoehorn.ArgumentConverter;
import io.shadowstack.shoehorn.model.PhysicalProduct;
import io.shadowstack.shoehorn.model.VirtualProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface VirtualProduct2PhysicalProduct extends ArgumentConverter<VirtualProduct, PhysicalProduct> {
    @Override
    PhysicalProduct convert(VirtualProduct from) throws AdapterException;

    @Override
    void convert(VirtualProduct from, @MappingTarget PhysicalProduct to) throws AdapterException;
}
