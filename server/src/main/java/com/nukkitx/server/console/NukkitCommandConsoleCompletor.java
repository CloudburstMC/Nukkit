package com.nukkitx.server.console;

import com.google.common.base.Preconditions;
import com.nukkitx.server.NukkitServer;
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
