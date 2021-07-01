package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BinaryStream;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
public class CraftingEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CRAFTING_EVENT_PACKET;

    public static final int TYPE_SHAPELESS = 0;
    public static final int TYPE_SHAPED = 1;
    public static final int TYPE_FURNACE = 2;
    public static final int TYPE_FURNACE_DATA = 3;
    public static final int TYPE_MULTI = 4;
    public static final int TYPE_SHULKER_BOX = 5;

    public int windowId;
    public int type;
    public UUID id;

    public Item[] input;
    public Item[] output;

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.type = (int) this.getUnsignedVarInt();
        this.id = this.getUUID();

        this.input = this.getArray(Item.class, BinaryStream::getSlot);
        this.output = this.getArray(Item.class, BinaryStream::getSlot);
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
