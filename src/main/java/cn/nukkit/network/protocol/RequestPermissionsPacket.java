package cn.nukkit.network.protocol;

public class RequestPermissionsPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REQUEST_PERMISSIONS_PACKET;

    @Override
    public void decode() {
        // TODO
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}