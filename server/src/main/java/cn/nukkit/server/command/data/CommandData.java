package cn.nukkit.server.command.data;

import java.util.HashMap;
import java.util.Map;

public class CommandData implements Cloneable {

    public String[] aliases = new String[0];
    public String description = "description";
    public Map<String, CommandOverload> overloads = new HashMap<>();
    public String permission = "any";

    @Override
    public CommandData clone() {
        try {
            return (CommandData) super.clone();
        } catch (Exception e) {
            return new CommandData();
        }
    }
}
