package cn.nukkit.api.message;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Objects;

/**
 * A popup message, including a caption and message.
 */
@Immutable
public final class PopupMessage implements ParameterMessage {
    /**
     * The caption of the message.
     */
    private final String caption;
    /**
     * The message this popup is using.
     */
    private final String message;

    /**
     * Constructs a popup message.
     * @param caption the caption to use for this popup
     * @param message the message to display in the popup
     */
    public PopupMessage(@Nonnull String caption, @Nonnull String message) {
        this.caption = Preconditions.checkNotNull(caption, "caption");
        this.message = Preconditions.checkNotNull(message, "message");
    }

    /**
     * Returns this popup's caption.
     * @return the caption
     */
    @Nonnull
    public final String getCaption() {
        return caption;
    }

    /**
     * Returns this popup's message.
     * @return the message
     */
    @Nonnull
    public final String getMessage() {
        return message;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopupMessage that = (PopupMessage) o;
        return Objects.equals(caption, that.caption) &&
                Objects.equals(message, that.message);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(caption, message);
    }

    @Override
    public final String toString() {
        return "PopupMessage{" +
                "caption='" + caption + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public Collection<String> getParameters() {
        return null;
    }
}
