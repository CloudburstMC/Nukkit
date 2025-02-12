package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.Map;

@ToString
public class CreativeContentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CREATIVE_CONTENT_PACKET;

    public Item.CreativeItems creativeItems;

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

        if (this.creativeItems == null) { // Spectator
            this.putUnsignedVarInt(0);
            this.putUnsignedVarInt(0);
            return;
        }

        this.putUnsignedVarInt(creativeItems.getGroups().size());
        for (Item.CreativeItemGroup group : creativeItems.getGroups()) {
            this.putLInt(group.getCategory().ordinal());
            this.putString(group.getName());
            this.putSlot(group.getIcon(), true);
        }

        int creativeNetId = 1; // 0 is not indexed by client

        this.putUnsignedVarInt(creativeItems.getContents().size());
        for (Map.Entry<Item, Item.CreativeItemGroup> entry : creativeItems.getContents().entrySet()) {
            this.putUnsignedVarInt(creativeNetId++);
            this.putSlot(entry.getKey(), true);
            this.putUnsignedVarInt(entry.getValue().getGroupId());
        }
    }
}
