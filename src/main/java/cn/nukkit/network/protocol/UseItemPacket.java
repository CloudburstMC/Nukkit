package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockVector3;

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
        BlockVector3 v = this.getBlockCoords();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.face = this.getVarInt();
        this.putVector3f(fx, fy, fz);
        this.putVector3f(posX, posY, posZ);
        this.slot = this.getVarInt();
        this.item = this.getSlot();
    }

    @Override
    public void encode() {

    }

}
