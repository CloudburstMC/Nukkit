package cn.nukkit.command.data;

import com.nukkitx.protocol.bedrock.data.CommandParamData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author CreeperFace
 */
@Getter
@RequiredArgsConstructor
public enum CommandParamType {
    INT(CommandParamData.Type.INT),
    FLOAT(CommandParamData.Type.FLOAT),
    VALUE(CommandParamData.Type.VALUE),
    WILDCARD_INT(CommandParamData.Type.WILDCARD_INT),
    TARGET(CommandParamData.Type.TARGET),
    WILDCARD_TARGET(CommandParamData.Type.WILDCARD_TARGET),
    STRING(CommandParamData.Type.STRING),
    POSITION(CommandParamData.Type.POSITION),
    MESSAGE(CommandParamData.Type.MESSAGE),
    RAWTEXT(CommandParamData.Type.TEXT),
    JSON(CommandParamData.Type.JSON),
    TEXT(CommandParamData.Type.TEXT), // backwards compatibility
    COMMAND(CommandParamData.Type.COMMAND),
    FILE_PATH(CommandParamData.Type.FILE_PATH),
    OPERATOR(CommandParamData.Type.OPERATOR);

    private final CommandParamData.Type type;
}
