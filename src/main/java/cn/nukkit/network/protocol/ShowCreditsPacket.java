package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ShowCreditsPacket extends DataPacket {

    public static final int STATUS_START_CREDITS = 0;
    public static final int STATUS_END_CREDITS = 1;

    public long entityRuntimeId;
    public int status;

    @Override
    public byte pid() {
        return ProtocolInfo.SHOW_CREDITS_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.status = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVarInt(this.status);
    }
}
