package cn.nukkit.network.protocol;


import cn.nukkit.item.Item;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public class CraftingEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CRAFTING_EVENT_PACKET;

    public int windowId;
    public int type;
    public UUID id;

    public Item[] input;
    public Item[] output;

    @Override
    public void decode() {
        windowId = getByte();
        type = getVarInt();
        id = getUUID();

        int inputSize = (int) getUnsignedVarInt();
        input = new Item[inputSize];
        for (int i = 0; i < inputSize && i < 128; ++i) {
            input[i] = getSlot();
        }

        int outputSize = (int) getUnsignedVarInt();
        output = new Item[outputSize];
        for (int i = 0; i < outputSize && i < 128; ++i) {
            output[i] = getSlot();
        }
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
