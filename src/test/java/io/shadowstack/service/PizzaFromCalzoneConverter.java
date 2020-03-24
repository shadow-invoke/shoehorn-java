package io.shadowstack.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import io.shadowstack.AdapterException;
import io.shadowstack.ArgumentConverter;
import io.shadowstack.model.Calzone;
import io.shadowstack.model.Pizza;

@Mapper
public interface PizzaFromCalzoneConverter extends ArgumentConverter<Calzone, Pizza> {
    PizzaFromCalzoneConverter INSTANCE = Mappers.getMapper(PizzaFromCalzoneConverter.class);

    @Override
    @Mapping(target = "size", source = "size")
    @Mapping(target = "toppings", source = "fillings")
    Pizza convert(Calzone from) throws AdapterException;

    @Override
    @Mapping(target = "size", source = "size")
    @Mapping(target = "toppings", source = "fillings")
    void convert(Calzone from, @MappingTarget Pizza to) throws AdapterException;
}
