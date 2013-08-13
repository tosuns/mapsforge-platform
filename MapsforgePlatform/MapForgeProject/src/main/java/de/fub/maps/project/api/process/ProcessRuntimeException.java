/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.api.process;

/**
 *
 * @author Serdar
 */
public class ProcessRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProcessRuntimeException() {
    }

    public ProcessRuntimeException(String message) {
        super(message);
    }

    public ProcessRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessRuntimeException(Throwable cause) {
        super(cause);
    }

    public ProcessRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
