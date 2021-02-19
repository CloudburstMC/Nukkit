package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EmotePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.EMOTE_PACKET;
    public long runtimeId;
    public String emoteID;
    public byte flags;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.runtimeId = this.getEntityRuntimeId();
        this.emoteID = this.getString();
        this.flags = (byte) this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.runtimeId);
        this.putString(this.emoteID);
        this.putByte(flags);
    }
}
