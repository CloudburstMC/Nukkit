package cn.nukkit.api.message;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Nonnull
@Immutable
public class TranslationMessage implements ParameterMessage {
    private static final Type type = Type.TRANSLATION;
    private final String name;
    private final List<String> replacements;

    /**
     * Creates a new translated message.
     * @param name the name of the message
     * @param replacements the replacements to use
     */
    public TranslationMessage(@Nonnull String name, @Nonnull List<String> replacements) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.replacements = ImmutableList.copyOf(Preconditions.checkNotNull(replacements, "replacements"));
    }

    /**
     * Creates a new translated message.
     * @param name the name of the message
     * @param replacements the replacements to use
     */
    public TranslationMessage(@Nonnull String name, @Nonnull String... replacements) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.replacements = ImmutableList.copyOf(replacements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationMessage that = (TranslationMessage) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(replacements, that.replacements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, replacements);
    }

    @Override
    public String toString() {
        return "TranslatedMessage{" +
                "name='" + name + '\'' +
                ", replacements=" + replacements +
                '}';
    }

    /**
     * Returns an immutable list of replacements for this message.
     * @return the replacements to use
     */
    @Override
    public Collection<String> getParameters() {
        return replacements;
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
        return type;
    }
}
