package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.List;
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

        List<Item.CreativeItemGroup> groups = creativeItems.getGroups();
        this.putUnsignedVarInt(groups.size());
        for (Item.CreativeItemGroup group : groups) {
            this.putByte((byte) group.getCategory().ordinal());
            this.putString(group.getName());
            this.putSlot(group.getIcon(), true);
        }

        int creativeNetId = 1; // 0 is not indexed by client

        Map<Item, Item.CreativeItemGroup> contents = creativeItems.getContents();
        this.putUnsignedVarInt(contents.size());
        for (Map.Entry<Item, Item.CreativeItemGroup> entry : contents.entrySet()) {
            this.putUnsignedVarInt(creativeNetId++);
            this.putSlot(entry.getKey(), true);
            this.putUnsignedVarInt(entry.getValue().getGroupId());
        }
    }
}
