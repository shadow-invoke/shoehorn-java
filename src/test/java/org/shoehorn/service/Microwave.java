package org.shoehorn.service;

import org.shoehorn.model.Calzone;
import org.shoehorn.model.Temperature;

public class Microwave {
    public Calzone heat(Calzone calzone) {
        calzone.setTemperature(Temperature.SCALDING);
        return calzone;
    }
}
