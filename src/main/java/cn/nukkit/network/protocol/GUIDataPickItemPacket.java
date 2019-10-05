package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class GUIDataPickItemPacket extends DataPacket {

    public int hotbarSlot;

    @Override
    public short pid() {
        return ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET;
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeIntLE(this.hotbarSlot);
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.hotbarSlot = buffer.readIntLE();
    }
}
