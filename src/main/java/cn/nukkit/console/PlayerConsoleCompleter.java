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
public class PlayerConsoleCompleter implements Completer {
    private final Server server;

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> candidates) {
        if (parsedLine.wordIndex() == 0) {
            server.getOnlinePlayers().values().forEach((player) -> candidates.add(new Candidate(player.getName())));
        } else {
            String word = parsedLine.word();
            SortedSet<String> names = new TreeSet<>();
            server.getOnlinePlayers().values().forEach((p) -> names.add(p.getName()));
            for (String match : names) {
                if (!match.toLowerCase().startsWith(word.toLowerCase())) {
                    continue;
                }

                candidates.add(new Candidate(parsedLine.line() + match.substring(word.length())));
            }
        }
    }
}
