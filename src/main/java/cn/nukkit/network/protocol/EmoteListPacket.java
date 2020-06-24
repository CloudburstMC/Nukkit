package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.UUID;

@PowerNukkitOnly
@Since("1.2.2.0-PN")
public class EmoteListPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.EMOTE_LIST_PACKET;

    public long runtimeEntityId;
    public List<UUID> pieceIds = new ObjectArrayList<>();

    @Override
    public void encode() {
        putUnsignedVarLong(runtimeEntityId);
        putUnsignedVarInt(pieceIds.size());
        pieceIds.forEach(this::putUUID);
    }

    @Override
    public void decode() {
        runtimeEntityId = getUnsignedVarLong();
        int size = (int) getUnsignedVarInt();
        pieceIds = new ObjectArrayList<>(size);
        for (int i = 0; i < size; i++) {
            pieceIds.add(getUUID());
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
