package cn.nukkit.api.message;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Nonnull
@Immutable
public class SystemMessage implements Message {
    private static final Type type = Type.SYSTEM;
    private final String message;

    public SystemMessage(@Nonnull String message) {
        Preconditions.checkNotNull(message, "message");
        this.message = message;
    }

    /**
     * Returns the message.
     * @return message
     */
    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemMessage that = (SystemMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "SystemMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
