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

package cn.nukkit.server.console;

import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.server.lang.NukkitLocaleManager;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.message.Message;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

public class TranslatableMessage implements Message {
    private static NukkitLocaleManager languageManager;

    private final String format;
    private final Object[] parameters;
    private transient String formatted = null;

    private TranslatableMessage(String format, Object[] parameters) {
        this.format = format;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    public static void setLanguageManager(NukkitLocaleManager languageManager) {
        TranslatableMessage.languageManager = languageManager;
    }

    public static TranslatableMessage of(@Nonnull String format, @Nonnull Object... parameters) {
        return new TranslatableMessage(Preconditions.checkNotNull(format, "format"), Preconditions.checkNotNull(parameters, "parameters"));
    }

    public static TranslatableMessage of(@Nonnull String format, @Nonnull Collection parameters) {
        Preconditions.checkNotNull(parameters, "parameters");
        return new TranslatableMessage(Preconditions.checkNotNull(format, "format"), parameters.toArray());
    }

    public static TranslatableMessage of(@Nonnull TranslationMessage message) {
        Preconditions.checkNotNull(message, "message");
        return of(message.getMessage(), message.getParameters());
    }

    @Override
    public String getFormattedMessage() {
        if (formatted != null) {
            return formatted;
        }
        try {
            formatted = languageManager.replaceI18n(format, parameters);
        } catch (Exception e) {
            return format + ", " +
                    Arrays.toString(parameters);
        }
        return formatted;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public Object[] getParameters() {
        return Arrays.copyOf(parameters, parameters.length);
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }
}
