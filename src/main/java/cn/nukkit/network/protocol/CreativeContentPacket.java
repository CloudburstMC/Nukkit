package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.ToString;

@Since("1.2.2.0-PN")
@ToString
public class CreativeContentPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CREATIVE_CONTENT_PACKET;

    //public Int2ObjectMap<Item> entries = new Int2ObjectOpenHashMap<>();
    public Item[] entries = new Item[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
/*        int size = this.getVarInt(); // Client bound packet only
        for (int i = 0; i < size; i++) {
            int id = (int) this.getUnsignedVarInt();
            Item item = this.getSlot();
            if (entries.putIfAbsent(id, item) != null) {
                throw new IllegalStateException("Creative content net ID collision!");
            }
        }*/
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(entries.length);
        for (Item item : entries) {
            this.putUnsignedVarInt(item.getId());
            this.putSlot(item);
        }

    }
}
