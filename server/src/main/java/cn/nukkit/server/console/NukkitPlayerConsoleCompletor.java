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

import cn.nukkit.server.NukkitServer;
import com.google.common.base.Preconditions;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class NukkitPlayerConsoleCompletor implements Completer {
    private final NukkitServer server;

    public NukkitPlayerConsoleCompletor(NukkitServer server) {
        this.server = server;
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        Preconditions.checkNotNull(lineReader, "lineReader");
        Preconditions.checkNotNull(list, "list");

        if (parsedLine.wordIndex() == 0) {
            server.getOnlinePlayers().forEach((p) -> list.add(new Candidate(p.getName())));
        } else {
            // We are auto completing player names, so 99% of the times we are doing this, it will be something like
            // "say John*TAB*"
            // however the buffer will be "say John", but we don't want that, the buffer must be the player's name
            // TODO: Rewrite this.
            String buffer = parsedLine.line();
            String[] split = parsedLine.line().split(" ");
            buffer = split[split.length - 1]; // So we split and get the last value
            split[split.length - 1] = ""; // And now clear the last value
            String cmd = String.join(" ", split);
            SortedSet<String> names = new TreeSet<String>();
            NukkitServer.getInstance().getOnlinePlayers().forEach((p) -> names.add(p.getName()));
            for (String match : names) {
                if (!match.toLowerCase().startsWith(buffer.toLowerCase())) {
                    continue;
                }

                list.add(new Candidate(cmd + match));
            }
        }
    }
}
