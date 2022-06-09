package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ToastRequestPacket extends DataPacket {

    public String mTitle = "";
    public String mContent = "";

    @Override
    public byte pid() {
        return ProtocolInfo.TOAST_REQUEST_PACKET;
    }

    @Override
    public void decode() {
        this.mTitle = this.getString();
        this.mContent = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.mTitle);
        this.putString(this.mContent);
    }
}
