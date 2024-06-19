package cn.nukkit.network.protocol;

public class SetDefaultGameTypePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_DEFAULT_GAME_TYPE_PACKET;

    public int gamemode;

    @Override
    public void decode() {
        this.gamemode = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.gamemode);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
