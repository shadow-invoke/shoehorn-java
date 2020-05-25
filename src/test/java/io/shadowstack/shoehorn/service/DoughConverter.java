package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.model.Dough;
import io.shadowstack.shoehorn.AdapterException;
import io.shadowstack.shoehorn.ArgumentConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import io.shadowstack.shoehorn.model.DoughDTO;

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
