package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ContainerClosePacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.CONTAINER_CLOSE_PACKET :
                ProtocolInfo.CONTAINER_CLOSE_PACKET;
    }

    public int windowId;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.windowId = this.getByte();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putByte((byte) this.windowId);
    }
}
