package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class UpdateBlockPacket extends DataPacket {

    public static final int FLAG_NONE = 0b0000;
    public static final int FLAG_NEIGHBORS = 0b0001;
    public static final int FLAG_NETWORK = 0b0010;
    public static final int FLAG_NOGRAPHIC = 0b0100;
    public static final int FLAG_PRIORITY = 0b1000;

    public static final int FLAG_ALL = (FLAG_NEIGHBORS | FLAG_NETWORK);
    public static final int FLAG_ALL_PRIORITY = (FLAG_ALL | FLAG_PRIORITY);

    public static final int DATA_LAYER_NORMAL = 0;
    public static final int DATA_LAYER_LIQUID = 1;

    public int x;
    public int y;
    public int z;
    public int blockRuntimeId;
    public int flags;
    public int dataLayer = DATA_LAYER_NORMAL;

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_BLOCK_PACKET;
    }

    @Override
    public void decode() {
        BlockVector3 blockVector3 = this.getBlockVector3();
        this.x = blockVector3.getX();
        this.y = blockVector3.getY();
        this.z = blockVector3.getZ();
        this.blockRuntimeId = this.getUnsignedVarInt();
        this.flags = this.getUnsignedVarInt();
        this.dataLayer = this.getUnsignedVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(this.x, this.y, this.z);
        this.putUnsignedVarInt(this.blockRuntimeId);
        this.putUnsignedVarInt(this.flags);
        this.putUnsignedVarInt(this.dataLayer);
    }

    public static class Entry {
        public final int x;
        public final int y;
        public final int z;
        public final int blockId;
        public final int blockData;
        public final int flags;

        public Entry(int x, int y, int z, int blockId, int blockData, int flags) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockId = blockId;
            this.blockData = blockData;
            this.flags = flags;
        }
    }
}
