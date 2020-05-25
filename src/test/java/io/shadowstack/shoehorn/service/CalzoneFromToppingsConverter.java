package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.model.Topping;
import io.shadowstack.shoehorn.AdapterException;
import io.shadowstack.shoehorn.ArgumentConverter;
import io.shadowstack.shoehorn.model.Calzone;

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
