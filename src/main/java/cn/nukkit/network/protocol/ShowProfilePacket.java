package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ShowProfilePacket extends DataPacket {

    public String xuid;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SHOW_PROFILE_PACKET");
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
