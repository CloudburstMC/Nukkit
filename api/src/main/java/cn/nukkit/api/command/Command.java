package cn.nukkit.api.command;


import java.util.Collection;

public interface Command extends CommandExecutor {

    Collection<String> getAliases();

    String getUsageMessage();

    String getDescription();

    CommandBuilder toBuilder();

    enum Origin {

    }
}
