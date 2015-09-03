package cn.nukkit.command;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FormattedCommandAlias extends Command {

    private List<String> formatStrings = new ArrayList<>();

    public FormattedCommandAlias(String alias, List<String> formatStrings) {
        super(alias);
        this.formatStrings = formatStrings;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        //todo
        return false;
    }
}
