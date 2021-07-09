package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ContainerOpenPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_OPEN_PACKET;

    public byte windowId;
    public byte type;
    public int x;
    public int y;
    public int z;
    public long entityUniqueId = -1;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.type = this.getByte();
        BlockVector3 blockVector3 = this.getBlockVector3();
        this.x = blockVector3.getX();
        this.y = blockVector3.getY();
        this.z = blockVector3.getZ();
        this.entityUniqueId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putByte(this.type);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putEntityUniqueId(this.entityUniqueId);
    }
}
