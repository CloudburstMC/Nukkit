package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.CommandOriginData;
import lombok.ToString;

import java.util.UUID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class CommandRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.COMMAND_REQUEST_PACKET;

    public static final int TYPE_PLAYER = 0;
    public static final int TYPE_COMMAND_BLOCK = 1;
    public static final int TYPE_MINECART_COMMAND_BLOCK = 2;
    public static final int TYPE_DEV_CONSOLE = 3;
    public static final int TYPE_AUTOMATION_PLAYER = 4;
    public static final int TYPE_CLIENT_AUTOMATION = 5;
    public static final int TYPE_DEDICATED_SERVER = 6;
    public static final int TYPE_ENTITY = 7;
    public static final int TYPE_VIRTUAL = 8;
    public static final int TYPE_GAME_ARGUMENT = 9;
    public static final int TYPE_INTERNAL = 10;

    public String command;
    public CommandOriginData data;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.command = this.getString();

        CommandOriginData.Origin type = CommandOriginData.Origin.values()[this.getVarInt()];
        UUID uuid = this.getUUID();
        String requestId = this.getString();
        Long varLong = null;
        if (type == CommandOriginData.Origin.DEV_CONSOLE || type == CommandOriginData.Origin.TEST) {
            varLong = this.getVarLong();
        }
        this.data = new CommandOriginData(type, uuid, requestId, varLong);
    }

    @Override
    public void encode() {
    }

}
