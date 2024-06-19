package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

@ToString
public class CreativeContentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CREATIVE_CONTENT_PACKET;

    public Item[] entries;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(entries.length);
        int i = 1; //HACK around since 0 is not indexed by client
        for (Item entry : entries) {
            this.putUnsignedVarInt(i++);
            this.putSlot(entry, true);
        }
    }
}
