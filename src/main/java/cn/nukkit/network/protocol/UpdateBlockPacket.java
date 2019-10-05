package cn.nukkit.network.protocol;


import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class UpdateBlockPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.UPDATE_BLOCK_PACKET;

    public static final int FLAG_NONE = 0b0000;
    public static final int FLAG_NEIGHBORS = 0b0001;
    public static final int FLAG_NETWORK = 0b0010;
    public static final int FLAG_NOGRAPHIC = 0b0100;
    public static final int FLAG_PRIORITY = 0b1000;

    public static final int FLAG_ALL = (FLAG_NEIGHBORS | FLAG_NETWORK);
    public static final int FLAG_ALL_PRIORITY = (FLAG_ALL | FLAG_PRIORITY);

    public int x;
    public int z;
    public int y;
    public int blockRuntimeId;
    public int flags;
    public int dataLayer = 0;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeBlockVector3(buffer, x, y, z);
        Binary.writeUnsignedVarInt(buffer, blockRuntimeId);
        Binary.writeUnsignedVarInt(buffer, flags);
        Binary.writeUnsignedVarInt(buffer, dataLayer);
    }

    public static class Entry {
        public final int x;
        public final int z;
        public final int y;
        public final int blockId;
        public final int blockData;
        public final int flags;

        public Entry(int x, int z, int y, int blockId, int blockData, int flags) {
            this.x = x;
            this.z = z;
            this.y = y;
            this.blockId = blockId;
            this.blockData = blockData;
            this.flags = flags;
        }
    }
}
