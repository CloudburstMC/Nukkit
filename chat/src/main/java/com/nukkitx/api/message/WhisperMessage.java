package com.nukkitx.api.message;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Nonnull
@Immutable
public final class WhisperMessage implements SourceMessage {
    private static final Type TYPE = Type.WHISPER;
    private final String sender;
    private final String message;
    private final boolean needsTranslating;

    public WhisperMessage(@Nonnull String message) {
        this("", message, false);
    }

    public WhisperMessage(@Nonnull String sender, @Nonnull String message) {
        this(sender, message, false);
    }

    public WhisperMessage(@Nonnull String message, boolean needsTranslating) {
        this("", message, needsTranslating);
    }

    public WhisperMessage(@Nonnull String sender, @Nonnull String message, boolean needsTranslating) {
        this.sender = Objects.requireNonNull(sender, "sender");
        this.message = Objects.requireNonNull(message, "message");
        this.needsTranslating = needsTranslating;
    }

    /**
     * Returns the message.
     *
     * @return the message
     */
    @Nonnull
    public final String getMessage() {
        return message;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public boolean needsTranslating() {
        return needsTranslating;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || WhisperMessage.class != o.getClass()) return false;
        WhisperMessage that = (WhisperMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public final String toString() {
        return "WhisperMessage{message='" + message + '\'' +
                "sender='" + sender + '\'' +
                '}';
    }

    @Override
    public String getSender() {
        return sender;
    }
}
