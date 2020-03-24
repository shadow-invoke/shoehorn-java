package io.shadowstack.service;

import io.shadowstack.model.Topping;
import io.shadowstack.AdapterException;
import io.shadowstack.ArgumentConverter;
import io.shadowstack.model.Calzone;

public class CalzoneFromToppingsConverter implements ArgumentConverter<Topping[], Calzone> {
    public static final CalzoneFromToppingsConverter INSTANCE = new CalzoneFromToppingsConverter();

    @Override
    public Calzone convert(Topping[] from) throws AdapterException {
        Calzone calzone = new Calzone();
        calzone.setFillings(from);
        return calzone;
    }

    @Override
    public void convert(Topping[] from, Calzone to) throws AdapterException {
        to.setFillings(from);
    }
}
