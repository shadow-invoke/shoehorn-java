package org.shoehorn.service;

import org.shoehorn.AdapterException;
import org.shoehorn.ArgumentConverter;
import org.shoehorn.model.Calzone;
import org.shoehorn.model.Topping;

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
