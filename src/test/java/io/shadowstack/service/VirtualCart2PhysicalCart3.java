package io.shadowstack.service;

import io.shadowstack.AdapterException;
import io.shadowstack.ArgumentConverter;
import io.shadowstack.model.PhysicalCart;
import io.shadowstack.model.VirtualCart;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {VirtualProduct2PhysicalProduct.class})
public interface VirtualCart2PhysicalCart3 extends ArgumentConverter<VirtualCart, PhysicalCart> {
    @Override
    PhysicalCart convert(VirtualCart from) throws AdapterException;

    @Override
    void convert(VirtualCart from, @MappingTarget PhysicalCart to) throws AdapterException;

    static ArgumentConverter<?, ?> getSingleton() {
        return Mappers.getMapper(VirtualCart2PhysicalCart.class);
    }
}
