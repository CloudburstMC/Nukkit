package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ToastRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TOAST_REQUEST_PACKET;

    public String title = "";
    public String content = "";

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.title);
        this.putString(this.content);
    }
}
