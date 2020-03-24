package io.shadowstack.service;

import io.shadowstack.model.Dough;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import io.shadowstack.AdapterException;
import io.shadowstack.ArgumentConverter;
import io.shadowstack.model.DoughDTO;

@Mapper
public interface DoughConverter extends ArgumentConverter<Dough, DoughDTO> {
    DoughConverter INSTANCE = Mappers.getMapper(DoughConverter.class);

    @Override
    @Mapping(target = "amountName", source = "amount")
    DoughDTO convert(Dough from) throws AdapterException;

    @Override
    @Mapping(target = "amountName", source = "amount")
    void convert(Dough from, @MappingTarget DoughDTO to) throws AdapterException;
}
