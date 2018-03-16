package cn.nukkit.api.message;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Nonnull
@Immutable
public final class TranslationMessage implements ParameterMessage {
    private static final Type TYPE = Type.TRANSLATION;
    private final String name;
    private final String[] replacements;

    /**
     * Creates a new translated message.
     * @param name the name of the message
     * @param replacements the replacements to use
     */
    public TranslationMessage(@Nonnull String name, @Nonnull Collection<String> replacements) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.replacements = Preconditions.checkNotNull(replacements, "replacements").toArray(new String[0]);
    }

    /**
     * Creates a new translated message.
     * @param name the name of the message
     * @param replacements the replacements to use
     */
    public TranslationMessage(@Nonnull String name, @Nonnull String... replacements) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.replacements = Arrays.copyOf(replacements, replacements.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || TranslationMessage.class != o.getClass()) return false;
        TranslationMessage that = (TranslationMessage) o;
        return Objects.equals(name, that.name) &&
                Arrays.equals(replacements, that.replacements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, replacements);
    }

    @Override
    public String toString() {
        return "TranslationMessage{" +
                "name='" + name + '\'' +
                ", replacements=" + Arrays.toString(replacements) +
                '}';
    }

    /**
     * Returns an immutable list of replacements for this message.
     * @return the replacements to use
     */
    @Override
    public Collection<String> getParameters() {
        return ImmutableList.copyOf(replacements);
    }

    /**
     * Returns the name of the message to use.
     * @return the name
     */
    @Override
    public String getMessage() {
        return name;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public boolean needsTranslating() {
        return false; // This is a translation container.
    }
}
