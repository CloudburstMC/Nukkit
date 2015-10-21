package cn.nukkit.network.protocol;

/**
 * Created on 15-10-21.
 */
public class ExplodePacket extends DataPacket {

    public static final byte NETWORK_ID = Info.EXPLODE_PACKET;

    public Record[] records = Record.EMPTY;

    public float x;
    public float y;
    public float z;

    public float radius;

    @Override
    public DataPacket clean() {
        records = Record.EMPTY;
        return super.clean();
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putFloat(x);
        putFloat(y);
        putFloat(z);
        putFloat(radius);
        putInt(records.length);
        if (records.length != 0) for (Record record : records) {
            putByte(record.x);
            putByte(record.y);
            putByte(record.z);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static class Record {

        public static final Record[] EMPTY = new Record[]{};

        public byte x;
        public byte y;
        public byte z;

        public Record(byte x, byte y, byte z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

    }

}
