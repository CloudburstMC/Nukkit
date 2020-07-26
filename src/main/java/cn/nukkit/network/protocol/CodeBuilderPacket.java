package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class CodeBuilderPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CODE_BUILDER_PACKET;

    public boolean isOpening;
    public String url = "";

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.url = this.getString();
        this.isOpening = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(url);
        this.putBoolean(isOpening);
    }
}
