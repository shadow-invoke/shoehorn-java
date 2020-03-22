package org.shoehorn.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.shoehorn.AdapterException;
import org.shoehorn.ArgumentConverter;
import org.shoehorn.model.Calzone;
import org.shoehorn.model.Dough;

@Mapper
public interface CalzoneFromDoughConverter extends ArgumentConverter<Dough, Calzone> {
    CalzoneFromDoughConverter INSTANCE = Mappers.getMapper(CalzoneFromDoughConverter.class);

    @Override
    @Mapping(target = "size", source = "amount")
    Calzone convert(Dough from) throws AdapterException;

    @Override
    @Mapping(target = "size", source = "amount")
    void convert(Dough from, @MappingTarget Calzone to) throws AdapterException;
}
