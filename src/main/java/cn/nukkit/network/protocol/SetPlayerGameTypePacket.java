package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetPlayerGameTypePacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.SET_PLAYER_GAME_TYPE_PACKET :
                ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET;
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
