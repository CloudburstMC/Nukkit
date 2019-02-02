package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3;

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
