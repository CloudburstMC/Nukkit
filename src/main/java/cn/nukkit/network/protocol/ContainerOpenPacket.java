package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.math.BlockVector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ContainerOpenPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.CONTAINER_OPEN_PACKET :
                ProtocolInfo.CONTAINER_OPEN_PACKET;
    }

    public int windowId;
    public int type;
    public int x;
    public int y;
    public int z;
    public long entityId = -1;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.windowId = this.getByte();
        this.type = this.getByte();
        BlockVector3 v = this.getBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.entityId = this.getEntityUniqueId();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putByte((byte) this.windowId);
        this.putByte((byte) this.type);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putEntityUniqueId(this.entityId);
    }
}
