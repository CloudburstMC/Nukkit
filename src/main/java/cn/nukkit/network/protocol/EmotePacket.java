package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import lombok.ToString;

@ToString
public class EmotePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.EMOTE_PACKET;
    
    @Since("1.3.0.0-PN") public long runtimeId;
    @Since("1.3.0.0-PN") public String emoteID;
    @Since("1.3.0.0-PN") public byte flags;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.runtimeId = this.getUnsignedVarLong();
        this.emoteID = this.getString();
        this.flags = (byte) this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarLong(this.runtimeId);
        this.putString(this.emoteID);
        this.putByte(flags);
    }
}
