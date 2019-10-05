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
public class MobArmorEquipmentPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public long eid;
    public Item[] slots = new Item[4];

    @Override
    protected void decode(ByteBuf buffer) {
        this.eid = Binary.readEntityRuntimeId(buffer);
        this.slots = new Item[4];
        this.slots[0] = Binary.readItem(buffer);
        this.slots[1] = Binary.readItem(buffer);
        this.slots[2] = Binary.readItem(buffer);
        this.slots[3] = Binary.readItem(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.eid);
        Binary.writeItem(buffer, this.slots[0]);
        Binary.writeItem(buffer, this.slots[1]);
        Binary.writeItem(buffer, this.slots[2]);
        Binary.writeItem(buffer, this.slots[3]);
    }
}
