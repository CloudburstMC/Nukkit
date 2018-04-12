package cn.nukkit.command.data;

/**
 * @author CreeperFace
 */
public enum CommandParamType {
    INT(0x01),
    FLOAT(0x02),
    VALUE(0x03),
    TARGET(0x04),

    STRING(0x0d),
    POSITION(0x0e),

    RAWTEXT(0x11),

    TEXT(0x13),

    JSON(0x16),

    COMMAND(0x1d);

    private final int id;

    CommandParamType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
