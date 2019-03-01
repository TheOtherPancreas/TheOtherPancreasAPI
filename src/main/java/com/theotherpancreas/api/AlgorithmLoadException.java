package com.theotherpancreas.api;

/**
 * Created by Mike on 1/25/2018.
 */

public class AlgorithmLoadException extends Exception {

    public AlgorithmLoadException() {
        super("Error loading algorithm.");
    }

    public AlgorithmLoadException(Exception cause) {
        super("Error loading algorithm.", cause);
    }

    public AlgorithmLoadException(String message) {
        super(message);
    }
}
