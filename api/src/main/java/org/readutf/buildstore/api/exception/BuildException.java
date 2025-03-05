package org.readutf.buildstore.api.exception;

import org.jspecify.annotations.NonNull;

public class BuildException extends Exception{

    private final @NonNull String userMessage;

    public BuildException(@NonNull String userMessage) {
        this.userMessage = userMessage;
    }

    public BuildException(String message, @NonNull String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    public BuildException(String message, Throwable cause, @NonNull String userMessage) {
        super(message, cause);
        this.userMessage = userMessage;
    }

    public BuildException(Throwable cause, @NonNull String userMessage) {
        super(cause);
        this.userMessage = userMessage;
    }

    public BuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, @NonNull String userMessage) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.userMessage = userMessage;
    }

    public @NonNull String getUserMessage() {
        return userMessage;
    }

}
