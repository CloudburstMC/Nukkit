package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
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

        int inputSize = (int) this.getUnsignedVarInt();
        List<Item> input = new ArrayList<>();
        for (int i = 0; i < inputSize; ++i) {
            input.add(this.getSlot());
        }
        this.input = input.toArray(new Item[0]);

        int outputSize = (int) this.getUnsignedVarInt();
        List<Item> output = new ArrayList<>();
        for (int i = 0; i < outputSize; ++i) {
            output.add(this.getSlot());
        }
        this.output = output.toArray(new Item[0]);
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
