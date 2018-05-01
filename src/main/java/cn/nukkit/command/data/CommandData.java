package cn.nukkit.command.data;

import java.util.HashMap;
import java.util.Map;

public class CommandData implements Cloneable {

    public CommandEnum aliases = null;
    public String description = "description";
    public Map<String, CommandOverload> overloads = new HashMap<>();

    public int flags;
    public int permission;

    @Override
    public CommandData clone() {
        try {
            return (CommandData) super.clone();
        } catch (Exception e) {
            return new CommandData();
        }
    }
}
