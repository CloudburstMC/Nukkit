package cn.nukkit.server.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CommandRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.COMMAND_REQUEST_PACKET;

    public String command;
    public RequestType type;
    public String requestId;
    public long playerUniqueId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.command = this.getString();
        this.type = RequestType.values()[this.getVarInt()];
        this.requestId = this.getString();
        this.playerUniqueId = this.getVarLong();
    }

    @Override
    public void encode() {
    }

    public enum RequestType {
        PLAYER,
        COMMAND_BLOCK,
        MINECART_COMMAND_BLOCK,
        DEV_CONSOLE,
        AUTOMATION_PLAYER,
        CLIENT_AUTOMATION,
        DEDICATED_SERVER,
        ENTITY,
        VIRTUAL,
        GAME_ARGUMENT,
        INTERNAL
    }
}
