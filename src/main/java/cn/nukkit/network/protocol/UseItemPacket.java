package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * @author Nukkit Project Team
 */
public class UseItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.USE_ITEM_PACKET;

    public int x;
    public int y;
    public int z;

    public int face;

    public float fx;
    public float fy;
    public float fz;

    public float posX;
    public float posY;
    public float posZ;

    public int slot;

    public Item item;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.x = this.getInt();
        this.y = this.getInt();
        this.z = this.getInt();
        this.face = this.getByte();
        this.fx = this.getFloat();
        this.fy = this.getFloat();
        this.fz = this.getFloat();
        this.posX = this.getFloat();
        this.posY = this.getFloat();
        this.posZ = this.getFloat();
        this.slot = this.getInt();
        this.item = this.getSlot();
    }

    @Override
    public void encode() {

    }

}
