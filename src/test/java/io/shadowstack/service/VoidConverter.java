package io.shadowstack.service;

import io.shadowstack.AdapterException;
import io.shadowstack.ArgumentConverter;

public class VoidConverter implements ArgumentConverter<Void, Void> {
    public static VoidConverter INSTANCE = new VoidConverter();

    @Override
    public Void convert(Void from) throws AdapterException {
        return null;
    }

    @Override
    public void convert(Void from, Void to) throws AdapterException {}
}
