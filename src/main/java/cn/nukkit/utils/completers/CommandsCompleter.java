package cn.nukkit.utils.completers;

import cn.nukkit.Server;
import jline.console.completer.Completer;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static jline.internal.Preconditions.checkNotNull;

public class CommandsCompleter implements Completer {

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        // buffer could be null
        checkNotNull(candidates);

        if (buffer == null) {
            candidates.addAll(Server.getInstance().getCommandMap().getCommands().keySet());
        } else {
            SortedSet<String> names = new TreeSet<>(Server.getInstance().getCommandMap().getCommands().keySet());
            for (String match : names) {
                if (!match.toLowerCase().startsWith(buffer.toLowerCase())) {
                    continue;
                }

                candidates.add(match);
            }
        }

        return candidates.isEmpty() ? -1 : 0;
    }

}
