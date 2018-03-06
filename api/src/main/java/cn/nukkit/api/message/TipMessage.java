package cn.nukkit.api.message;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Nonnull
@Immutable
public final class TipMessage implements Message {
    private static final Type TYPE = Type.TIP;
    private final String message;
    private final boolean needsTranslating;

    public TipMessage(@Nonnull String message) {
        this(message, false);
    }

    public TipMessage(@Nonnull String message, boolean needsTranslating) {
        this.message = Preconditions.checkNotNull(message, "message");
        this.needsTranslating = needsTranslating;
    }

    /**
     * Returns the message.
     *
     * @return message
     */
    @Override
    public String getMessage() {
        return null;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || TipMessage.class != o.getClass()) return false;
        TipMessage that = (TipMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "TipMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
