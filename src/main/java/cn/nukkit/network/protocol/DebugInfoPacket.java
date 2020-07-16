package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import lombok.ToString;

@Since("1.3.0.0-PN")
@ToString
public class DebugInfoPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.DEBUG_INFO_PACKET;

    @Since("1.3.0.0-PN") public long entityId;
    @Since("1.3.0.0-PN") public String data;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityId = this.getLong();
        this.data = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(this.entityId);
        this.putString(this.data);
    }
}
