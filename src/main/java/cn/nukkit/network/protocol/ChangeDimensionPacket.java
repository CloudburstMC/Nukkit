package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.network.protocol in project nukkit .
 */
@ToString
public class ChangeDimensionPacket extends DataPacket {

    public int dimensionId;
    public Vector3f position;
    public boolean respawn = false;

    @Override
    public byte pid() {
        return ProtocolInfo.CHANGE_DIMENSION_PACKET;
    }

    @Override
    public void decode() {
        this.dimensionId = this.getVarInt();
        this.position = this.getVector3f();
        this.respawn = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.dimension);
        this.putVector3f(this.position);
        this.putBoolean(this.respawn);
    }
}
