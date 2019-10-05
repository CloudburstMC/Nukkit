package cn.nukkit.network.protocol;


import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
public class CraftingEventPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.CRAFTING_EVENT_PACKET;

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
    protected void decode(ByteBuf buffer) {
        this.windowId = buffer.readByte();
        this.type = Binary.readVarInt(buffer);
        this.id = Binary.readUuid(buffer);

        int inputSize = (int) Binary.readUnsignedVarInt(buffer);
        this.input = new Item[inputSize];
        for (int i = 0; i < inputSize && i < 128; ++i) {
            this.input[i] = Binary.readItem(buffer);
        }

        int outputSize = (int) Binary.readUnsignedVarInt(buffer);
        this.output = new Item[outputSize];
        for (int i = 0; i < outputSize && i < 128; ++i) {
            this.output[i] = Binary.readItem(buffer);
        }
    }

    @Override
    protected void encode(ByteBuf buffer) {

    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
