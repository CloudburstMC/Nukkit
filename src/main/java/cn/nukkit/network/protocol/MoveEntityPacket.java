package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MoveEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.MOVE_ENTITY_PACKET;

    public long eid;
    public double x;
    public double y;
    public double z;
    public double yaw;
    public double headYaw;
    public double pitch;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.eid = this.getLong();
        this.x = this.getFloat();
        this.y = this.getFloat();
        this.z = this.getFloat();
        this.pitch = this.getByte() * (360d / 256d);
        this.yaw = this.getByte() * (360d / 256d);
        this.headYaw = this.getByte() * (360d / 256d);
    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(this.eid);
        this.putFloat((float) this.x);
        this.putFloat((float) this.y);
        this.putFloat((float) this.z);
        this.putByte((byte) (this.pitch / (360d / 256d)));
        this.putByte((byte) (this.headYaw / (360d / 256d)));
        this.putByte((byte) (this.yaw / (360d / 256d)));
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
