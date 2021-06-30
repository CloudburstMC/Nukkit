package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.UUID;

@ToString
public class EmoteListPacket extends DataPacket {

    public long entityRuntimeId;
    public UUID[] entries = new UUID[0];

    @Override
    public byte pid() {
        return ProtocolInfo.EMOTE_LIST_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        int count = (int) this.getUnsignedVarInt();
        for (int i = 0; i < count; i++) {
            this.entries[i] = this.getUUID();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putUnsignedVarInt(this.entries.length);
        for (UUID entry : this.entries) {
            this.putUUID(entry);
        }
    }
}
