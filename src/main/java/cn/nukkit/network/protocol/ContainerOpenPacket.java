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

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int windowId;
    public int type;
    public int x;
    public int y;
    public int z;
    public long entityId = -1;

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.type = this.getByte();
        BlockVector3 v = this.getBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.entityId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowId);
        this.putByte((byte) this.type);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putEntityUniqueId(this.entityId);
    }
}
