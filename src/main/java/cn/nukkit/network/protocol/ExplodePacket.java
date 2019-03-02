package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ExplodePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.EXPLODE_PACKET;

    public float x;
    public float y;
    public float z;
    public float radius;
    public Vector3[] records = new Vector3[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public DataPacket clean() {
        this.records = new Vector3[0];
        return super.clean();
    }

    @Override
    public void decode() {
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.radius = this.getVarInt() / 32;
        this.records = new Vector3[(int) this.getUnsignedVarInt()];
        for(int i = 0; i < this.records.length; i++) {
            this.records[i] = this.getSignedBlockPosition().asVector3();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putVector3f(this.x, this.y, this.z);
        this.putVarInt((int) (this.radius * 32));
        this.putUnsignedVarInt(this.records.length);
        if (this.records.length > 0) {
            for (Vector3 record : records) {
                this.putSignedBlockPosition(record.asBlockVector3());
            }
        }
    }

}
