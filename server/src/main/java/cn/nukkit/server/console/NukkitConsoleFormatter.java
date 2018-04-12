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

import cn.nukkit.api.util.TextFormat;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class NukkitConsoleFormatter implements Function<String, String> {
    private static final String RESET = "\u001B[39;0m";

    private static final ImmutableMap<Pattern, String> REPLACEMENTS = ImmutableMap.<Pattern, String>builder()
            .put(compile(TextFormat.BLACK), "\u001B[0;30;22m")
            .put(compile(TextFormat.DARK_BLUE), "\u001B[0;34;22m")
            .put(compile(TextFormat.DARK_GREEN), "\u001B[0;32;22m")
            .put(compile(TextFormat.DARK_AQUA), "\u001B[0;36;22m")
            .put(compile(TextFormat.DARK_RED), "\u001B[0;31;22m")
            .put(compile(TextFormat.DARK_PURPLE), "\u001B[0;35;22m")
            .put(compile(TextFormat.GOLD), "\u001B[0;33;22m")
            .put(compile(TextFormat.GRAY), "\u001B[0;37;22m")
            .put(compile(TextFormat.DARK_GRAY), "\u001B[0;30;1m")
            .put(compile(TextFormat.BLUE), "\u001B[0;34;1m")
            .put(compile(TextFormat.GREEN), "\u001B[0;32;1m")
            .put(compile(TextFormat.AQUA), "\u001B[0;36;1m")
            .put(compile(TextFormat.RED), "\u001B[0;31;1m")
            .put(compile(TextFormat.LIGHT_PURPLE), "\u001B[0;35;1m")
            .put(compile(TextFormat.YELLOW), "\u001B[0;33;1m")
            .put(compile(TextFormat.WHITE), "\u001B[0;37;1m")
            .put(compile(TextFormat.BOLD), "\u001B[21m")
            .put(compile(TextFormat.STRIKETHROUGH), "\u001B[9m")
            .put(compile(TextFormat.UNDERLINE), "\u001B[4m")
            .put(compile(TextFormat.ITALIC), "\u001B[3m")
            .put(compile(TextFormat.RESET), RESET).build();

    private static Pattern compile(TextFormat textFormat) {
        return Pattern.compile(textFormat.toString(), Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
    }

    @Override
    public String apply(String text) {
        for (Map.Entry<Pattern, String> entry : REPLACEMENTS.entrySet()) {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }
        return text + RESET;
    }
}
