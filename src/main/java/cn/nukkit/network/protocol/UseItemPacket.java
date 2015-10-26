package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * @author Nukkit Project Team
 */
public class UseItemPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.USE_ITEM_PACKET;

    public int x;
    public int y;
    public int z;

    public byte face;

    public float fx;
    public float fy;
    public float fz;

    public float posX;
    public float posY;
    public float posZ;

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
        this.face = (byte) getByte();
        this.fx = this.getFloat();
        this.fy = this.getFloat();
        this.fz = this.getFloat();
        this.posX = this.getFloat();
        this.posY = this.getFloat();
        this.posZ = this.getFloat();

        this.item = this.getSlot();
    }

    @Override
    public void encode() {

    }

}
