package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MobEquipmentPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.MOB_EQUIPMENT_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public long eid;
    public Item item;
    public int inventorySlot;
    public int hotbarSlot;
    public int windowId;

    @Override
    protected void decode(ByteBuf buffer) {
        this.eid = Binary.readEntityRuntimeId(buffer); //EntityRuntimeID
        this.item = Binary.readItem(buffer);
        this.inventorySlot = buffer.readByte();
        this.hotbarSlot = buffer.readByte();
        this.windowId = buffer.readByte();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.eid); //EntityRuntimeID
        Binary.writeItem(buffer, this.item);
        buffer.writeByte((byte) this.inventorySlot);
        buffer.writeByte((byte) this.hotbarSlot);
        buffer.writeByte((byte) this.windowId);
    }
}
