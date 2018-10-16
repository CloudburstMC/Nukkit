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
            server.getOnlinePlayers().forEach((p) -> names.add(p.getName()));
            for (String match : names) {
                if (!match.toLowerCase().startsWith(buffer.toLowerCase())) {
                    continue;
                }

                list.add(new Candidate(cmd + match));
            }
        }
    }
}
