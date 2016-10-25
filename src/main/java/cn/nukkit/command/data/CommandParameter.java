package cn.nukkit.command.data;


public class CommandParameter {

    public String name;
    public String type;
    public boolean optional;

    public CommandParameter(String name, String type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

}
