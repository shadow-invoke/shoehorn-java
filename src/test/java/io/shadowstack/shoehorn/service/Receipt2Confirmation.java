package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.AdapterException;
import io.shadowstack.shoehorn.ArgumentConverter;
import io.shadowstack.shoehorn.model.Confirmation;
import io.shadowstack.shoehorn.model.Receipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface Receipt2Confirmation extends ArgumentConverter<Receipt, Confirmation> {
    Receipt2Confirmation INSTANCE = Mappers.getMapper(Receipt2Confirmation.class);

    @Override
    @Mapping(target = "total", source = "total")
    Confirmation convert(Receipt from) throws AdapterException;

    @Override
    @Mapping(target = "total", source = "total")
    void convert(Receipt from, @MappingTarget Confirmation to) throws AdapterException;
}
