package io.shadowstack.service;

import io.shadowstack.AdapterException;
import io.shadowstack.ArgumentConverter;
import io.shadowstack.model.PhysicalProduct;
import io.shadowstack.model.VirtualProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.lang.invoke.MethodHandles;

@Mapper
public interface VirtualProduct2PhysicalProduct extends ArgumentConverter<VirtualProduct, PhysicalProduct> {
    VirtualProduct2PhysicalProduct INSTANCE = Mappers.getMapper(VirtualProduct2PhysicalProduct.class);

    @Override
    PhysicalProduct convert(VirtualProduct from) throws AdapterException;

    @Override
    void convert(VirtualProduct from, @MappingTarget PhysicalProduct to) throws AdapterException;

    static ArgumentConverter<?, ?> instance() {
        return (ArgumentConverter<?, ?>) Mappers.getMapper(MethodHandles.lookup().lookupClass());
    }
}
