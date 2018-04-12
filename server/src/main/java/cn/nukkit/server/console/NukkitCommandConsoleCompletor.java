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

public class NukkitCommandConsoleCompletor implements Completer {
    private final NukkitServer server;

    public NukkitCommandConsoleCompletor(NukkitServer server) {
        this.server = server;
    }


    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        // buffer could be null
        Preconditions.checkNotNull(lineReader, "lineReader");
        Preconditions.checkNotNull(list, "list");

        if (parsedLine.wordIndex() == 0) {
            server.getCommandManager().getCommandNames().forEach((cmd) -> list.add(new Candidate(cmd)));
        } else {
            SortedSet<String> names = new TreeSet<>(server.getCommandManager().getCommandNames());
            for (String match : names) {
                if (!match.toLowerCase().startsWith(parsedLine.line().toLowerCase())) {
                    continue;
                }

                list.add(new Candidate(match));
            }
        }
    }
}
