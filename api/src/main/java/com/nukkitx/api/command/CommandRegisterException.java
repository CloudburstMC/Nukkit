package com.nukkitx.api.command;

public class CommandRegisterException extends CommandException {

    public CommandRegisterException() {
        super();
    }

    public CommandRegisterException(String message) {
        super(message);
    }

    public CommandRegisterException(Throwable cause) {
        super(cause);
    }

    public CommandRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandRegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
