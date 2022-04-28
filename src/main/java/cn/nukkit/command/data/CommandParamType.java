package cn.nukkit.command.data;

import static cn.nukkit.network.protocol.AvailableCommandsPacket.*;

/**
 * @author CreeperFace
 */
public enum CommandParamType {
    INT(ARG_TYPE_INT),
    FLOAT(ARG_TYPE_FLOAT),
    VALUE(ARG_TYPE_VALUE),
    WILDCARD_INT(ARG_TYPE_WILDCARD_INT),
    TARGET(ARG_TYPE_TARGET),
    WILDCARD_TARGET(ARG_TYPE_WILDCARD_TARGET),
    EQUIPMENT_SLOT(ARG_TYPE_EQUIPMENT_SLOT),
    STRING(ARG_TYPE_STRING),
    BLOCK_POSITION(ARG_TYPE_BLOCK_POSITION),
    POSITION(ARG_TYPE_POSITION),
    MESSAGE(ARG_TYPE_MESSAGE),
    RAWTEXT(ARG_TYPE_RAWTEXT),
    JSON(ARG_TYPE_JSON),
    TEXT(ARG_TYPE_RAWTEXT), // backwards compatibility
    COMMAND(ARG_TYPE_COMMAND),
    FILE_PATH(ARG_TYPE_FILE_PATH),
    OPERATOR(ARG_TYPE_OPERATOR),
    ;

    private final int id;

    CommandParamType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
