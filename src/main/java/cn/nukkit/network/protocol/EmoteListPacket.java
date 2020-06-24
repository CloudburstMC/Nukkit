package cn.nukkit.network.protocol;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.UUID;

public class EmoteListPacket extends DataPacket {
    public long runtimeId;
    public final List<UUID> pieceIds = new ObjectArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.EMOTE_LIST_PACKET;
    }

    @Override
    public void decode() {
        this.runtimeId = this.getEntityUniqueId();
        int size = (int) this.getUnsignedVarInt();
        for (int i = 0; i < size; i++) {
            UUID id = this.getUUID();
            pieceIds.add(id);
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(runtimeId);
        this.putUnsignedVarInt(pieceIds.size());
        for (UUID id : pieceIds) {
            this.putUUID(id);
        }
    }
}
