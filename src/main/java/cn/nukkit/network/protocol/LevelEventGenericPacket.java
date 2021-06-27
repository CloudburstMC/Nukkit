package cn.nukkit.network.protocol;

import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

@ToString
public class LevelEventGenericPacket extends DataPacket {

    public int eventId;
    public CompoundTag eventData;

    @Override
    public byte pid() {
        return ProtocolInfo.LEVEL_EVENT_GENERIC_PACKET;
    }

    @Override
    public void decode() {
        this.eventId = this.getVarInt();
        this.eventData = this.getCompoundTag();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.eventId);
        this.putCompoundTag(this.eventData);
    }
}
