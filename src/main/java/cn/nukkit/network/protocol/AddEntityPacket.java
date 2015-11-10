package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.entries.EntityDataEntry;
import cn.nukkit.utils.Binary;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AddEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADD_ENTITY_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public int type;
    public float x;
    public float y;
    public float z;
    public float speedX;
    public float speedY;
    public float speedZ;
    public float yaw;
    public float pitch;
    public Map<Integer, EntityDataEntry> metadata;
    public Object[][] links = new Object[0][3];

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(this.eid);
        this.putInt(this.type);
        this.putFloat(this.x);
        this.putFloat(this.y);
        this.putFloat(this.z);
        this.putFloat(this.speedX);
        this.putFloat(this.speedY);
        this.putFloat(this.speedZ);
        this.putFloat(this.yaw);
        this.putFloat(this.pitch);
        this.put(Binary.writeMetadata(this.metadata));
        this.putShort(this.links.length);
        for (Object[] link : links) {
            this.putLong((Long) link[0]);
            this.putLong((Long) link[1]);
            this.putByte((Byte) link[2]);
        }
    }
}
