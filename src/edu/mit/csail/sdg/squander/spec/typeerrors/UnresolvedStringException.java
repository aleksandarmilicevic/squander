package edu.mit.csail.sdg.squander.spec.typeerrors;

public class UnresolvedStringException extends TypeCheckException {

    private static final long serialVersionUID = 8711946394349411898L;

    public UnresolvedStringException(String msg, String src) {
        super(msg, src);
    }

    public UnresolvedStringException(String msg, Throwable t) {
        super(msg, t);
    }

    public UnresolvedStringException(String msg) {
        super(msg);
    }

}
