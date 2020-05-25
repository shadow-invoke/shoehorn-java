package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.AdapterException;
import io.shadowstack.shoehorn.ArgumentConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import io.shadowstack.shoehorn.model.Pizza;
import io.shadowstack.shoehorn.model.PizzaDTO;

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
