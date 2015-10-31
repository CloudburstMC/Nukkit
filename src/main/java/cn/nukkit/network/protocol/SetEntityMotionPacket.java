package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetEntityMotionPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.SET_ENTITY_MOTION_PACKET;

    // eid, motX, motY, motZ
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
        this.putInt(this.entities.length);
        for (Entry entry : this.entities) {
            this.putLong(entry.entityId);
            this.putFloat(entry.motionX);
            this.putFloat(entry.motionY);
            this.putFloat(entry.motionZ);
        }
    }

   public static class Entry {
        public long entityId;
        public float motionX;
        public float motionY;
        public float motionZ;

        public Entry(long entityId, float motionX, float motionY, float motionZ) {
            this.entityId = entityId;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
        }
    }

}
