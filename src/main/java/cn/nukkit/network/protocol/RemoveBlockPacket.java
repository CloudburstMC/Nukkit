package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

public class RemoveBlockPacket extends DataPacket {

    public int x;
    public int y;
    public int z;

    @Override
    public void decode(PlayerProtocol protocol) {
        BlockVector3 v = this.getBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    @Override
    public void encode(PlayerProtocol protocol) {
    }

    @Override
    public byte pid(PlayerProtocol protocol){
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.REMOVE_BLOCK_PACKET : 0;
    }

}
