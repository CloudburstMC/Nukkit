package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MoveEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.MOVE_ENTITY_PACKET;

    // eid, x, y, z, yaw, pitch
    public Entry[] entities = new Entry[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public DataPacket clean() {
        this.entities = new Entry[0];
        return super.clean();
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        for (Entry entry : this.entities) {
            this.putLong(entry.eid);
            this.putFloat((float) entry.x);
            this.putFloat((float) entry.y);
            this.putFloat((float) entry.z);
            this.putByte((byte) (entry.pitch * 0.71));
            this.putByte((byte) (entry.headyaw * 0.71));
            this.putByte((byte) (entry.yaw * 0.71));
        }
    }

    public static class Entry {
        public long eid;
        public double x;
        public double y;
        public double z;
        public double yaw;
        public double headyaw;
        public double pitch;

        public Entry(long eid, double x, double y, double z, double yaw, double headyaw, double pitch) {
            this.eid = eid;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.headyaw = headyaw;
            this.pitch = pitch;
        }
    }
}
