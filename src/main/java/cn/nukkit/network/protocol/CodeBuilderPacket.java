package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import lombok.ToString;

@Since("1.3.0.0-PN")
@ToString
public class CodeBuilderPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CODE_BUILDER_PACKET;

    @Since("1.3.0.0-PN") public boolean isOpening;
    @Since("1.3.0.0-PN") public String url = "";

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
