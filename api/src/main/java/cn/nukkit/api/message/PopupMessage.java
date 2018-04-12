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

/**
 * A popup message, including a caption and message.
 */
@Nonnull
@Immutable
public final class PopupMessage implements ParameterMessage {
    private static final Type TYPE = Type.POPUP;
    /**
     * The caption of the message.
     */
    private final String message;
    /**
     * The message this popup is using.
     */
    private final String[] captions;
    /**
     * Whether the message will need to be translated by the client.
     */
    private final boolean needsTranslating;

    /**
     * Constructs a popup message.
     * @param message the message to display in the popup
     * @param captions the captions to use for this popup
     */
    public PopupMessage(@Nonnull String message, @Nonnull String... captions) {
        this(message, false, captions);
    }

    /**
     * Constructs a popup message.
     *
     * @param message  the message to display in the popup
     * @param captions the captions to use for this popup
     */
    public PopupMessage(@Nonnull String message, @Nonnull Collection<String> captions) {
        this(message, false, captions);
    }

    /**
     * Constructs a popup message.
     *
     * @param message          the message to display in the popup
     * @param needsTranslating needs translating client side
     * @param captions         the captions to use for this popup
     */
    public PopupMessage(@Nonnull String message, boolean needsTranslating, @Nonnull Collection<String> captions) {
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
    public PopupMessage(@Nonnull String message, boolean needsTranslating, @Nonnull String... captions) {
        this.message = Preconditions.checkNotNull(message, "message");
        this.needsTranslating = needsTranslating;
        this.captions = Preconditions.checkNotNull(captions, "captions");
    }

    /**
     * Returns this popup's message.
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
        if (o == null || PopupMessage.class != o.getClass()) return false;
        PopupMessage that = (PopupMessage) o;
        return Objects.equals(message, that.message) &&
                Arrays.equals(captions, that.captions);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(message, captions);
    }

    @Override
    public final String toString() {
        return "PopupMessage{" +
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
    public Collection<String> getParameters() {
        return ImmutableList.copyOf(captions);
    }
}
