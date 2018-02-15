package cn.nukkit.api.message;

import jdk.nashorn.internal.ir.annotations.Immutable;

import javax.annotation.Nonnull;
import java.util.Objects;

@Nonnull
@Immutable
public class ChatMessage implements Message {
    private static final Type type = Type.CHAT;
    private final String message;


    public ChatMessage(@Nonnull String message) {
        this.message = message;
    }

    /**
     * Returns the message.
     * @return the message
     */
    @Nonnull
    public final String getMessage() {
        return message;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public final String toString() {
        return "PopupMessage{message='" + message + '\'' + '}';
    }
}
