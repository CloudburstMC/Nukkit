package cn.nukkit.command.data;


public class CommandParameter {

    public final static String ARG_TYPE_STRING = "string";
    public final static String ARG_TYPE_STRING_ENUM = "stringenum";
    public final static String ARG_TYPE_BOOL = "bool";
    public final static String ARG_TYPE_TARGET = "target";
    public final static String ARG_TYPE_PLAYER = "target";
    public final static String ARG_TYPE_BLOCK_POS = "blockpos";
    public final static String ARG_TYPE_RAW_TEXT = "rawtext";
    public final static String ARG_TYPE_TEXT = "text";
    public final static String ARG_TYPE_INT = "int";

    public String name;
    public String type;
    public boolean optional;

    public String[] enum_values;

    public CommandParameter(String name, String type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    public CommandParameter(String name, boolean optional) {
        this(name, ARG_TYPE_RAW_TEXT, optional);
    }

    public CommandParameter(String name) {
        this(name, false);
    }

    public CommandParameter(String name, boolean optional, String[] enumValues) {
        this.name = name;
        this.type = ARG_TYPE_STRING_ENUM;
        this.optional = optional;
        this.enum_values = enumValues;
    }

    public CommandParameter(String name, String[] enumValues) {
        this(name, false, enumValues);
    }

    /**
     * Minecraft PE 1.1 enum types. Not used as of 1.2.
     */
    @Deprecated
    public static final String ENUM_TYPE_ITEM_LIST = "itemType";
    @Deprecated
    public static final String ENUM_TYPE_BLOCK_LIST = "blockType";
    @Deprecated
    public static final String ENUM_TYPE_COMMAND_LIST = "commandName";
    @Deprecated
    public static final String ENUM_TYPE_ENCHANTMENT_LIST = "enchantmentType";
    @Deprecated
    public static final String ENUM_TYPE_ENTITY_LIST = "entityType";
    @Deprecated
    public static final String ENUM_TYPE_EFFECT_LIST = "effectType";
    @Deprecated
    public static final String ENUM_TYPE_PARTICLE_LIST = "particleType";

    @Deprecated
    public String enum_type;

    @Deprecated
    public CommandParameter(String name, boolean optional, String enumType) {
        this.name = name;
        this.type = ARG_TYPE_STRING_ENUM;
        this.optional = optional;
        this.enum_type = enumType;
    }
    @Deprecated
    public CommandParameter(String name, String enumType) {
        this(name, false, enumType);
    }

}
