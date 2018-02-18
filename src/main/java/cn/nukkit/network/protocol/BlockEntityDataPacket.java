package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntityDataPacket extends DataPacket {

    public int x;
    public int y;
    public int z;
    public byte[] namedTag;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("BLOCK_ENTITY_DATA_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        BlockVector3 v = this.getBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.namedTag = this.get();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putBlockVector3(this.x, this.y, this.z);
        this.put(this.namedTag);
    }
}