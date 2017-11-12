package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShowProfilePacket extends DataPacket {

    public String xuid;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                0 :
                ProtocolInfo.SHOW_PROFILE_PACKET;
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.xuid = this.getString();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putString(this.xuid);
    }

}
