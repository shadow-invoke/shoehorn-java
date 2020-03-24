package io.shadowstack.service;

import io.shadowstack.model.Calzone;
import io.shadowstack.model.Dough;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import io.shadowstack.AdapterException;
import io.shadowstack.ArgumentConverter;

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
