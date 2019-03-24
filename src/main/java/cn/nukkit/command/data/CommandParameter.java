package cn.nukkit.command.data;


import java.util.ArrayList;
import java.util.Arrays;

public class CommandParameter {

    public final static String ARG_TYPE_STRING = "string";
    public final static String ARG_TYPE_STRING_ENUM = "stringenum";
    public final static String ARG_TYPE_BOOL = "bool";
    public final static String ARG_TYPE_TARGET = "target";
    public final static String ARG_TYPE_PLAYER = "target";
    public final static String ARG_TYPE_BLOCK_POS = "blockpos";
    public final static String ARG_TYPE_RAW_TEXT = "rawtext";
    public final static String ARG_TYPE_INT = "int";

    public static final String ENUM_TYPE_ITEM_LIST = "itemType";
    public static final String ENUM_TYPE_BLOCK_LIST = "blockType";
    public static final String ENUM_TYPE_COMMAND_LIST = "commandName";
    public static final String ENUM_TYPE_ENCHANTMENT_LIST = "enchantmentType";
    public static final String ENUM_TYPE_ENTITY_LIST = "entityType";
    public static final String ENUM_TYPE_EFFECT_LIST = "effectType";
    public static final String ENUM_TYPE_PARTICLE_LIST = "particleType";

    public String name;
    public CommandParamType type;
    public boolean optional;
    public byte options = 0;

    public CommandEnum enumData;
    public String postFix;

    @Deprecated
    public CommandParameter(String name, String type, boolean optional) {
        this(name, fromString(type), optional);
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

    public CommandParameter(String name, String enumType) {
        this(name, false, enumType);
    }

    public CommandParameter(String name, String[] enumValues) {
        this(name, false, enumValues);
    }

    protected static CommandParamType fromString(String param) {
        switch (param) {
            case "string":
            case "stringenum":
                return CommandParamType.STRING;
            case "target":
                return CommandParamType.TARGET;
            case "blockpos":
                return CommandParamType.POSITION;
            case "rawtext":
                return CommandParamType.RAWTEXT;
            case "int":
                return CommandParamType.INT;
        }

        return CommandParamType.RAWTEXT;
    }
}
