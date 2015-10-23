package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * @author Nukkit Project Team
 */
public class ContainerSetSlotPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.CONTAINER_SET_SLOT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int windowId;
    public int slot;
    public Item item;

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.slot = this.getShort();
        this.item = this.getSlot();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putShort(this.slot);
        this.putSlot(this.item);
    }
}
