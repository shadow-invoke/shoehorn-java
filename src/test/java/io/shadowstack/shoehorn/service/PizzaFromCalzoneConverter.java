package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.AdapterException;
import io.shadowstack.shoehorn.ArgumentConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import io.shadowstack.shoehorn.model.Calzone;
import io.shadowstack.shoehorn.model.Pizza;

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
