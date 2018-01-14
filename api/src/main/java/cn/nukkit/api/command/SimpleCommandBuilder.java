package cn.nukkit.api.command;

public interface SimpleCommandBuilder {

    CommandExecutor getCommandExecutor();

    String getName();

    void setName(String name);



    Command build();
}
