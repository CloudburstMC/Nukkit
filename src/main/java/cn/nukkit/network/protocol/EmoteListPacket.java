package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Since("1.3.0.0-PN")
@ToString
public class EmoteListPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.EMOTE_LIST_PACKET;

    @Since("1.3.0.0-PN") public long runtimeId;
    @Since("1.3.0.0-PN") public final List<UUID> pieceIds = new ObjectArrayList<>();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.runtimeId = this.getEntityRuntimeId();
        int size = (int) this.getUnsignedVarInt();
        for (int i = 0; i < size; i++) {
            UUID id = this.getUUID();
            pieceIds.add(id);
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(runtimeId);
        this.putUnsignedVarInt(pieceIds.size());
        for (UUID id : pieceIds) {
            this.putUUID(id);
        }
    }
}
