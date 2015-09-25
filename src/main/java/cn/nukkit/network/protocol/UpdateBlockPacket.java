package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class UpdateBlockPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.UPDATE_BLOCK_PACKET;

    public static final int FLAG_NONE = 0b0000;
    public static final int FLAG_NEIGHBORS = 0b0001;
    public static final int FLAG_NETWORK = 0b0010;
    public static final int FLAG_NOGRAPHIC = 0b0100;
    public static final int FLAG_PRIORITY = 0b1000;

    public static final int FLAG_ALL = (FLAG_NEIGHBORS | FLAG_NETWORK);
    public static final int FLAG_ALL_PRIORITY = (FLAG_ALL | FLAG_PRIORITY);

    public int[][] records = new int[0][6];

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
        for (int[] r : this.records) {
            this.putInt(r[0]);
            this.putInt(r[1]);
            this.putByte((byte) (r[2] & 0xff));
            this.putByte((byte) (r[3] & 0xff));
            this.putByte((byte) (((r[5] << 4) | r[4]) & 0xff));
        }
    }

}
