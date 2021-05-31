package cn.nukkit.command.data;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

import java.util.ArrayList;

public class CommandParameter {
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final CommandParameter[] EMPTY_ARRAY = new CommandParameter[0];
    
    public String name;
    public CommandParamType type;
    public boolean optional;
    public byte options = 0;

    public CommandEnum enumData;
    public String postFix;

    /**
     * @deprecated use {@link #newType(String, boolean, CommandParamType)} instead
     */
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newType(String, boolean, CommandParamType)")
    public CommandParameter(String name, String type, boolean optional) {
        this(name, fromString(type), optional);
    }

    /**
     * @deprecated use {@link #newType(String, boolean, CommandParamType)} instead
     */
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newType(String, boolean, CommandParamType)")
    public CommandParameter(String name, CommandParamType type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    /**
     * @deprecated use {@link #newType(String, boolean, CommandParamType)} instead
     */
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newType(String, boolean, CommandParamType)")
    public CommandParameter(String name, boolean optional) {
        this(name, CommandParamType.RAWTEXT, optional);
    }

    /**
     * @deprecated use {@link #newType(String, CommandParamType)} instead
     */
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newType(String, CommandParamType)")
    public CommandParameter(String name) {
        this(name, false);
    }

    /**
     * @deprecated use {@link #newEnum(String, boolean, String)} instead
     */
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newEnum(String, boolean, String)")
    public CommandParameter(String name, boolean optional, String enumType) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(enumType, ENUM_TYPE_ITEM_LIST.equals(enumType)? Item.getItemList() : new ArrayList<>());
    }

    /**
     * @deprecated use {@link #newEnum(String, boolean, String[])} instead
     */
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newEnum(String, boolean, String[])")
    public CommandParameter(String name, boolean optional, String[] enumValues) {
        this.name = name;
        this.type = CommandParamType.RAWTEXT;
        this.optional = optional;
        this.enumData = new CommandEnum(name + "Enums", enumValues);
    }

    /**
     * @deprecated use {@link #newEnum(String, String)} instead
     */
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newEnum(String, String)")
    public CommandParameter(String name, String enumType) {
        this(name, false, enumType);
    }

    /**
     * @deprecated use {@link #newEnum(String, String[])} instead
     */
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Deprecated by Cloudburst Nukkit", replaceWith = "newEnum(String, String[])")
    public CommandParameter(String name, String[] enumValues) {
        this(name, false, enumValues);
    }

    private CommandParameter(String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        this.name = name;
        this.optional = optional;
        this.type = type;
        this.enumData = enumData;
        this.postFix = postFix;
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newType(String name, CommandParamType type) {
        return newType(name, false, type);
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newType(String name, boolean optional, CommandParamType type) {
        return new CommandParameter(name, optional, type, null, null);
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, String[] values) {
        return newEnum(name, false, values);
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, boolean optional, String[] values) {
        return newEnum(name, optional, new CommandEnum(name + "Enums", values));
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, String type) {
        return newEnum(name, false, type);
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, boolean optional, String type) {
        return newEnum(name, optional, new CommandEnum(type, new ArrayList<>()));
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, CommandEnum data) {
        return newEnum(name, false, data);
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newEnum(String name, boolean optional, CommandEnum data) {
        return new CommandParameter(name, optional, CommandParamType.RAWTEXT, data, null);
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newPostfix(String name, String postfix) {
        return newPostfix(name, false, postfix);
    }

    @Since("1.4.0.0-PN")
    public static CommandParameter newPostfix(String name, boolean optional, String postfix) {
        return new CommandParameter(name, optional, CommandParamType.RAWTEXT, null, postfix);
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

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_STRING = "string";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_STRING_ENUM = "stringenum";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_BOOL = "bool";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_TARGET = "target";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_PLAYER = "target";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_BLOCK_POS = "blockpos";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_RAW_TEXT = "rawtext";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public final static String ARG_TYPE_INT = "int";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_ITEM_LIST = "Item";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_BLOCK_LIST = "blockType";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_COMMAND_LIST = "commandName";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_ENCHANTMENT_LIST = "enchantmentType";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_ENTITY_LIST = "entityType";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_EFFECT_LIST = "effectType";

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Removed from Cloudburst Nukkit") @PowerNukkitOnly("Re-added for backward compatibility")
    public static final String ENUM_TYPE_PARTICLE_LIST = "particleType";
}
