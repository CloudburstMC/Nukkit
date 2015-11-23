package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class UpdateBlockPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_BLOCK_PACKET;

    public static final int FLAG_NONE = 0b0000;
    public static final int FLAG_NEIGHBORS = 0b0001;
    public static final int FLAG_NETWORK = 0b0010;
    public static final int FLAG_NOGRAPHIC = 0b0100;
    public static final int FLAG_PRIORITY = 0b1000;

    public static final int FLAG_ALL = (FLAG_NEIGHBORS | FLAG_NETWORK);
    public static final int FLAG_ALL_PRIORITY = (FLAG_ALL | FLAG_PRIORITY);

    public Entry[] records = new Entry[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.records.length);
        for (Entry entry : this.records) {
            this.putInt(entry.x);
            this.putInt(entry.z);
            this.putByte((byte) entry.y);
            this.putByte((byte) entry.blockId);
            this.putByte((byte) ((entry.flags << 4) | entry.blockData));
        }
    }

    public static class Entry {
        public int x;
        public int z;
        public int y;
        public int blockId;
        public int blockData;
        public int flags;

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
