package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.ToString;

@Since("1.3.0.0-PN")
@ToString
public class CreativeContentPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CREATIVE_CONTENT_PACKET;

    @Since("1.3.0.0-PN") public Item[] entries = Item.EMPTY_ARRAY;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(entries.length);
        for (int i = 0; i < entries.length; i++) {
            this.putUnsignedVarInt(i + 1);
            this.putSlot(entries[i], true);
        }
    }
}
