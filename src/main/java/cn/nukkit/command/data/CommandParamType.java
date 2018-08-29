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
    OPERATOR(ARG_TYPE_OPERATOR),
    TARGET(ARG_TYPE_TARGET),
    STRING(ARG_TYPE_STRING),
    POSITION(ARG_TYPE_POSITION),
    MESSAGE(ARG_TYPE_MESSAGE),
    RAWTEXT(ARG_TYPE_TEXT),
    JSON(ARG_TYPE_JSON),
    TEXT(ARG_TYPE_TEXT), // backwards compatibility
    COMMAND(ARG_TYPE_COMMAND),
    ;

    private final int id;

    CommandParamType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
