package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ContainerSetSlotPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_SET_SLOT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int windowid;
    public int slot;
    public int hotbarSlot;
    public Item item;

    @Override
    public void decode() {
        this.windowid = this.getByte();
        this.slot = this.getShort();
        this.hotbarSlot = this.getShort();
        this.item = this.getSlot();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowid);
        this.putShort(this.slot);
        this.putShort(this.hotbarSlot);
        this.putSlot(this.item);
    }
}
