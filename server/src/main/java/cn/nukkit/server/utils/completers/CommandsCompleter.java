package cn.nukkit.server.utils.completers;

import cn.nukkit.server.NukkitServer;
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
            NukkitServer.getInstance().getCommandMap().getCommands().keySet().forEach((cmd) -> candidates.add(cmd));
        } else {
            SortedSet<String> names = new TreeSet<String>();
            NukkitServer.getInstance().getCommandMap().getCommands().keySet().forEach((cmd) -> names.add(cmd));
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
