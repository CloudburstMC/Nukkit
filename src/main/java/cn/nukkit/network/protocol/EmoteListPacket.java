package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.List;
import java.util.UUID;

@ToString
public class EmoteListPacket extends DataPacket {

    public long playerRuntimeId;
    public final List<UUID> emoteIds = new ArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.EMOTE_LIST_PACKET;
    }

    @Override
    public void decode() {
        this.playerRuntimeId = this.getEntityRuntimeId();
		for (int i = 0, count = (int) this.getUnsignedVarInt(); i < count; i++) {
			this.emoteIds.add(this.getUUID());
		}
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.playerEntityRuntimeId);
		this.putUnsignedVarInt(this.emoteIds.size());
		for (UUID emoteId : this.emoteIds) {
			this.putUUID(emoteId);
		}
    }
}
