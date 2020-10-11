package cn.nukkit.command.data;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandParameter {

    public String name;
    public CommandParamType type;
    public boolean optional;
    public byte options = 0;

    public CommandEnum enumData;
    public String postFix;

    public CommandParameter(String name, String postFix, boolean optional) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.postFix = postFix;
    }

    public CommandParameter(String name, CommandParamType type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    public CommandParameter(String name, boolean optional) {
        this(name, CommandParamType.RAWTEXT, optional);
    }

    public CommandParameter(String name) {
        this(name, false);
    }

    public CommandParameter(String name, boolean optional, String enumType) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(enumType, new ArrayList<>());
    }

    public CommandParameter(String name, boolean optional, String[] enumValues) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(name + "Enums", Arrays.asList(enumValues));
    }

    public CommandParameter(String name, boolean optional, CommandEnum enumData) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.enumData = enumData;
    }

    public CommandParameter(String name, String enumType) {
        this(name, false, enumType);
    }

    public CommandParameter(String name, String[] enumValues) {
        this(name, false, enumValues);
    }
}
