package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetEntityMotionPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.SET_ENTITY_MOTION_PACKET;

    // eid, motX, motY, motZ
    public double[][] entities = new double[0][];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public DataPacket clean() {
        this.entities = new double[0][];
        return super.clean();
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.entities.length);
        for (double[] d : this.entities) {
            this.putLong((long) d[0]);
            this.putFloat((float) d[1]);
            this.putFloat((float) d[2]);
            this.putFloat((float) d[3]);
        }
    }

}
