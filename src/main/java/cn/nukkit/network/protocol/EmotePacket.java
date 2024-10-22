package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EmotePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.EMOTE_PACKET;

    public static final byte FLAG_SERVER = 1;
    public static final byte FLAG_MUTE_ANNOUNCEMENT = 2;

    public long runtimeId;
    public String xuid = "";
    public String platformId = "";
    public String emoteID;
    public byte flags;
    public long emoteTicks;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.runtimeId = this.getEntityRuntimeId();
        this.emoteID = this.getString();
        this.emoteTicks = this.getUnsignedVarInt();
        this.xuid = this.getString();
        this.platformId = this.getString();
        this.flags = (byte) this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.runtimeId);
        this.putString(this.emoteID);
        this.putUnsignedVarInt(this.emoteTicks);
        this.putString(this.xuid);
        this.putString(this.platformId);
        this.putByte(flags);
    }
}
