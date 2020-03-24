package io.shadowstack.service;

import io.shadowstack.model.Topping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import io.shadowstack.AdapterException;
import io.shadowstack.ArgumentConverter;

@Mapper
public interface ToppingsConverter extends ArgumentConverter<Topping[], String[]> {
    ToppingsConverter INSTANCE = Mappers.getMapper(ToppingsConverter.class);

    @Override
    String[] convert(Topping[] from) throws AdapterException;

    @Override
    void convert(Topping[] from, @MappingTarget String[] to) throws AdapterException;
}
