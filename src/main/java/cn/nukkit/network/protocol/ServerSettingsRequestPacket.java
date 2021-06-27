package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ServerSettingsRequestPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        //No decode
    }

    @Override
    public void encode() {
        //No encode
    }
}
