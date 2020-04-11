package io.shadowstack;

public class AdapterException extends Exception {
    private static final long serialVersionUID = 209722786269383930L;

    public AdapterException(String message) {
        super(message);
    }

    public AdapterException(Throwable t) {
        super(t);
    }

    public AdapterException(String message, Throwable t) {
        super(message, t);
    }
}
