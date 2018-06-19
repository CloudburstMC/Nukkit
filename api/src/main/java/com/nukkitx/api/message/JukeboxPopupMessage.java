package com.nukkitx.api.message;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Nonnull
@Immutable
public final class JukeboxPopupMessage implements ParameterMessage {
    private static final Type TYPE = Type.JUKEBOX_POPUP;
    /**
     * The message.
     */
    private final String message;
    /**
     * The captions for the message.
     */
    private final String[] captions;
    /**
     * Whether the message will need to be translated by the client.
     */
    private final boolean needsTranslating;

    /**
     * Constructs a popup message.
     *
     * @param message  the message to display in the popup
     * @param captions the captions to use for this popup
     */
    public JukeboxPopupMessage(@Nonnull String message, @Nonnull String... captions) {
        this(message, false, captions);
    }

    /**
     * Constructs a popup message.
     *
     * @param message  the message to display in the popup
     * @param captions the captions to use for this popup
     */
    public JukeboxPopupMessage(@Nonnull String message, @Nonnull Collection<String> captions) {
        this(message, false, captions);
    }

    /**
     * Constructs a popup message.
     *
     * @param message          the message to display in the popup
     * @param needsTranslating needs translating client side
     * @param captions         the captions to use for this popup
     */
    public JukeboxPopupMessage(@Nonnull String message, boolean needsTranslating, @Nonnull Collection<String> captions) {
        this.message = Preconditions.checkNotNull(message, "message");
        this.needsTranslating = needsTranslating;
        this.captions = Preconditions.checkNotNull(captions, "captions").toArray(new String[0]);
    }

    /**
     * Constructs a popup message.
     *
     * @param message          the message to display in the popup
     * @param needsTranslating needs translating client side
     * @param captions         the captions to use for this popup
     */
    public JukeboxPopupMessage(@Nonnull String message, boolean needsTranslating, @Nonnull String... captions) {
        this.message = Preconditions.checkNotNull(message, "message");
        this.needsTranslating = needsTranslating;
        this.captions = Preconditions.checkNotNull(captions, "captions");
    }

    /**
     * Returns this popup's message.
     *
     * @return the message
     */
    @Override
    public String getMessage() {
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
        if (o == null || JukeboxPopupMessage.class != o.getClass()) return false;
        JukeboxPopupMessage that = (JukeboxPopupMessage) o;
        return Objects.equals(message, that.message) &&
                Arrays.equals(captions, that.captions);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(message, captions);
    }

    @Override
    public final String toString() {
        return "JukeboxPopupMessage{" +
                "message='" + message + '\'' +
                ", captions=" + Arrays.toString(captions) +
                '}';
    }

    /**
     * Returns this popup's captions.
     *
     * @return the caption
     */
    @Override
    public String[] getParameters() {
        return Arrays.copyOf(captions, captions.length);
    }
}
