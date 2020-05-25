package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.model.Topping;
import io.shadowstack.shoehorn.AdapterException;
import io.shadowstack.shoehorn.ArgumentConverter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ToppingsConverter extends ArgumentConverter<Topping[], String[]> {
    ToppingsConverter INSTANCE = Mappers.getMapper(ToppingsConverter.class);

    @Override
    String[] convert(Topping[] from) throws AdapterException;

    @Override
    void convert(Topping[] from, @MappingTarget String[] to) throws AdapterException;
}
