package cn.nukkit.api.message;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Nonnull
@Immutable
public class TranslatedMessage implements Message {

    private final String name;
    private final List<String> replacements;

    /**
     * Creates a new translated message.
     * @param name the name of the MCPE message
     * @param replacements the replacements to use
     */
    public TranslatedMessage(@Nonnull String name, @Nonnull List<String> replacements) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.replacements = ImmutableList.copyOf(Preconditions.checkNotNull(replacements, "replacements"));
    }

    /**
     * Creates a new translated message.
     * @param name the name of the MCPE message
     * @param replacements the replacements to use
     */
    public TranslatedMessage(@Nonnull String name, @Nonnull String... replacements) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.replacements = ImmutableList.copyOf(replacements);
    }

    /**
     * Returns the name of the message to use.
     * @return the name
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Returns an immutable list of replacements for this message.
     * @return the replacements to use
     */
    @Nonnull
    public List<String> getReplacements() {
        return replacements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslatedMessage that = (TranslatedMessage) o;
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
}
