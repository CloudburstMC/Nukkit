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
    public void decode() {
        x = getInt();
        y = getInt();
        z = getInt();
        face = (byte) getByte();
        fx = getFloat();
        fy = getFloat();
        fz = getFloat();
        posX = getFloat();
        posY = getFloat();
        posZ = getFloat();
        item = getSlot();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
    
}
