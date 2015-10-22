package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ContainerSetContentPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.CONTAINER_SET_CONTENT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static final byte SPECIAL_INVENTORY = 0;
    public static final byte SPECIAL_ARMOR = 0x78;
    public static final byte SPECIAL_CREATIVE = 0x79;
    public static final byte SPECIAL_CRAFTING = 0x7a;

    public int windowId;
    public Item[] slots = new Item[0];
    public int[] hotBar = new int[0];

    @Override
    public DataPacket clean() {
        this.slots = new Item[0];
        this.hotBar = new int[0];
        return super.clean();
    }

    @Override
    public void decode() {
        this.windowId = this.getByte();
        int count = this.getShort();
        this.slots = new Item[count];

        for (int s = 0; s < count && !this.feof(); ++s) {
            this.slots[s] = this.getSlot();
        }

        if (this.windowId == SPECIAL_INVENTORY) {
            count = this.getShort();
            this.hotBar = new int[count];
            for (int s = 0; s < count && !this.feof(); ++s) {
                this.hotBar[s] = this.getInt();
            }
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putShort((short) this.slots.length);
        for (Item slot : this.slots) {
            this.putSlot(slot);
        }

        if (this.windowId == SPECIAL_INVENTORY && this.hotBar.length > 0) {
            this.putShort((short) this.hotBar.length);
            for (int slot : this.hotBar) {
                this.putInt(slot);
            }
        } else {
            this.putShort((short) 0);
        }
    }

    @Override
    public ContainerSetContentPacket clone() {
        ContainerSetContentPacket pk = (ContainerSetContentPacket) super.clone();
        pk.slots = this.slots.clone();
        return pk;
    }
}
