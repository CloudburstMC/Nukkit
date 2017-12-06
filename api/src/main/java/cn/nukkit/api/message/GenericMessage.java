package cn.nukkit.api.message;

import jdk.nashorn.internal.ir.annotations.Immutable;

import javax.annotation.Nonnull;
import java.util.Objects;

@Nonnull
@Immutable
public class GenericMessage implements Message {
    private final String message;


    public GenericMessage(@Nonnull String message) {
        this.message = message;
    }

    /**
     * Returns this message.
     * @return the message
     */
    @Nonnull
    public final String getMessage() {
        return message;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericMessage that = (GenericMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(caption, message);
    }

    @Override
    public final String toString() {
        return "PopupMessage{message='" + message + '\'' + '}';
    }
}
