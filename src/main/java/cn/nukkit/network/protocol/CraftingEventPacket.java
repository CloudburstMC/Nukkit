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

    public static final int TYPE_SHAPELESS = 0;
    public static final int TYPE_SHAPED = 1;
    public static final int TYPE_FURNACE = 2;
    public static final int TYPE_FURNACE_DATA = 3;
    public static final int TYPE_MULTI = 4;
    public static final int TYPE_SHULKER_BOX = 5;

    public byte windowId;
    public int type;
    public UUID id;
    public Item[] input = new Item[0];
    public Item[] output = new Item[0];

    @Override
    public byte pid() {
        return ProtocolInfo.CRAFTING_EVENT_PACKET;
    }

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.type = this.getVarInt();
        this.id = this.getUUID();
        this.input = this.getArray(Item.class, BinaryStream::getSlot);
        this.output = this.getArray(Item.class, BinaryStream::getSlot);
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putVarInt(this.type);
        this.putUUID(this.id);
        this.putUnsignedVarInt(this.input.length);
        for (Item item : this.input) {
            this.putSlot(item);
        }
        this.putUnsignedVarInt(this.output.length);
        for (Item item : this.output) {
            this.putSlot(item);
        }
    }
}
