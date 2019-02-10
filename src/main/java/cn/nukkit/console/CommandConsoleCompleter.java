package cn.nukkit.console;

import cn.nukkit.Server;
import lombok.RequiredArgsConstructor;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@RequiredArgsConstructor
public class CommandConsoleCompleter implements Completer {
    private final Server server;

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> candidates) {
        if (parsedLine.wordIndex() == 0) {
            for (String command : server.getCommandMap().getCommands().keySet()) {
                candidates.add(new Candidate(command));
            }
        } else {
            SortedSet<String> names = new TreeSet<>(server.getCommandMap().getCommands().keySet());
            for (String match : names) {
                if (!match.toLowerCase().startsWith(parsedLine.line())) {
                    continue;
                }

                candidates.add(new Candidate(match));
            }
        }
    }
}
