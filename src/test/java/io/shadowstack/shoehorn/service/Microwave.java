package io.shadowstack.shoehorn.service;

import io.shadowstack.shoehorn.model.Calzone;
import io.shadowstack.shoehorn.model.Temperature;

public class Microwave {
    @SuppressWarnings("UnusedReturnValue")
    public Calzone heat(Calzone calzone) {
        calzone.setTemperature(Temperature.SCALDING);
        return calzone;
    }
}
