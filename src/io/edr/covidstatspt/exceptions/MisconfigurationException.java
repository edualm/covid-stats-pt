package io.edr.covidstatspt.exceptions;

public class MisconfigurationException extends Exception {
    public MisconfigurationException(String errorMessage) {
        super(errorMessage);
    }
}
