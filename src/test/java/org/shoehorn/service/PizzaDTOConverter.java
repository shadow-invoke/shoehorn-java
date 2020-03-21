package org.shoehorn.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.shoehorn.AdapterException;
import org.shoehorn.ArgumentConverter;
import org.shoehorn.model.Pizza;
import org.shoehorn.model.PizzaDTO;

@Mapper
public interface PizzaDTOConverter extends ArgumentConverter<PizzaDTO, Pizza> {
    PizzaDTOConverter INSTANCE = Mappers.getMapper(PizzaDTOConverter.class);

    @Override
    @Mapping(target = "toppings", source = "toppingNames")
    @Mapping(target = "size", source = "sizeName")
    Pizza convert(PizzaDTO from) throws AdapterException;

    @Override
    @Mapping(target = "toppings", source = "toppingNames")
    @Mapping(target = "size", source = "sizeName")
    void convert(PizzaDTO from, @MappingTarget Pizza to) throws AdapterException;
}
