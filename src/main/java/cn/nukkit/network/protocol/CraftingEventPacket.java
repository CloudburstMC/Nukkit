package cn.nukkit.network.protocol;


import cn.nukkit.item.Item;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public class CraftingEventPacket extends DataPacket {

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
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("CRAFTING_EVENT_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.windowId = this.getByte();
        switch(protocol.getMainNumber()){
            case 130:
            default:
                this.type = this.getVarInt();
                break;
            case 113:
                this.type = (int) this.getUnsignedVarInt();
                break;
        }
        this.id = this.getUUID(protocol);

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
    public void encode(PlayerProtocol protocol) {

    }

}
