package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

@ToString
public class AnvilDamagePacket extends DataPacket {

    public byte damage;
    public int x;
    public int y;
    public int z;

    @Override
    public byte pid() {
        return ProtocolInfo.ANVIL_DAMAGE_PACKET;
    }

    @Override
    public void decode() {
        this.damage = this.getByte();
        BlockVector3 blockVector3 = this.getBlockVector3();
        this.x = blockVector3.getX();
        this.y = blockVector3.getY();
        this.z = blockVector3.getZ();
    }

    @Override
    public void encode() {
        this.putByte(this.damage);
        this.putBlockVector3(this.x, this.y, this.z);
    }
}
