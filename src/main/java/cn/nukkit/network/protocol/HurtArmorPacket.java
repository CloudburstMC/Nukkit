package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class HurtArmorPacket extends DataPacket {

    public int cause;
    public int damage;

    @Override
    public byte pid() {
        return ProtocolInfo.HURT_ARMOR_PACKET;
    }

    @Override
    public void decode() {
        this.cause = this.getVarInt();
        this.damage = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.cause);
        this.putVarInt(this.damage);
    }
}
