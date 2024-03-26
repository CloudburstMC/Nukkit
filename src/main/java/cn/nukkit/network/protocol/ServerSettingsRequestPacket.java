package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ServerSettingsRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        // Nothing to decode
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }
}
