package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetTimePacket extends DataPacket {

    public int time;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SET_TIME_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVarInt(this.time);
    }

}
