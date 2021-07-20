package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class AutomationClientConnectPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.AUTOMATION_CLIENT_CONNECT_PACKET;

    private String address;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.address = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.address);
    }
}
