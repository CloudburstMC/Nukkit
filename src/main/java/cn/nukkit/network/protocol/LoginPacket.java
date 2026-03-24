package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class LoginPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LOGIN_PACKET;

    private int protocol_;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.protocol_ = this.getInt();
        if (this.protocol_ == 0) {
            setOffset(getOffset() + 2);
            this.protocol_ = getInt();
        }
        this.setBuffer(this.getByteArray(), 0);
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }

    public int getProtocol() {
        return protocol_;
    }
}
