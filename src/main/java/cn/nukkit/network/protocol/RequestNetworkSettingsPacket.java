package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class RequestNetworkSettingsPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET;

    public int protocolVersion;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }

    @Override
    public void decode() {
        this.protocolVersion = this.getInt();
    }
}
