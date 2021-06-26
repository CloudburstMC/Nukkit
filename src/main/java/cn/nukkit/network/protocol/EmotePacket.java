package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EmotePacket extends DataPacket {

    public long entityRuntimeId;
    public String emoteId;
    public byte flags;

    @Override
    public byte pid() {
        return ProtocolInfo.EMOTE_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
		this.emoteId = this.getString();
		this.flags = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
		this.putString(this.emoteId);
		this.putByte(this.flags);
    }
}
