package com.isyb.obd.initialization_components.exceptions;

public class StopStartup extends RuntimeException {
    public StopStartup() {
        super();
    }

    public StopStartup(Throwable cause) {
        super(cause);
    }

    public StopStartup(String message) {
        super(message);
    }


    public StopStartup(String message, Throwable cause) {
        super(message, cause);
    }

}
