package edu.mit.csail.sdg.squander.errors;

public class ConsistencyError extends RuntimeException {

    private static final long serialVersionUID = 7042856392425745894L;

    public ConsistencyError(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsistencyError(String message) {
        super(message);
    }

}
