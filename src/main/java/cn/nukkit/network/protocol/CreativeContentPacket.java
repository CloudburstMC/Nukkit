package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@PowerNukkitOnly
@Since("1.2.2.0-PN")
public class CreativeContentPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CREATIVE_CONTENT_PACKET;
    
    public Int2ObjectMap<Item> entries = new Int2ObjectOpenHashMap<>();

    @Override
    public void encode() {
        putUnsignedVarInt(entries.size());
        entries.forEach((i,item)-> {
            putUnsignedVarInt(i);
            putSlot(item);
        });
    }

    @Override
    public void decode() {
        int size = (int) getUnsignedVarInt();
        Int2ObjectMap<Item> entries = new Int2ObjectOpenHashMap<>(size);
        for (int remaining = size; remaining > 0; remaining--) {
            entries.put((int) getUnsignedVarInt(), getSlot());
        }
        this.entries = entries;
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
