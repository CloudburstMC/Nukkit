package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
public class CraftingEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CRAFTING_EVENT_PACKET;

    public byte windowId;
    public Type type;
    public UUID uuid;
    public Item[] input = new Item[0];
    public Item[] output = new Item[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.windowId = (byte) this.getByte();
        this.type = Type.values()[this.getVarInt()];
        this.uuid = this.getUUID();
        int count = (int) this.getUnsignedVarInt();
        for (int i = 0; i < count && i < 128; i++) {
            this.input[i] = this.getSlot();
        }
        count = (int) this.getUnsignedVarInt();
        for (int i = 0; i < count && i < 128; i++) {
            this.output[i] = this.getSlot();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putVarInt(this.type.ordinal());
        this.putUUID(this.uuid);
        this.putUnsignedVarInt(this.input.length);
        for (Item item : this.input) {
            this.putSlot(item);
        }
        this.putUnsignedVarInt(this.output.length);
        for (Item item : this.output) {
            this.putSlot(item);
        }
    }

    public static enum Type {

        SHAPELESS,
        SHAPED,
        FURNACE,
        FURNACE_DATA,
        MULTI,
        SHULKER_BOX
    }
}
