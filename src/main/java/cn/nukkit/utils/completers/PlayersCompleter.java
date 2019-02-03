package cn.nukkit.utils.completers;

import cn.nukkit.Server;
import jline.console.completer.Completer;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static jline.internal.Preconditions.checkNotNull;

public class PlayersCompleter implements Completer {

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        // buffer could be null
        checkNotNull(candidates);

        if (buffer == null) {
            Server.getInstance().getOnlinePlayers().values().forEach((p) -> candidates.add(p.getName()));
        } else {
            // We are auto completing player names, so 99% of the times we are doing this, it will be something like
            // "say John*TAB*"
            // however the buffer will be "say John", but we don't want that, the buffer must be the player's name
            String[] split = buffer.split(" ");
            buffer = split[split.length - 1]; // So we split and get the last value
            split[split.length - 1] = ""; // And now clear the last value
            String cmd = String.join(" ", split);
            SortedSet<String> names = new TreeSet<>();
            Server.getInstance().getOnlinePlayers().values().forEach((p) -> names.add(p.getName()));
            for (String match : names) {
                if (!match.toLowerCase().startsWith(buffer.toLowerCase())) {
                    continue;
                }

                candidates.add(cmd + match);
            }
        }

        return candidates.isEmpty() ? -1 : 0;
    }

}
