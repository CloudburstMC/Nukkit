package cn.nukkit.network.protocol;

public class SimpleEventPacket extends DataPacket {

    public short unknown;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.SIMPLE_EVENT_PACKET :
                ProtocolInfo.SIMPLE_EVENT_PACKET;
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putShort(this.unknown);
    }
}
