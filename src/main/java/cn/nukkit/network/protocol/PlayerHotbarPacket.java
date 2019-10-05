package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class PlayerHotbarPacket extends DataPacket {

    public int selectedHotbarSlot;
    public int windowId = ContainerIds.INVENTORY;

    public boolean selectHotbarSlot = true;

    @Override
    public short pid() {
        return ProtocolInfo.PLAYER_HOTBAR_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.selectedHotbarSlot = (int) Binary.readUnsignedVarInt(buffer);
        this.windowId = buffer.readByte();
        this.selectHotbarSlot = buffer.readBoolean();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUnsignedVarInt(buffer, this.selectedHotbarSlot);
        buffer.writeByte((byte) this.windowId);
        buffer.writeBoolean(this.selectHotbarSlot);
    }
}
