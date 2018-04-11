/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

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
     *
     * @param name         the name of the message
     * @param replacements the replacements to use
     */
    public TranslationMessage(@Nonnull String name, @Nonnull Object... replacements) {
        this.name = Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(replacements, "replacements");
        this.replacements = new String[replacements.length];
        for (int i = 0; i < replacements.length; i++) {
            this.replacements[i] = replacements[i].toString();
        }
    }

    /**
     * Creates a new translated message.
     * @param name the name of the message
     * @param replacements the replacements to use
     */
    public TranslationMessage(@Nonnull String name, @Nonnull String... replacements) {
        this.name = Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(replacements, "replacements");
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
