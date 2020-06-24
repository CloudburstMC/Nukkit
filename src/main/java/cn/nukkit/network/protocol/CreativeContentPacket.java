package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.ToString;

@ToString
public class CreativeContentPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CREATIVE_CONTENT_PACKET;

    public Int2ObjectMap<Item> entries = new Int2ObjectOpenHashMap<>();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        int size = this.getVarInt();
        for (int i = 0; i < size; i++) {
            int id = (int) this.getUnsignedVarInt();
            Item item = this.getSlot();
            if (entries.putIfAbsent(id, item) != null) {
                throw new IllegalStateException("Creative content net ID collision!");
            }
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(entries.size());
        for (Int2ObjectMap.Entry<Item> entry : entries.int2ObjectEntrySet()) {
            this.putUnsignedVarInt(entry.getIntKey());
            this.putSlot(entry.getValue());
        }

    }
}
