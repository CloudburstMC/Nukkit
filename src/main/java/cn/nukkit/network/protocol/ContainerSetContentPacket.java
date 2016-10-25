package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ContainerSetContentPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_SET_CONTENT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static final byte SPECIAL_INVENTORY = 0;
    public static final byte SPECIAL_ARMOR = 0x78;
    public static final byte SPECIAL_CREATIVE = 0x79;
    public static final byte SPECIAL_HOTBAR = 0x7a;

    public int windowid;
    public Item[] slots = new Item[0];
    public int[] hotbar = new int[0];

    @Override
    public DataPacket clean() {
        this.slots = new Item[0];
        this.hotbar = new int[0];
        return super.clean();
    }

    @Override
    public void decode() {
        this.windowid = this.getByte();
        int count = (int) this.getUnsignedVarInt();
        this.slots = new Item[count];

        for (int s = 0; s < count && !this.feof(); ++s) {
            this.slots[s] = this.getSlot();
        }

        if (this.windowid == SPECIAL_INVENTORY) {
            count = (int) this.getUnsignedVarInt();
            this.hotbar = new int[count];
            for (int s = 0; s < count && !this.feof(); ++s) {
                this.hotbar[s] = this.getVarInt();
            }
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowid);
        this.putUnsignedVarInt(this.slots.length);
        for (Item slot : this.slots) {
            this.putSlot(slot);
        }

        if (this.windowid == SPECIAL_INVENTORY && this.hotbar.length > 0) {
            this.putUnsignedVarInt(this.hotbar.length);
            for (int slot : this.hotbar) {
                this.putVarInt(slot);
            }
        } else {
            this.putShort(0);
        }
    }

    @Override
    public ContainerSetContentPacket clone() {
        ContainerSetContentPacket pk = (ContainerSetContentPacket) super.clone();
        pk.slots = this.slots.clone();
        return pk;
    }
}
