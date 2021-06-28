package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

@ToString
public class CreativeContentPacket extends DataPacket {

    public Item[] entries = new Item[0];

    @Override
    public byte pid() {
        return ProtocolInfo.CREATIVE_CONTENT_PACKET;
    }

    @Override
    public void decode() {
        int count = (int) this.getUnsignedVarInt();
        this.entries = new Item[count];
        for (int i = 0; i < count; i++) {
            this.entries[i] = this.getSlot();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.entries.length);
        for (int i = 0; i < this.entries.length; i++) {
            this.putUnsignedVarInt(i + 1);
            this.putSlot(entries[i], true);
        }
    }
}
