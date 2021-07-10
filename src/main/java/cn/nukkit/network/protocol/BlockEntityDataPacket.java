package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = "namedTag")
public class BlockEntityDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BLOCK_ENTITY_DATA_PACKET;

    public int x;
    public int y;
    public int z;
    public CompoundTag namedTag;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        BlockVector3 blockVector3 = this.getBlockVector3();
        this.x = blockVector3.getX();
        this.y = blockVector3.getY();
        this.z = blockVector3.getZ();
        this.namedTag = this.getCompoundTag();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(this.x, this.y, this.z);
        this.putCompoundTag(this.namedTag);
    }
}