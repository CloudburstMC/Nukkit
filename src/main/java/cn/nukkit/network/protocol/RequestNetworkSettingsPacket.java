package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class RequestNetworkSettingsPacket extends DataPacket {

    public int protocolVersion;

    @Override
    public byte pid() {
        return ProtocolInfo.REQUEST_NETWORK_SETTINGS_PACKET;
    }

    @Override
    public void encode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode() {
        this.protocolVersion = this.getInt();
    }
}
