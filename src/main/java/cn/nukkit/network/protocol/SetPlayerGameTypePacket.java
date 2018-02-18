package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetPlayerGameTypePacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SET_PLAYER_GAME_TYPE_PACKET");
    }

    public int gamemode;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.gamemode = this.getVarInt();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVarInt(this.gamemode);
    }
}
