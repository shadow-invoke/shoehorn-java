package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.model.Calzone;
import io.shadowstack.shoehorn.model.Dough;
import io.shadowstack.shoehorn.AdapterException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import io.shadowstack.shoehorn.ArgumentConverter;

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
