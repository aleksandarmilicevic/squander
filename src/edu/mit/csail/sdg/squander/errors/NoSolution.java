package edu.mit.csail.sdg.squander.errors;

public class NoSolution extends RuntimeException {

    private static final long serialVersionUID = 4763274745854211377L;

    public NoSolution(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSolution(String message) {
        super(message);
    }
    
    public NoSolution() {
        super();
    }
}
