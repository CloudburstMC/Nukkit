package cn.nukkit.console;

import cn.nukkit.Server;
import lombok.RequiredArgsConstructor;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class NukkitConsoleCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> candidates) {
        if (parsedLine.wordIndex() == 0) {
            if (parsedLine.word().isEmpty()) {
                addCandidates(s -> candidates.add(new Candidate(s)));
                return;
            }
            SortedSet<String> names = new TreeSet<>();
            addCandidates(names::add);
            for (String match : names) {
                if (!match.toLowerCase(Locale.ROOT).startsWith(parsedLine.word())) {
                    continue;
                }

                candidates.add(new Candidate(match));
            }
        } else if (parsedLine.wordIndex() > 0 && !parsedLine.word().isEmpty()) {
            String word = parsedLine.word();
            SortedSet<String> names = new TreeSet<>();
            Server.getInstance().getOnlinePlayers().values().forEach((p) -> names.add(p.getName()));
            for (String match : names) {
                if (!match.toLowerCase(Locale.ROOT).startsWith(word.toLowerCase(Locale.ROOT))) {
                    continue;
                }

                candidates.add(new Candidate(match));
            }
        }
    }

    private static void addCandidates(Consumer<String> commandConsumer) {
        for (String command : Server.getInstance().getCommandMap().getCommands().keySet()) {
            if (!command.contains(":")) {
                commandConsumer.accept(command);
            }
        }
    }
}
