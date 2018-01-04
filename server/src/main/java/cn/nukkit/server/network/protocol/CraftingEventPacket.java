package cn.nukkit.server.network.protocol;


import cn.nukkit.server.item.Item;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public class CraftingEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CRAFTING_EVENT_PACKET;

    public int windowId;
    public CraftingEventType type;
    public UUID id;

    public Item[] input;
    public Item[] output;

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.type = CraftingEventType.values()[this.getVarInt()];
        this.id = this.getUUID();

        int inputSize = (int) this.getUnsignedVarInt();
        this.input = new Item[inputSize];
        for (int i = 0; i < inputSize && i < 128; ++i) {
            this.input[i] = this.getSlot();
        }

        int outputSize = (int) this.getUnsignedVarInt();
        this.output = new Item[outputSize];
        for (int i = 0; i < outputSize && i < 128; ++i) {
            this.output[i] = getSlot();
        }
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public enum CraftingEventType {
        SHAPELESS,
        SHAPED,
        FURNACE,
        FURNACE_DATA,
        MULTI,
        SHULKER_BOX
    }
}
