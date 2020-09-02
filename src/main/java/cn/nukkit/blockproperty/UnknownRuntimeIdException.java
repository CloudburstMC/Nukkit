package cn.nukkit.blockproperty;

public class UnknownRuntimeIdException extends IllegalStateException {
    public UnknownRuntimeIdException() {
    }

    public UnknownRuntimeIdException(String s) {
        super(s);
    }

    public UnknownRuntimeIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownRuntimeIdException(Throwable cause) {
        super(cause);
    }
}
