package org.shoehorn.service;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.shoehorn.AdapterException;
import org.shoehorn.ArgumentConverter;
import org.shoehorn.model.Topping;

@Mapper
public interface ToppingsConverter extends ArgumentConverter<Topping[], String[]> {
    ToppingsConverter INSTANCE = Mappers.getMapper(ToppingsConverter.class);

    @Override
    String[] convert(Topping[] from) throws AdapterException;

    @Override
    void convert(Topping[] from, @MappingTarget String[] to) throws AdapterException;
}
