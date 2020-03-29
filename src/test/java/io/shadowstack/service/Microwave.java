package io.shadowstack.service;

import io.shadowstack.model.Calzone;
import io.shadowstack.model.Temperature;

public class Microwave {
    @SuppressWarnings("UnusedReturnValue")
    public Calzone heat(Calzone calzone) {
        calzone.setTemperature(Temperature.SCALDING);
        return calzone;
    }
}
