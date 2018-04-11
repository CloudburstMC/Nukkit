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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Nonnull
@Immutable
public final class RawMessage implements Message {
    private static final Type TYPE = Type.RAW;
    private final String message;
    private final boolean needsTranslating;

    public RawMessage(@Nonnull String message) {
        this(message, false);
    }

    public RawMessage(@Nonnull String message, boolean needsTranslating) {
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
        if (o == null || RawMessage.class != o.getClass()) return false;
        RawMessage that = (RawMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "RawMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
